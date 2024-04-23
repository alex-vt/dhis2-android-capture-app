package org.dhis2.usescases.teiDashboard.ui.model

data class TeiBiometricsVerificationModel(
    val text: String,
    val backgroundColor: String,
    val icon: Int,
    val onActionClick: () -> Unit,
)

