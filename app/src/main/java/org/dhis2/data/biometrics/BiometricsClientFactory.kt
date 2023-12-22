package org.dhis2.data.biometrics

import android.content.Context
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.CONFIDENCE_SCORE_FILTER
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.PROJECT_ID
import org.dhis2.commons.prefs.BasicPreferenceProviderImpl
import org.dhis2.commons.prefs.SECURE_USER_NAME

object BiometricsClientFactory {
    fun get( context:Context):BiometricsClient{
        val preferences = BasicPreferenceProviderImpl(context)

        val projectId = preferences.getString(PROJECT_ID,"Ma9wi0IBdo215PKRXOf5")!!
        val userId = preferences.getString(SECURE_USER_NAME,"")!!
        val confidenceScoreFilter = preferences.getInt(CONFIDENCE_SCORE_FILTER,0)


        return BiometricsClient(projectId, userId, confidenceScoreFilter)
    }
}