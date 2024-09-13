package org.dhis2.data.biometrics.utils

import org.dhis2.commons.prefs.BasicPreferenceProvider
import org.dhis2.data.biometrics.getBiometricsParentChildConfig
import org.dhis2.usescases.biometrics.entities.BiometricsParentChildConfig
import org.dhis2.usescases.biometrics.isUnderAgeThreshold
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue

fun getParentTeiUid(
    d2: D2,
    basicPreferenceProvider: BasicPreferenceProvider,
    attributeValues: List<TrackedEntityAttributeValue>,
    programUid: String,
    teiUid: String
):String? {
        val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

        val biometricsAttribute = getBiometricsTrackedEntityAttribute(d2) ?: return null

        val existBiometricsValue =
            existBiometricsAttributeValue(biometricsAttribute, attributeValues)

        val isUnderAgeThreshold =
            isUnderAgeThreshold(basicPreferenceProvider, attributeValues, programUid)

        val searchParentBiometricsIsRequired = !existBiometricsValue && isUnderAgeThreshold

    return if (searchParentBiometricsIsRequired) {
        getRelatedTei(d2, biometricsParentChildConfig, teiUid)
    } else {
        null
    }
}

private fun existBiometricsAttributeValue(
    biometricsAttribute: String,
    attributeValues: List<TrackedEntityAttributeValue>
): Boolean {
    val attValueOptional =
        getTrackedEntityAttributeValueByAttribute(biometricsAttribute, attributeValues)

    return attValueOptional != null
}

private fun getRelatedTei(
    d2: D2,
    biometricsParentChildConfig: BiometricsParentChildConfig,
    teiUid: String
): String? {

    val relationships: List<Relationship> =
        d2.relationshipModule().relationships().getByItem(
            RelationshipItem.builder().trackedEntityInstance(
                RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(teiUid)
                    .build()
            ).build()
        ).filter { relationship: Relationship ->
            relationship.relationshipType() ==
                    biometricsParentChildConfig.parentChildRelationship
        }

    return relationships.map { relationship: Relationship ->
        if (relationship.from()!!.elementUid() == teiUid) {
            return@map relationship.to()!!.elementUid()
        } else {
            return@map relationship.from()!!.elementUid()
        }
    }.firstOrNull()
}

