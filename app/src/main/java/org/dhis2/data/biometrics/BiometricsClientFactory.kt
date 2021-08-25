package org.dhis2.data.biometrics

import android.content.Context
import org.dhis2.data.biometrics.BiometricsPreference.Companion.PROJECT_ID
import org.dhis2.data.prefs.PreferenceProviderImpl
import org.dhis2.utils.Constants

object BiometricsClientFactory {
    fun get( context:Context):BiometricsClient{
        val preferences = PreferenceProviderImpl(context)

        val projectId = preferences.getString(PROJECT_ID,"Ma9wi0IBdo215PKRXOf5")!!
        val userId = preferences.getString(Constants.SECURE_USER_NAME,"")!!

        return BiometricsClient(projectId, userId)
    }
}