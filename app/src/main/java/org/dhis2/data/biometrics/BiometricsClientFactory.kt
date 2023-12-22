package org.dhis2.data.biometrics

import android.content.Context
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.CONFIDENCE_SCORE_FILTER
import org.dhis2.commons.biometrics.BiometricsPreference.Companion.PROJECT_ID
import org.dhis2.commons.prefs.BasicPreferenceProviderImpl

object BiometricsClientFactory {
    fun get( context:Context):BiometricsClient{
        val preferences = BasicPreferenceProviderImpl(context)

        val projectId = preferences.getString(PROJECT_ID,"Ma9wi0IBdo215PKRXOf5")!!

        //Hardcoded
        //val userId = preferences.getString(SECURE_USER_NAME,"")!!
        val userId = "admin"
        val confidenceScoreFilter = preferences.getInt(CONFIDENCE_SCORE_FILTER,0)


        return BiometricsClient(projectId, userId, confidenceScoreFilter)
    }
}