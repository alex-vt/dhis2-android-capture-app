package org.dhis2.usescases.biometrics.entities

data class BiometricsConfig(
    val orgUnitGroup: String?,
    val projectId: String,
    val confidenceScoreFilter: Int?,
    val icon: String?,
    val lastVerificationDuration: Int?,
    val program: String?,
)