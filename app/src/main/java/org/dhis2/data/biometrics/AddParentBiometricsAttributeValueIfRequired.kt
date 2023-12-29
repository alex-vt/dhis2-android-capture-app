package org.dhis2.data.biometrics

import org.dhis2.commons.date.DateUtils
import org.dhis2.commons.prefs.BasicPreferenceProvider
import org.dhis2.usescases.biometrics.BIOMETRICS_ENABLED
import org.dhis2.usescases.biometrics.entities.BiometricsParentChildConfig
import org.dhis2.usescases.biometrics.entities.DateOfBirthAttributeByProgram
import org.dhis2.usescases.teiDashboard.TeiAttributesProvider
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.format.DateTimeFormat

fun addParentBiometricsAttributeValueIfRequired(
    d2: D2,
    teiAttributesProvider: TeiAttributesProvider,
    basicPreferenceProvider: BasicPreferenceProvider,
    attributeValues: MutableList<TrackedEntityAttributeValue>,
    programUid: String,
    teiUid: String
) {
    if (BIOMETRICS_ENABLED) {
        val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

        val biometricsAttribute = getBiometricsTrackedEntityAttribute(d2) ?: return

        val existBiometricsValue =
            existBiometricsAttributeValue(biometricsAttribute, attributeValues)

        val isUnderAgeThreshold =
            isUnderAgeThreshold(biometricsParentChildConfig, attributeValues, programUid)

        val searchParentBiometricsIsRequired = !existBiometricsValue && isUnderAgeThreshold

        if (searchParentBiometricsIsRequired) {

            val relatedTeiUid = getRelatedTei(d2, biometricsParentChildConfig, teiUid) ?: return

            val relatedAttributeValues: List<TrackedEntityAttributeValue> =
                teiAttributesProvider.getValuesFromProgramTrackedEntityAttributesByProgram(
                    programUid,
                    relatedTeiUid
                ).blockingGet()

            val attValue = getTrackedEntityAttributeValueByAttribute(
                biometricsAttribute,
                relatedAttributeValues
            )
            if (attValue != null) {
                attributeValues.add(attValue)
            }
        }
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

private fun getBiometricsTrackedEntityAttribute(d2: D2): String? {
    return d2.trackedEntityModule().trackedEntityAttributes()
        .byDisplayFormName().eq("Biometrics").blockingGet().firstOrNull()?.uid()
}

private fun getTrackedEntityAttributeValueByAttribute(
    attribute: String,
    attributeValues: List<TrackedEntityAttributeValue>
): TrackedEntityAttributeValue? {
    return attributeValues
        .find { attributeValue -> attributeValue.trackedEntityAttribute() == attribute }
}

private fun isUnderAgeThreshold(
    biometricsParentChildConfig: BiometricsParentChildConfig,
    attributeValues: List<TrackedEntityAttributeValue>,
    programUid: String
): Boolean {

    val birthdayAttribute =
        biometricsParentChildConfig.dateOfBirthAttributeByProgram
            .find { (program): DateOfBirthAttributeByProgram -> program == programUid }

    return if (birthdayAttribute != null) {
        val birthdateAttValue =
            getTrackedEntityAttributeValueByAttribute(birthdayAttribute.attribute, attributeValues)

        if (birthdateAttValue != null) {
            val formatter = DateTimeFormat.forPattern(DateUtils.DATE_FORMAT_EXPRESSION)
            val dateValue = formatter.parseDateTime(birthdateAttValue.value())
            val moths = Months.monthsBetween(dateValue, DateTime.now()).months
            moths <= biometricsParentChildConfig.ageThresholdMonths
        } else {
            false
        }
    } else {
        false
    }
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

