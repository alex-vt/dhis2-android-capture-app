package org.dhis2.usescases.biometrics.entities

data class BiometricsParentChildConfig(
    val ageThresholdMonths: Int,
    val parentChildRelationship: String,
    val dateOfBirthAttributeByProgram: List<DateOfBirthAttributeByProgram>
)

data class DateOfBirthAttributeByProgram(  val program: String,  val attribute: String)