package org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment

import org.dhis2.form.data.DataIntegrityCheckResult
import org.dhis2.form.data.FieldsWithErrorResult
import org.dhis2.form.data.FieldsWithWarningResult
import org.dhis2.form.data.MissingMandatoryResult
import org.dhis2.form.data.NotSavedResult
import org.dhis2.form.data.SuccessfulResult
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.FieldUiModelImpl
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.EventCaptureContract
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.eventCaptureFragment.upg.domain.UPGItem
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.event.EventEditableStatus

private const val UPG_Name = "cFKggVHL4pu"
private const val UPG_UId = "LDh4Dt7xGD0"

class EventCaptureFormPresenter(
    private val view: EventCaptureFormView,
    private val activityPresenter: EventCaptureContract.Presenter,
    private val d2: D2,
    private val eventUid: String,
) {

    fun handleDataIntegrityResult(result: DataIntegrityCheckResult) {
        when (result) {
            is FieldsWithErrorResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                result.fieldUidErrorList,
                result.mandatoryFields,
                result.warningFields,
            )

            is FieldsWithWarningResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                emptyList(),
                emptyMap(),
                result.fieldUidWarningList,
            )

            is MissingMandatoryResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                result.errorFields,
                result.mandatoryFields,
                result.warningFields,
            )

            is SuccessfulResult -> activityPresenter.attemptFinish(
                result.canComplete,
                result.onCompleteMessage,
                emptyList(),
                emptyMap(),
                emptyList(),
            )

            NotSavedResult -> {
                // Nothing to do in this case
            }
        }
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

    private var upgUidUIModel: FieldUiModel? = null
    private var upgNameUIModel: FieldUiModel? = null
    private var savingSelectedUPG = false

    fun onFieldsLoading(fields: List<FieldUiModel>): List<FieldUiModel> {

        //Eyeseetea customization - Remove UPG field from the form
        val finalFields = fields
            .map { field ->
                if (field.uid == UPG_Name) {
                    field.setEditable(false)
                } else if (field.uid == UPG_UId){
                    field.setVisible(false)
                }
                else {
                    field
                }
            }

        upgUidUIModel = finalFields.find { it.uid == UPG_UId }
        upgNameUIModel = finalFields.find { it.uid == UPG_Name }

        val event = d2.eventModule().events().byUid().eq(eventUid).one().blockingGet()

        (upgNameUIModel as FieldUiModelImpl).setFocusCallback {
            if (event?.organisationUnit() != null && !savingSelectedUPG) {
                view.selectUPG(event.organisationUnit()!!)
            }
        }

        return finalFields
    }

    fun onUPGSelected(upg: UPGItem) {
        savingSelectedUPG = true

        upgUidUIModel?.onSave(upg.guid)
        upgNameUIModel?.onSave(upg.name)

        savingSelectedUPG = false
    }
}
