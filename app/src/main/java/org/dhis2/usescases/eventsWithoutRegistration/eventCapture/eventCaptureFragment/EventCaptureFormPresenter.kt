package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment

import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.FlowableProcessor
import org.dhis2.data.forms.dataentry.fields.biometrics.BiometricsViewModel
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationView
import org.dhis2.data.forms.dataentry.fields.biometricsVerification.BiometricsVerificationViewModel
import org.dhis2.data.schedulers.SchedulerProvider
import org.dhis2.form.data.FormRepository
import org.dhis2.form.model.ActionType
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.RowAction
import org.dhis2.form.model.ValueStoreResult
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.usescases.biometrics.isBiometricModel
import org.dhis2.usescases.biometrics.isBiometricsVerificationModel
import org.dhis2.usescases.biometrics.isBiometricsVerificationText
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

                                if (result.uid == getBiometricVerificationViewModel().uid) {
                                    view.verifyBiometrics(this.biometricsGuid, this.teiOrgUnit)
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
                    { fields -> populateList(fields) },
                    { Timber.e(it) }
                )
        )
    }

    private fun getBiometricVerificationViewModel(): BiometricsVerificationViewModel {
        val viewModel = formRepository.composeList().toMutableList().firstOrNull {
            it.isBiometricsVerificationModel()
        }

        if (viewModel == null) {
            throw IllegalStateException("Shouldn't have been allowed to start Simprints without Biometrics Verification ViewModel")
        } else {
            return viewModel as BiometricsVerificationViewModel
        }
    }

    private fun populateList(items: List<FieldUiModel>? = null) {
        val fields = formRepository.composeList(items).toMutableList()

        val updatedFields =
            if (BIOMETRICS_ENABLED) updateBiometricsField(fields)
            else fields

        view.showFields(updatedFields)
        checkFinishing(true)
        activityPresenter.hideProgress()
        if (items != null) {
            selectedSection ?: items
                .mapNotNull { it.programStageSection }
                .firstOrNull()
                .let { selectedSection = it }
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

    fun mapVerificationStatus(biometricsVerificationStatus: Int): BiometricsVerificationView.BiometricsVerificationStatus {
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

    fun saveValue(uid: String, value: String?) {
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
        teiOrgUnit: String
    ) {
        this.biometricsGuid = biometricsGuid
        this.biometricsVerificationStatus = biometricsVerificationStatus
        this.teiOrgUnit = teiOrgUnit
    }

    fun refreshBiometricsVerificationStatus(
        biometricsVerificationStatus: Int
    ) {
        this.biometricsVerificationStatus = biometricsVerificationStatus
        activityPresenter.refreshByBiometricsVerification(this.biometricsVerificationStatus)
    }
}
