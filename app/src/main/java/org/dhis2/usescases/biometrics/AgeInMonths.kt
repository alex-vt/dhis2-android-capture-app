package org.dhis2.usescases.biometrics

import org.dhis2.commons.date.DateUtils
import org.dhis2.commons.prefs.BasicPreferenceProvider
import org.dhis2.data.biometrics.getBiometricsParentChildConfig
import org.dhis2.form.model.FieldUiModel
import org.dhis2.usescases.biometrics.entities.DateOfBirthAttributeByProgram
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.joda.time.DateTime
import org.joda.time.Months
import org.joda.time.format.DateTimeFormat

fun isUnderAgeThreshold(
    basicPreferenceProvider: BasicPreferenceProvider,
    attributeValues: List<TrackedEntityAttributeValue>,
    programUid: String
): Boolean {
    val ageInMonths = getAgeInMonthsByAttributes(basicPreferenceProvider, attributeValues, programUid)
    val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

    return ageInMonths <= biometricsParentChildConfig.ageThresholdMonths
}

fun containsAgeFilterAndIsUnderAgeThreshold(
    basicPreferenceProvider: BasicPreferenceProvider,
    queryData: Map<String, String>,
    programUid: String
): Boolean {
    val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

    val birthdayAttribute =
        biometricsParentChildConfig.dateOfBirthAttributeByProgram
            .find { (program): DateOfBirthAttributeByProgram -> program == programUid }

    return if (birthdayAttribute != null) {
        val birthdateFieldKey = queryData.keys.find { it == birthdayAttribute.attribute }

        val value = queryData[birthdateFieldKey]

        if (value != null){
            val ageInMonths = calculateAgeInMonths(value)
            return ageInMonths <= biometricsParentChildConfig.ageThresholdMonths
        } else {
            false
        }
    } else {
        false
    }
}

fun getAgeInMonthsByFieldUiModel(
    basicPreferenceProvider: BasicPreferenceProvider,
    fields: List<FieldUiModel>,
    programUid: String
): Long {
    val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

    val birthdayAttribute =
        biometricsParentChildConfig.dateOfBirthAttributeByProgram
            .find { (program): DateOfBirthAttributeByProgram -> program == programUid }

    return if (birthdayAttribute != null) {
        val birthdateFieldValue = fields.find { it.uid == birthdayAttribute.attribute }

        if (birthdateFieldValue?.value != null && birthdateFieldValue.value != ""){
            return calculateAgeInMonths(birthdateFieldValue.value!!)
        } else {
            0
        }
    } else {
        0
    }
}

fun getAgeInMonthsByAttributes(
    basicPreferenceProvider: BasicPreferenceProvider,
    attributes: List<TrackedEntityAttributeValue>,
    programUid: String
): Long {
    val biometricsParentChildConfig = getBiometricsParentChildConfig(basicPreferenceProvider)

    val birthdayAttribute =
        biometricsParentChildConfig.dateOfBirthAttributeByProgram
            .find { (program): DateOfBirthAttributeByProgram -> program == programUid }

    return if (birthdayAttribute != null) {
        val birthdateFieldValue = attributes.find { it.trackedEntityAttribute() == birthdayAttribute.attribute }

        if (birthdateFieldValue?.value() != null && birthdateFieldValue.value() != ""){
            return calculateAgeInMonths(birthdateFieldValue.value()!!)
        } else {
            0
        }
    } else {
        0
    }
}

fun calculateAgeInMonths(value: String): Long {
    return try {
        val formatter = DateTimeFormat.forPattern(DateUtils.DATE_FORMAT_EXPRESSION)
        val dateValue = formatter.parseDateTime(value)
        Months.monthsBetween(dateValue, DateTime.now()).months.toLong()
    } catch (e: Exception) {
        0
    }
}