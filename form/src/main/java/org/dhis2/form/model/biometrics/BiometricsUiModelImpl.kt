package org.dhis2.form.model.biometrics

import org.dhis2.form.model.*
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.event.UiEventFactory
import org.dhis2.form.ui.intent.FormIntent
import org.dhis2.form.ui.intent.FormIntent.OnFocus
import org.dhis2.form.ui.style.FormUiModelStyle
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.option.Option

data class BiometricsUiModelImpl(
    override val uid: String,
    override val layoutId: Int,
    override val value: String? = null,

) : FieldUiModel {

    private var sectionNumber: Int = 0
    private var showBottomShadow: Boolean = false
    private var lastPositionShouldChangeHeight: Boolean = false

    private var callback: FieldUiModel.Callback? = null

    fun hasToShowDescriptionIcon(isTitleEllipsed: Boolean): Boolean {
        return description != null && description.isNotEmpty() || isTitleEllipsed
    }


    fun setSelected() {
  /*      onItemClick()
        selectedField.get()?.let {
            val sectionToOpen = if (it == uid) "" else uid
            selectedField.set(sectionToOpen)
            callback!!.intent(OnSection(sectionToOpen))
        }*/
    }

    fun isSelected(): Boolean = false


    override val formattedLabel: String
        get() = label

    override fun setCallback(callback: FieldUiModel.Callback) {
        this.callback = callback
    }

    override fun equals(item: FieldUiModel): Boolean {
        item as SectionUiModelImpl
        return super.equals(item)
    }

    override val focused = false
    override val error = ""
    override val editable = false
    override val warning = ""
    override val mandatory = true
    override val label = ""
    override val programStageSection: String? = null
    override val style: FormUiModelStyle? = null
    override val hint: String? = null
    override val description: String = ""
    override val valueType: ValueType? = null
    override val legend: LegendValue? = null
    override val optionSet: String? = null
    override val allowFutureDates: Boolean? = null
    override val uiEventFactory: UiEventFactory? = null
    override val displayName: String? = null
    override val renderingType: UiRenderType? = null
    override var optionSetConfiguration: OptionSetConfiguration? = null
    override val keyboardActionType: KeyboardActionType? = null
    override val fieldMask: String? = null
    override val isLoadingData = false

    override fun onItemClick() {
        callback!!.intent(
            OnFocus(
                uid,
                value
            )
        )
    }

    override fun onDescriptionClick() {
        callback?.recyclerViewUiEvents(
            RecyclerViewUiEvents.ShowDescriptionLabelDialog(
                label,
                description
            )
        )
    }

    override fun invokeUiEvent(uiEventType: UiEventType) {
        onItemClick()
    }

    override fun invokeIntent(intent: FormIntent) {
        callback?.intent(intent)
    }

    override val textColor: Int?
        get() = style?.textColor(error, warning)

    override val backGroundColor: Pair<Array<Int>, Int>?
        get() =
            valueType?.let {
                style?.backgroundColor(it, error, warning)
            }

    override val hasImage: Boolean
        get() = false

    override val isAffirmativeChecked: Boolean
        get() = false

    override val isNegativeChecked: Boolean
        get() = false

    override fun onNext() {}

    override fun onTextChange(value: CharSequence?) {}

    override fun onClear() {}

    override fun onSave(value: String?) {}

    override fun onSaveBoolean(boolean: Boolean) {}

    override fun onSaveOption(option: Option) {}

    override fun setValue(value: String?) = this.copy(value = value)

    override fun setIsLoadingData(isLoadingData: Boolean) = this.copy()

    override fun setDisplayName(displayName: String?) = this.copy()

    override fun setKeyBoardActionDone()  = this.copy()

    override fun setFocus() = this.copy()

    override fun setError(error: String?) =  this.copy()

    override fun setEditable(editable: Boolean)= this.copy()

    override fun setLegend(legendValue: LegendValue?) = this.copy()

    override fun setWarning(warning: String) = this.copy()

    override fun setFieldMandatory()= this.copy()

    override fun isSectionWithFields() = false

    companion object {
        const val BIOMETRICS_UID = "BIOMETRICS_UID"
    }
}
