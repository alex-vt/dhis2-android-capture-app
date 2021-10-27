package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment

import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.FlowableProcessor
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationView
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationViewModel
import org.dhis2.data.schedulers.SchedulerProvider
import org.dhis2.form.data.FormRepository
import org.dhis2.form.model.ActionType
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.RowAction
import org.dhis2.form.model.ValueStoreResult
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.usescases.biometrics.isBiometricsVerificationModel
import org.dhis2.usescases.biometrics.isBiometricsVerificationText
import org.dhis2.usescases.biometrics.isLastVerificationValid
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureContract
import org.jetbrains.annotations.Nullable
import timber.log.Timber

class EventCaptureFormPresenter(
    val view: EventCaptureFormView,
    private val activityPresenter: EventCaptureContract.Presenter,
    val schedulerProvider: SchedulerProvider,
    private val onFieldActionProcessor: FlowableProcessor<RowAction>,
    private val formRepository: FormRepository
) {
    private var finishing: Boolean = false
    private var selectedSection: String? = null

    var disposable: CompositeDisposable = CompositeDisposable()

    private var biometricsVerificationStatus: Int = -1
    private var biometricsGuid: String? = null
    private var teiOrgUnit: String? = null

    fun init() {
        disposable.add(
            onFieldActionProcessor.onBackpressureBuffer().distinctUntilChanged()
                .doOnNext { activityPresenter.showProgress() }
                .observeOn(schedulerProvider.io())
                .switchMap { rowAction ->
                    Flowable.just(formRepository.processUserAction(rowAction))
                }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { result ->
                        result.valueStoreResult?.let {
                            if (result.valueStoreResult == ValueStoreResult.VALUE_CHANGED
                            ) {
                                activityPresenter.setValueChanged(result.uid)
                                activityPresenter.nextCalculation(true)
                            } else {
                                populateList()

                                if (BIOMETRICS_ENABLED) {
                                    val biometricsViewModel = getBiometricVerificationViewModel()

                                    if (result.uid == biometricsViewModel?.uid) {
                                        view.verifyBiometrics(this.biometricsGuid, this.teiOrgUnit)
                                    }
                                }
                            }
                        } ?: activityPresenter.hideProgress()
                    },
                    Timber::e
                )
        )

        disposable.add(
            activityPresenter.formFieldsFlowable()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { fields ->
                        populateList(fields)
                    },
                    { Timber.e(it) }
                )
        )
    }

    private fun getBiometricVerificationViewModel(): BiometricsVerificationViewModel? {
        val viewModel = formRepository.composeList().toMutableList().firstOrNull {
            it.isBiometricsVerificationModel()
        }

        return if (viewModel == null) {
            null
        } else {
            viewModel as BiometricsVerificationViewModel
        }
    }

    private fun populateList(items: List<FieldUiModel>? = null) {
        val fields = formRepository.composeList(items).toMutableList()

        if (BIOMETRICS_ENABLED) {
            val updatedFields = updateBiometricsField(fields)
            view.showFields(updatedFields)

            launchBiometricsVerificationIfRequired(updatedFields)
        } else {
            view.showFields(fields)
        }

        checkFinishing(true)
        activityPresenter.hideProgress()
        if (items != null) {
            selectedSection ?: items
                .mapNotNull { it.programStageSection }
                .firstOrNull()
                .let { selectedSection = it }
        }
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


            if (!isLastVerificationValid(lastUpdated, activityPresenter.lastBiometricsVerificationDuration,true)) {
                view.verifyBiometrics(this.biometricsGuid, this.teiOrgUnit)
            } else {
                refreshBiometricsVerificationStatus(1,false)
            }
        }
    }

    private fun updateBiometricsField(fields: List<FieldUiModel>): MutableList<FieldUiModel> {
        return fields.map {
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

    private fun checkFinishing(canFinish: Boolean) {
        if (finishing && canFinish) {
            view.performSaveClick()
        }
        finishing = false
    }

    fun onDetach() {
        disposable.clear()
    }

    fun onActionButtonClick() {
        activityPresenter.attempFinish()
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

    private fun saveValue(uid: String, value: String?) {
        onFieldActionProcessor.onNext(
            RowAction(
                id = uid,
                value = value,
                type = ActionType.ON_SAVE
            )
        )
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
        saveValue: Boolean =true
    ) {
        val biometricsViewModel = getBiometricVerificationViewModel()

        if (biometricsViewModel != null) {
            saveValue(biometricsViewModel.uid, biometricsGuid)
        }

        val status = mapVerificationStatus(biometricsVerificationStatus)

        if (status == BiometricsVerificationView.BiometricsVerificationStatus.SUCCESS && saveValue) {
            activityPresenter.updateBiometricsAttributeValueInTei(biometricsGuid)
        }

        this.biometricsVerificationStatus = biometricsVerificationStatus
        activityPresenter.refreshByBiometricsVerification(this.biometricsVerificationStatus)
    }
}
