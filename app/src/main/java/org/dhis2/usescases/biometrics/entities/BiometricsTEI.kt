package org.dhis2.usescases.biometrics.entities

data class BiometricsTEI(
    val uid: String,
    val relationships: List<BiometricsTEIRelationShip>
)

data class BiometricsTEIRelationShip(
    val relationshipTypeUid: String,
    val fromUid: String,
    val toUid: String
)

