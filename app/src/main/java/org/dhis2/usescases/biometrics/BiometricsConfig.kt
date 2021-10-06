package org.dhis2.usescases.biometrics

data class BiometricsConfig(
    val projectId: String,
    val confidenceScoreFilter: Int,
    val icon: String
)