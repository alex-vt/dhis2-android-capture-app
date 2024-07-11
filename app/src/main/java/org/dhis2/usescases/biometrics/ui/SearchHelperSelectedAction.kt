package org.dhis2.usescases.biometrics.ui

sealed class SearchHelperSelectedAction {
    data object SearchWithBiometrics : SearchHelperSelectedAction()
    data object SearchWithAttributes : SearchHelperSelectedAction()
    data object RegisterNew : SearchHelperSelectedAction()
}