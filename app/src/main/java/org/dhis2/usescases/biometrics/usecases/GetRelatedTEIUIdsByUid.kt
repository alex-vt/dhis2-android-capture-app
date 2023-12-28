package org.dhis2.usescases.biometrics.usecases

import org.dhis2.commons.data.SearchTeiModel
import org.dhis2.usescases.biometrics.repositories.BiometricsParentChildConfigRepository

class GetRelatedTEIUIdsByUid(
    private val biometricsParentChildConfigRepository: BiometricsParentChildConfigRepository
) {
    operator fun invoke(results: List<SearchTeiModel>): List<String> {
        val config = biometricsParentChildConfigRepository.get()

        val uIds = results.map { tei ->
            val relationships =
                    tei.relationships.filter { it.relationshipType.uid() == config.parentChildRelationship }

            val uIds = relationships.map { it.ownerUid}

            uIds.distinct()

        }.flatten()

        return uIds
    }
}