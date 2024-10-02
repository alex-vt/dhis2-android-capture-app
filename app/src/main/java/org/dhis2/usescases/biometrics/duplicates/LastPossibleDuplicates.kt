package org.dhis2.usescases.biometrics.duplicates

data class LastPossibleDuplicates(
    val guids: List<String>,
    val sessionId: String,
)