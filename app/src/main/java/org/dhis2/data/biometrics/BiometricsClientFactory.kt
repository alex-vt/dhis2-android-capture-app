package org.dhis2.data.biometrics

import android.content.Context
import org.dhis2.data.biometrics.BiometricsPreference.Companion.MODULE_ID
import org.dhis2.data.biometrics.BiometricsPreference.Companion.PROJECT_ID
import org.dhis2.data.biometrics.BiometricsPreference.Companion.USER_ID
import org.dhis2.data.prefs.PreferenceProviderImpl

object BiometricsClientFactory {
    fun get( context:Context):BiometricsClient{
        val preferences = PreferenceProviderImpl(context)

        val projectId = preferences.getString(PROJECT_ID,"Ma9wi0IBdo215PKRXOf5")!!
        val userId = preferences.getString(USER_ID,"android")!!
        val moduleId = preferences.getString(MODULE_ID,"MODULE ID")!!

        return BiometricsClient(projectId, userId, moduleId)
    }
}