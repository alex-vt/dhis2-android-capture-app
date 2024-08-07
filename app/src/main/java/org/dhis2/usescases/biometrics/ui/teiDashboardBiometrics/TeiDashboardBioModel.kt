package org.dhis2.usescases.biometrics.ui.teiDashboardBiometrics

data class TeiDashboardBioModel(
    val text: String,
    val backgroundColor: String,
    val icon: Int,
    val onActionClick: () -> Unit,
)



