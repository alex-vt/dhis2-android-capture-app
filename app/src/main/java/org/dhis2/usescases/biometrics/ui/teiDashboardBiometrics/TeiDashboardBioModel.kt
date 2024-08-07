package org.dhis2.usescases.biometrics.ui.teiDashboardBiometrics

data class TeiDashboardBioModel(
    val verificationStatusModel: BioVerificationStatus?,
    val buttonModel: BioButtonModel,
)

data class BioButtonModel(
    val text: String,
    val backgroundColor: String,
    val icon: Int,
    val onActionClick: () -> Unit,
)

data class BioVerificationStatus(
    val text: String,
    val backgroundColor: String,
    val icon: Int
)



