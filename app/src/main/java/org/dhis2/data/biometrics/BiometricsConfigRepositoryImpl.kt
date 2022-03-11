package org.dhis2.data.biometrics

import org.dhis2.commons.prefs.PreferenceProvider
import org.dhis2.usescases.biometrics.BiometricsConfigRepository
import org.dhis2.usescases.biometrics.BiometricsIcon
import timber.log.Timber
import java.lang.Exception

class BiometricsConfigRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val biometricsConfigApi: BiometricsConfigApi
) : BiometricsConfigRepository {

    override fun sync() {
        try {
            val response = biometricsConfigApi.getData().execute()

            val configOptions = response.body()
            val config = configOptions?.find { it.orgUnitGroup.toLowerCase() == "default" }

            if (response.isSuccessful && config != null) {

                preferenceProvider.setValue(BiometricsPreference.PROJECT_ID, config.projectId)
                preferenceProvider.setValue(
                    BiometricsPreference.CONFIDENCE_SCORE_FILTER,
                    config.confidenceScoreFilter?:0
                )

                val icon =
                    BiometricsIcon.values()
                        .firstOrNull { it.name == config.icon?.toUpperCase() }?.name
                        ?: BiometricsIcon.FINGERPRINT.name

                preferenceProvider.setValue(BiometricsPreference.ICON, icon)
                preferenceProvider.setValue(BiometricsPreference.LAST_VERIFICATION_DURATION, config.lastVerificationDuration?:0)


                Timber.d("downloadBiometricsConfig!")
                Timber.d("projectId: ${config.projectId}")
                Timber.d("confidenceScoreFilter: ${config.confidenceScoreFilter}")
                Timber.d("icon: $icon")
                Timber.d("lastVerificationDuration: ${config.lastVerificationDuration}")
            } else {
                Timber.e(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}