package org.dhis2.data.biometrics

import android.content.Context
import com.google.gson.reflect.TypeToken
import org.dhis2.commons.biometrics.BiometricsPreference
import org.dhis2.commons.prefs.PreferenceProviderImpl
import org.dhis2.commons.prefs.SECURE_USER_NAME
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.CONFIDENCE_SCORE_FILTER
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.PROJECT_ID
import org.dhis2.commons.prefs.BasicPreferenceProviderImpl
import org.dhis2.usescases.biometrics.BiometricsConfig

object BiometricsClientFactory {
    fun get( context:Context):BiometricsClient{
        val preferences = BasicPreferenceProviderImpl(context)

        val biometricsConfigType = object : TypeToken<List<BiometricsConfig>>() {}
        val options = preferences.getObjectFromJson(BiometricsPreference.CONFIGURATIONS,  biometricsConfigType, listOf())


        val listStringType = object : TypeToken<List<String>>() {}
        val userOrUnitGroups = preferences.getObjectFromJson(BiometricsPreference.USER_ORG_UNIT_GROUPS,  listStringType, listOf())

        val projectId = preferences.getString(PROJECT_ID,"Ma9wi0IBdo215PKRXOf5")!!
        val userId = preferences.getString(SECURE_USER_NAME,"")!!
        val confidenceScoreFilter = preferences.getInt(CONFIDENCE_SCORE_FILTER,0)


        return BiometricsClient(projectId, userId, confidenceScoreFilter)
    }
}