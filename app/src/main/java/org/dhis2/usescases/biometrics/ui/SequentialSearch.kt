package org.dhis2.usescases.biometrics.ui

import org.dhis2.data.biometrics.SimprintsItem

sealed class SequentialSearch(
    open val previousSearch: SequentialSearch?,
    open val nextAction: SequentialSearchNextAction?
) {
    data class BiometricsSearch(
        override val previousSearch: SequentialSearch?,
        override val nextAction: SequentialSearchNextAction?,
        val simprintsItems: List<SimprintsItem>
    ) : SequentialSearch(previousSearch, nextAction)

    data class AttributeSearch(
        override val previousSearch: SequentialSearch?,
        override val nextAction: SequentialSearchNextAction?,
    ) : SequentialSearch(previousSearch, nextAction)
}

sealed class SequentialSearchNextAction {
    data object SearchWithBiometrics : SequentialSearchNextAction()
    data object SearchWithAttributes : SequentialSearchNextAction()
    data object RegisterNew : SequentialSearchNextAction()
}

const val bioMatchKey: String = "Bio Match"
