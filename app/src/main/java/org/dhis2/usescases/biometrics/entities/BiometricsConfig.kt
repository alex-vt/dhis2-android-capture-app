package org.dhis2.usescases.biometrics.entities

data class BiometricsConfig(
    val orgUnitGroup: String?,
    val projectId: String,
    val userId: String,
    val confidenceScoreFilter: Int?,
    val icon: String?,
    val lastVerificationDuration: Int?,
    val lastDeclinedEnrolDuration: Int?,
    val program: String?,
    val orgUnitLevelAsModuleId: Int?,
    val ageThresholdMonths: String?,
    val dateOfBirthAttribute: String?
)