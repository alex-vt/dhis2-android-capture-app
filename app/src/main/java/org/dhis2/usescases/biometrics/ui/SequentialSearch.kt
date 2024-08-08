package org.dhis2.usescases.biometrics.ui

import org.dhis2.data.biometrics.SimprintsItem

sealed class SequentialSearch(
    open val previousSearch: SequentialSearch?,
    open val nextAction: SequentialSearchAction?
) {
    val sequentialSessionId: String?
        get() = when (this) {
            is BiometricsSearch -> {
                this.sessionId
            }

            is AttributeSearch -> {
                (previousSearch as? BiometricsSearch)?.sessionId
            }
        }

    data class BiometricsSearch(
        override val previousSearch: SequentialSearch?,
        override val nextAction: SequentialSearchAction?,
        val simprintsItems: List<SimprintsItem>,
        val sessionId: String
    ) : SequentialSearch(previousSearch, nextAction)

    data class AttributeSearch(
        override val previousSearch: SequentialSearch?,
        override val nextAction: SequentialSearchAction?,
    ) : SequentialSearch(previousSearch, nextAction)
}

sealed class SequentialSearchAction {
    data object SearchWithBiometrics : SequentialSearchAction()
    data object SearchWithAttributes : SequentialSearchAction()
    data object RegisterNew : SequentialSearchAction()
}

