package org.dhis2.form.data

import org.dhis2.form.model.ActionType
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.RowAction
import org.dhis2.form.model.SectionUiModelImpl
import org.dhis2.form.model.StoreResult
import org.dhis2.form.model.ValueStoreResult
import org.dhis2.form.ui.provider.DisplayNameProvider
import org.dhis2.form.ui.validation.FieldErrorMessageProvider
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.common.ValueType.LONG_TEXT

private const val loopThreshold = 5

class FormRepositoryImpl(
    private val formValueStore: FormValueStore?,
    private val fieldErrorMessageProvider: FieldErrorMessageProvider,
    private val displayNameProvider: DisplayNameProvider,
    private val dataEntryRepository: DataEntryRepository?,
    private val ruleEngineRepository: RuleEngineRepository?,
    private val rulesUtilsProvider: RulesUtilsProvider?
) : FormRepository {

    private var completionPercentage: Float = 0f
    private val itemsWithError: MutableList<RowAction> = mutableListOf()
    private val mandatoryItemsWithoutValue: MutableMap<String, String> = mutableMapOf()
    private var openedSectionUid: String? = null
    private var itemList: List<FieldUiModel> = emptyList()
    private var focusedItem: RowAction? = null
    private var ruleEffectsResult: RuleUtilsProviderResult? = null
    private var showWarnigns: Boolean = false
    private var showErrors: Boolean = false
    private var calculationLoop: Int = 0

    override fun fetchFormItems(): List<FieldUiModel> {
        openedSectionUid =
            dataEntryRepository?.sectionUids()?.blockingFirst()?.firstOrNull()
        itemList = dataEntryRepository?.list()?.blockingFirst() ?: emptyList()
        return composeList()
    }

    override fun processUserAction(action: RowAction): StoreResult {
        return when (action.type) {
            ActionType.ON_SAVE -> {
                updateErrorList(action)
                if (action.error != null) {
                    StoreResult(
                        action.id,
                        ValueStoreResult.VALUE_HAS_NOT_CHANGED
                    )
                } else {
                    val saveResult = formValueStore?.save(action.id, action.value, action.extraData)
                    updateValueOnList(action.id, action.value, action.valueType)
                    saveResult ?: StoreResult(
                        action.id,
                        ValueStoreResult.VALUE_CHANGED
                    )
                }
            }
            ActionType.ON_FOCUS, ActionType.ON_NEXT -> {
                this.focusedItem = action

                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_HAS_NOT_CHANGED
                )
            }

            ActionType.ON_TEXT_CHANGE -> {
                updateValueOnList(action.id, action.value, action.valueType)
                StoreResult(
                    action.id,
                    ValueStoreResult.TEXT_CHANGING
                )
            }
            ActionType.ON_SECTION_CHANGE -> {
                updateSectionOpened(action)
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_HAS_NOT_CHANGED
                )
            }
            ActionType.ON_CLEAR -> {
                removeAllValues()
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_CHANGED
                )
            }
        }
    }

    override fun composeList(): List<FieldUiModel> {
        calculationLoop = 0
        return itemList
            .applyRuleEffects()
            .mergeListWithErrorFields(itemsWithError)
            .also {
                calculateCompletionPercentage(it)
            }
            .setOpenedSection()
            .setFocusedItem()
            .setLastItem()
    }

    private fun List<FieldUiModel>.setLastItem(): List<FieldUiModel> {
        if (isEmpty()) {
            return this
        }
        return if (this.all { it is SectionUiModelImpl }) {
            val lastItem = getLastSectionItem(this)
            return if (usesKeyboard(lastItem.valueType) && lastItem.valueType != LONG_TEXT) {
                updated(indexOf(lastItem), lastItem.setKeyBoardActionDone())
            } else {
                this
            }
        } else {
            this
        }
    }

    private fun usesKeyboard(valueType: ValueType?): Boolean {
        return valueType?.let {
            it.isText || it.isNumeric || it.isInteger
        } ?: false
    }

    private fun getLastSectionItem(list: List<FieldUiModel>): FieldUiModel {
        return if (list.all { it is SectionUiModelImpl }) {
            list.asReversed().first()
        } else {
            list.asReversed().first { it.valueType != null }
        }
    }

    private fun ruleEffects() = try {
        ruleEngineRepository?.calculate() ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    private fun calculateCompletionPercentage(list: List<FieldUiModel>) {
        val unsupportedValueTypes = listOf(
            ValueType.FILE_RESOURCE,
            ValueType.TRACKER_ASSOCIATE,
            ValueType.USERNAME
        )
        val fields = list.filter {
            it.valueType != null &&
                !unsupportedValueTypes.contains(it.valueType)
        }
        val totalFields = fields.size
        val fieldsWithValue = fields.filter { it.value != null }.size
        completionPercentage = if (totalFields == 0) {
            0f
        } else {
            fieldsWithValue.toFloat().div(totalFields.toFloat())
        }
    }

    override fun getConfigurationErrors(): List<RulesUtilsProviderConfigurationError>? {
        return ruleEffectsResult?.configurationErrors
    }

    override fun runDataIntegrityCheck(): DataIntegrityCheckResult {
        val result = when {
            mandatoryItemsWithoutValue.isNotEmpty() -> {
                showWarnigns = true
                MissingMandatoryResult(
                    mandatoryItemsWithoutValue,
                    ruleEffectsResult?.canComplete ?: true,
                    ruleEffectsResult?.messageOnComplete
                )
            }
            ruleEffectsResult?.fieldsWithErrors?.isNotEmpty() == true -> {
                showWarnigns =
                    showWarnigns || ruleEffectsResult?.fieldsWithWarnings?.isNotEmpty() == true
                showErrors = true
                FieldsWithErrorResult(
                    fieldsWithError(),
                    ruleEffectsResult?.canComplete ?: true,
                    ruleEffectsResult?.messageOnComplete
                )
            }
            ruleEffectsResult?.fieldsWithWarnings?.isNotEmpty() == true -> {
                showWarnigns = true
                FieldsWithWarningResult(
                    fieldsWithWarning(),
                    ruleEffectsResult?.canComplete ?: true,
                    ruleEffectsResult?.messageOnComplete
                )
            }
            else -> {
                SuccessfulResult(
                    canComplete = ruleEffectsResult?.canComplete ?: true,
                    onCompleteMessage = ruleEffectsResult?.messageOnComplete
                )
            }
        }
        return result
    }

    override fun completedFieldsPercentage(value: List<FieldUiModel>): Float {
        return completionPercentage
    }

    override fun calculationLoopOverLimit(): Boolean {
        return calculationLoop == loopThreshold
    }

    private fun fieldsWithError() = itemList.mapNotNull { field ->
        field.error?.let { field.uid }
    }

    private fun fieldsWithWarning() = itemList.mapNotNull { field ->
        field.warning?.let { field.uid }
    }

    private fun List<FieldUiModel>.applyRuleEffects(): List<FieldUiModel> {
        val ruleEffects = ruleEffects()
        val fieldMap = this.map { it.uid to it }.toMap().toMutableMap()
        ruleEffectsResult = rulesUtilsProvider?.applyRuleEffects(
            applyForEvent = dataEntryRepository?.isEvent == true,
            fieldViewModels = fieldMap,
            ruleEffects,
            valueStore = formValueStore
        )
        return if (ruleEffectsResult?.fieldsToUpdate?.isNotEmpty() == true ||
            calculationLoop == loopThreshold
        ) {
            calculationLoop += 1
            ArrayList(fieldMap.values).applyRuleEffects()
        } else {
            ArrayList(fieldMap.values)
        }
    }

    private fun List<FieldUiModel>.setFocusedItem(): List<FieldUiModel> {
        return focusedItem?.let {
            val uid = if (it.type == ActionType.ON_NEXT) {
                getNextItem(it.id)
            } else {
                it.id
            }

            find { item ->
                item.uid == uid
            }?.let { item ->
                updated(indexOf(item), item.setFocus())
            } ?: this
        } ?: this
    }

    private fun List<FieldUiModel>.setOpenedSection(): List<FieldUiModel> {
        return map { field ->
            if (field.valueType == null) {
                updateSection(field, this)
            } else {
                updateField(field)
            }
        }
            .filter { field ->
                field.valueType == null ||
                    field.programStageSection == openedSectionUid
            }
    }

    private fun updateSection(
        sectionFieldUiModel: FieldUiModel,
        fields: List<FieldUiModel>
    ): FieldUiModel {
        var total = 0
        var values = 0
        val isOpen = sectionFieldUiModel.uid == openedSectionUid
        fields.filter {
            it.programStageSection.equals(sectionFieldUiModel.uid) && it.valueType != null
        }.forEach {
            total++
            if (!it.value.isNullOrEmpty()) {
                values++
            }
        }

        val warningCount =
            ruleEffectsResult?.takeIf { showWarnigns }?.warningMap()?.filter { warning ->
                fields.firstOrNull { field ->
                    field.uid == warning.key && field.programStageSection == sectionFieldUiModel.uid
                } != null
            }?.size ?: 0
        val mandatoryCount = mandatoryItemsWithoutValue.takeIf { showWarnigns }
            ?.filter { it.value == sectionFieldUiModel.uid }?.size ?: 0
        val errorCount = ruleEffectsResult?.takeIf { showErrors }?.errorMap()?.filter { error ->
            fields.firstOrNull { field ->
                field.uid == error.key && field.programStageSection == sectionFieldUiModel.uid
            } != null
        }?.size ?: 0

        return dataEntryRepository?.updateSection(
            sectionFieldUiModel,
            isOpen,
            total,
            values,
            errorCount,
            warningCount + mandatoryCount
        ) ?: sectionFieldUiModel
    }

    private fun updateField(fieldUiModel: FieldUiModel): FieldUiModel {
        val needsMandatoryWarning = fieldUiModel.mandatory &&
            fieldUiModel.value == null && showWarnigns

        if (needsMandatoryWarning) {
            mandatoryItemsWithoutValue[fieldUiModel.label] = fieldUiModel.programStageSection ?: ""
        }

        return dataEntryRepository?.updateField(
            fieldUiModel,
            fieldErrorMessageProvider.mandatoryWarning().takeIf { needsMandatoryWarning },
            ruleEffectsResult?.optionsToHide(fieldUiModel.uid) ?: emptyList(),
            ruleEffectsResult?.optionGroupsToHide(fieldUiModel.uid) ?: emptyList(),
            ruleEffectsResult?.optionGroupsToShow(fieldUiModel.uid) ?: emptyList()
        ) ?: fieldUiModel
    }

    private fun getNextItem(currentItemUid: String): String? {
        itemList.let { fields ->
            val oldItem = fields.find { it.uid == currentItemUid }
            val pos = fields.indexOf(oldItem)
            if (pos < fields.size - 1) {
                return fields[pos + 1].uid
            }
        }
        return null
    }

    private fun updateValueOnList(uid: String, value: String?, valueType: ValueType?) {
        itemList.let { list ->
            list.find { item ->
                item.uid == uid
            }?.let { item ->
                itemList = list.updated(
                    list.indexOf(item),
                    item.setValue(value)
                        .setDisplayName(
                            displayNameProvider.provideDisplayName(
                                valueType,
                                value,
                                item.optionSet
                            )
                        )
                )
            }
        }
    }

    private fun removeAllValues() {
        itemList = itemList.map { fieldUiModel ->
            fieldUiModel.setValue(null)
        }
    }

    private fun List<FieldUiModel>.mergeListWithErrorFields(
        fieldsWithError: MutableList<RowAction>
    ): List<FieldUiModel> {
        return map { item ->
            if (item.mandatory && item.value == null) {
                mandatoryItemsWithoutValue[item.label] = item.programStageSection ?: ""
            } else {
                mandatoryItemsWithoutValue.remove(item.label)
            }
            fieldsWithError.find { it.id == item.uid }?.let { action ->
                val error = action.error?.let {
                    fieldErrorMessageProvider.getFriendlyErrorMessage(it)
                }
                item.setValue(action.value).setError(error)
                    .setDisplayName(
                        displayNameProvider.provideDisplayName(
                            action.valueType,
                            action.value
                        )
                    )
            } ?: item
        }
    }

    private fun updateErrorList(action: RowAction) {
        if (action.error != null) {
            if (itemsWithError.find { it.id == action.id } == null) {
                itemsWithError.add(action)
            }
        } else {
            itemsWithError.find { it.id == action.id }?.let {
                itemsWithError.remove(it)
            }
        }
    }

    private fun updateSectionOpened(action: RowAction) {
        openedSectionUid = action.id
    }

    fun <E> Iterable<E>.updated(index: Int, elem: E): List<E> =
        mapIndexed { i, existing -> if (i == index) elem else existing }
}