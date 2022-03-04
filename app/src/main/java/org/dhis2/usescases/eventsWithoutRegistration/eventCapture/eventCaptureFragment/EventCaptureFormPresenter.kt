package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment

import io.reactivex.disposables.CompositeDisposable
import org.dhis2.commons.schedulers.SchedulerProvider
import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationView
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationViewModel
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.intent.FormIntent
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.usescases.biometrics.isBiometricModel
import org.dhis2.usescases.biometrics.isBiometricsVerificationModel
import org.dhis2.usescases.biometrics.isBiometricsVerificationText
import org.dhis2.usescases.biometrics.isLastVerificationValid
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureContract
import org.jetbrains.annotations.Nullable
import timber.log.Timber

class EventCaptureFormPresenter(
    val view: EventCaptureFormView,
    private val activityPresenter: EventCaptureContract.Presenter,
    val schedulerProvider: SchedulerProvider
) {
    private var finishing: Boolean = false
    private var selectedSection: String? = null

    var disposable: CompositeDisposable = CompositeDisposable()

    private var biometricsVerificationStatus: Int = -1
    private var biometricsGuid: String? = null
    private var teiOrgUnit: String? = null
    private var fields: List<FieldUiModel> = listOf()
    private var biometricsVerificationViewModel: BiometricsVerificationViewModel? = null

    fun init() {
        disposable.add(
            activityPresenter.formFieldsFlowable()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { fields ->

                        val updatedFields = updateBiometricsField(fields)
                        populateList(updatedFields)
                        this.fields = fields

                        if (BIOMETRICS_ENABLED) {
                            biometricsVerificationViewModel = updatedFields.firstOrNull {
                                it.isBiometricsVerificationModel()
                            }?.let { it as BiometricsVerificationViewModel }

                            biometricsVerificationViewModel?.setBiometricsRetryListener {
                                view.verifyBiometrics(biometricsGuid, teiOrgUnit)
                            }
                        }
                    },
                    { Timber.e(it) }
                )
        )
    }

    private fun populateList(fields: List<FieldUiModel>) {
        if (BIOMETRICS_ENABLED) {
            launchBiometricsVerificationIfRequired(fields)
        }

        checkFinishing()
        view.showFields(fields)
        activityPresenter.hideProgress()

        selectedSection ?: fields
            .mapNotNull { it.programStageSection }
            .firstOrNull()
            .let { selectedSection = it }
    }

    private fun launchBiometricsVerificationIfRequired(fields: List<FieldUiModel>) {
        val biometricsViewModel =
            fields.firstOrNull { it.isBiometricsVerificationModel() }

        if (biometricsViewModel != null &&
            this.biometricsGuid != null &&
            (biometricsViewModel as BiometricsVerificationViewModel).status() ==
            BiometricsVerificationView.BiometricsVerificationStatus.NOT_DONE
        ) {
            val lastUpdated = activityPresenter.getBiometricsAttributeValueInTeiLastUpdated(
                biometricsViewModel.uid()
            )

            if (!isLastVerificationValid(
                    lastUpdated,
                    activityPresenter.lastBiometricsVerificationDuration,
                    true
                )
            ) {
                view.verifyBiometrics(this.biometricsGuid, this.teiOrgUnit)
            } else {
                refreshBiometricsVerificationStatus(1, false)
            }
        }
    }

    private fun updateBiometricsField(fields: List<FieldUiModel>?): MutableList<FieldUiModel> {
        return fields?.map {
            if (it.label.isBiometricsVerificationText()
            ) {
                val biometricsViewModel = it as BiometricsVerificationViewModel
                val status = mapVerificationStatus(biometricsVerificationStatus)
                biometricsViewModel.withValueAndStatus(biometricsGuid, status)
            } else {
                it
            }
        } as MutableList<FieldUiModel>
    }

    private fun checkFinishing() {
        if (finishing) {
            view.performSaveClick()
        }
        finishing = false
    }

    fun onDetach() {
        disposable.clear()
    }

    fun onActionButtonClick() {
        activityPresenter.attemptFinish()
    }

    private fun mapVerificationStatus(biometricsVerificationStatus: Int): BiometricsVerificationView.BiometricsVerificationStatus {
        return when (biometricsVerificationStatus) {
            0 -> BiometricsVerificationView.BiometricsVerificationStatus.FAILURE
            1 -> BiometricsVerificationView.BiometricsVerificationStatus.SUCCESS
            else -> BiometricsVerificationView.BiometricsVerificationStatus.NOT_DONE
        }
    }

    fun <E> Iterable<E>.updated(index: Int, elem: E): List<E> =
        mapIndexed { i, existing -> if (i == index) elem else existing }

    fun setFinishing() {
        finishing = true
    }

    fun initBiometricsValues(
        biometricsGuid: @Nullable String?,
        biometricsVerificationStatus: Int,
        teiOrgUnit: @Nullable String?
    ) {
        this.biometricsGuid = biometricsGuid
        this.biometricsVerificationStatus = biometricsVerificationStatus
        this.teiOrgUnit = teiOrgUnit
    }

    fun refreshBiometricsVerificationStatus(
        biometricsVerificationStatus: Int,
        saveValue: Boolean = true
    ) {
        if (biometricsVerificationViewModel != null) {
            activityPresenter.saveValue(biometricsVerificationViewModel!!.uid, biometricsGuid)
        }

        val status = mapVerificationStatus(biometricsVerificationStatus)

        if (status == BiometricsVerificationView.BiometricsVerificationStatus.SUCCESS && saveValue) {
            activityPresenter.updateBiometricsAttributeValueInTei(biometricsGuid)
        }

        this.biometricsVerificationStatus = biometricsVerificationStatus
        activityPresenter.refreshByBiometricsVerification(this.biometricsVerificationStatus)
    }
}
