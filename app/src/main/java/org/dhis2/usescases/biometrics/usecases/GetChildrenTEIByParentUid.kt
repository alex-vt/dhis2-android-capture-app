package org.dhis2.usescases.biometrics.usecases

import org.dhis2.usescases.biometrics.repositories.BiometricsParentChildConfigRepository
import org.dhis2.usescases.biometrics.repositories.BiometricsTEIRepository

class GetChildrenTEIByParentUid(
    private val biometricsParentChildConfigRepository: BiometricsParentChildConfigRepository,
    private val biometricsTEIRepository: BiometricsTEIRepository
) {
    operator fun invoke(uIds: List<String>): List<String> {
        val config = biometricsParentChildConfigRepository.get()

        val childrenUIds = uIds.map { uid ->
            val tei = biometricsTEIRepository.getByUid(uid)

            if (tei == null) {
                listOf()
            } else {
                val children =
                    tei.relationships.filter { it.relationshipTypeUid == config.parentChildRelationship }

                val childrenFromUids = children.map { it.fromUid }
                val childrenToUids = children.map { it.fromUid }
                val allChildrenUids = (childrenFromUids + childrenToUids).filter { it != uid }

                allChildrenUids.distinct()
            }
        }.flatten()

        return childrenUIds;
    }
}