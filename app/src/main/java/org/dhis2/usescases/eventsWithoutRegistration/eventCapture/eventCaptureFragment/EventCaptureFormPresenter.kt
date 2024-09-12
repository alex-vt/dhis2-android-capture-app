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

data class UPGProgram(val programUid: String, val upgName: String, val upgUid: String)

private const val seasonPlanProgramUid = "WCJhvPcJomX"
private const val seasonPlanUPGName = "cFKggVHL4pu"
private const val seasonPlanUPGUId = "LDh4Dt7xGD0"

private const val endOfSeasonReportProgramUid = "JsM6wTUTsL6"
private const val endOfSeasonReportUPGName = "T4TY8UuBiza"
private const val endOfSeasonReportUPGUId = "e3m1f3pdLAl"

private val programsWithUPG = listOf(
    UPGProgram(seasonPlanProgramUid, seasonPlanUPGName, seasonPlanUPGUId),
    UPGProgram(endOfSeasonReportProgramUid, endOfSeasonReportUPGName, endOfSeasonReportUPGUId))


class EventCaptureFormPresenter(
    private val view: EventCaptureFormView,
    private val activityPresenter: EventCaptureContract.Presenter,
    private val d2: D2,
    private val eventUid: String,
) {
    var programUid: String = d2.eventModule().events().byUid().eq(eventUid).one().blockingGet()?.program() ?: ""

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
        val programWithUPG = programsWithUPG.find { it.programUid == programUid } ?: return fields

        //Eyeseetea customization - Remove UPG field from the form
        val finalFields = fields
            .map { field ->
                if (field.uid == programWithUPG.upgName) {
                    field.setEditable(false)
                } else if (field.uid == programWithUPG.upgUid){
                    field.setVisible(false)
                }
                else {
                    field
                }
            }

        upgUidUIModel = finalFields.find { it.uid == programWithUPG.upgUid }
        upgNameUIModel = finalFields.find { it.uid == programWithUPG.upgName }

        if(upgUidUIModel != null && upgNameUIModel != null){
            val event = d2.eventModule().events().byUid().eq(eventUid).one().blockingGet()

            (upgNameUIModel as FieldUiModelImpl).setFocusCallback {
                if (event?.organisationUnit() != null && !savingSelectedUPG) {
                    view.selectUPG(event.organisationUnit()!!)
                }
            }
        }

        return finalFields
    }

    fun onUPGSelected(upg: UPGItem) {
        savingSelectedUPG = true

        if (upg.guid.isNotBlank()){
            upgUidUIModel?.onSave(upg.guid)
        }

        upgNameUIModel?.onSave(upg.name)

        savingSelectedUPG = false
    }
}
