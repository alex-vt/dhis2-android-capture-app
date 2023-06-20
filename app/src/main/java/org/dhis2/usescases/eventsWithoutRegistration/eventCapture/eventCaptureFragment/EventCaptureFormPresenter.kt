package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment

import org.dhis2.form.data.DataIntegrityCheckResult
import org.dhis2.form.data.FieldsWithErrorResult
import org.dhis2.form.data.FieldsWithWarningResult
import org.dhis2.form.data.MissingMandatoryResult
import org.dhis2.form.data.NotSavedResult
import org.dhis2.form.data.SuccessfulResult
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.biometrics.BiometricsVerificationStatus
import org.dhis2.form.model.biometrics.BiometricsVerificationUiModelImpl
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureContract
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.event.EventEditableStatus
import org.jetbrains.annotations.Nullable

import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.usescases.biometrics.isLastVerificationValid

class EventCaptureFormPresenter(
    private val view: EventCaptureFormView,
    private val activityPresenter: EventCaptureContract.Presenter,
    private val d2: D2,
    private val eventUid: String
) {

    private var biometricsVerificationStatus: Int = -1
    private var biometricsGuid: String? = null
    private var teiOrgUnit: String? = null
    private var fields: List<FieldUiModel> = listOf()

    private var biometricsVerificationUiModel: BiometricsVerificationUiModelImpl? = null


    fun handleDataIntegrityResult(result: DataIntegrityCheckResult) {
        when (result) {
            is FieldsWithErrorResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                result.fieldUidErrorList,
                result.mandatoryFields,
                result.warningFields
            )
            is FieldsWithWarningResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                emptyList(),
                emptyMap(),
                result.fieldUidWarningList
            )
            is MissingMandatoryResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                result.errorFields,
                result.mandatoryFields,
                result.warningFields
            )
            is SuccessfulResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                emptyList(),
                emptyMap(),
                emptyList()
            )
            NotSavedResult -> {
                // Nothing to do in this case
            }
        }
    }

    fun onFieldsLoaded(fields: List<FieldUiModel>) {
        if (BIOMETRICS_ENABLED) {
            biometricsVerificationUiModel = fields.firstOrNull {
                it is BiometricsVerificationUiModelImpl
            }?.let { it as BiometricsVerificationUiModelImpl }

            updateBiometricsField()

            biometricsVerificationUiModel?.setBiometricsRetryListener(
                object : BiometricsVerificationUiModelImpl.BiometricsReTryOnClickListener {
                    override fun onRetryClick() {
                        view.verifyBiometrics(biometricsGuid, teiOrgUnit)
                    }
                }
            )

            if (biometricsVerificationUiModel != null) {
                launchBiometricsVerificationIfRequired(fields)
            }
        }
    }

    private fun launchBiometricsVerificationIfRequired(fields: List<FieldUiModel>) {
        biometricsVerificationUiModel = fields.firstOrNull {
            it is BiometricsVerificationUiModelImpl
        }?.let { it as BiometricsVerificationUiModelImpl }

        if (biometricsVerificationUiModel != null && this.biometricsGuid != null &&
            biometricsVerificationUiModel?.status == BiometricsVerificationStatus.NOT_DONE
        ) {
            val lastUpdated = activityPresenter.getBiometricsAttributeValueInTeiLastUpdated(
                biometricsVerificationUiModel?.uid
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

    private fun updateBiometricsField(){
        val status = mapVerificationStatus(biometricsVerificationStatus)
        biometricsVerificationUiModel?.setValue(biometricsGuid)
        biometricsVerificationUiModel?.setStatus(status)
    }

    fun showOrHideSaveButton() {
        val isEditable =
            d2.eventModule().eventService().getEditableStatus(eventUid = eventUid).blockingGet()
        if (isEditable is EventEditableStatus.Editable) {
            view.showSaveButton()
        } else {
            view.hideSaveButton()
        }
    }

    private fun mapVerificationStatus(biometricsVerificationStatus: Int): BiometricsVerificationStatus {
        return when (biometricsVerificationStatus) {
            0 -> BiometricsVerificationStatus.FAILURE
            1 -> BiometricsVerificationStatus.SUCCESS
            else -> BiometricsVerificationStatus.NOT_DONE
        }
    }

    fun <E> Iterable<E>.updated(index: Int, elem: E): List<E> =
        mapIndexed { i, existing -> if (i == index) elem else existing }

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
        if (biometricsVerificationUiModel != null) {
            biometricsVerificationUiModel?.onSave(biometricsGuid)
            //activityPresenter.saveValue(biometricsVerificationUiModel!!.uid, )
        }

        val status = mapVerificationStatus(biometricsVerificationStatus)

        if (status == BiometricsVerificationStatus.SUCCESS && saveValue) {
            activityPresenter.updateBiometricsAttributeValueInTei(biometricsGuid)
        }

        this.biometricsVerificationStatus = biometricsVerificationStatus
        activityPresenter.refreshByBiometricsVerification(this.biometricsVerificationStatus)
    }
}
