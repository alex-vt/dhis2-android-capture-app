package org.dhis2.data.biometrics

import com.google.gson.reflect.TypeToken
import org.dhis2.commons.biometrics.BiometricsPreference
import org.dhis2.commons.prefs.BasicPreferenceProvider
import org.dhis2.usescases.biometrics.entities.BiometricsParentChildConfig
import org.dhis2.usescases.biometrics.entities.DateOfBirthAttributeByProgram

fun getBiometricsParentChildConfig(preferenceProvider: BasicPreferenceProvider): BiometricsParentChildConfig {
    val parentChildRelationship =
        preferenceProvider.getString(BiometricsPreference.PARENT_CHILD_RELATIONSHIP, "")

    val ageThreshold = preferenceProvider.getInt(BiometricsPreference.AGE_THRESHOLD, 0)

    val sateOfBirthAttributeByProgramType =
        object : TypeToken<List<DateOfBirthAttributeByProgram>>() {}

    val dateOfBirthAttributeByProgram = preferenceProvider.getObjectFromJson(
        BiometricsPreference.DATE_OF_BIRTH_ATT_BY_PROGRAM,
        sateOfBirthAttributeByProgramType,
        listOf()
    )

    return BiometricsParentChildConfig(
        ageThreshold,
        parentChildRelationship!!,
        dateOfBirthAttributeByProgram
    )
}