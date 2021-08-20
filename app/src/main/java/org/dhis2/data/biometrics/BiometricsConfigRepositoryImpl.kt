package org.dhis2.data.biometrics

import org.dhis2.data.prefs.PreferenceProvider
import org.dhis2.data.prefs.PreferenceProviderImpl
import org.dhis2.usescases.biometrics.BiometricsConfig
import org.dhis2.usescases.biometrics.BiometricsConfigRepository
import timber.log.Timber
import java.lang.Exception

class BiometricsConfigRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val biometricsConfigApi: BiometricsConfigApi
) : BiometricsConfigRepository {

    override fun sync() {
        try {
            val response = biometricsConfigApi.getData().execute()

            if (response.isSuccessful && response.body() != null) {
                val config = response.body()

                preferenceProvider.setValue(BiometricsPreference.PROJECT_ID, config?.projectId)

                Timber.d("downloadBiometricsConfig!")
                Timber.d("ProjectId: ${config?.projectId}")
            } else {
                Timber.e(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}