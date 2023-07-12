package org.dhis2.data.biometrics

import org.dhis2.commons.biometrics.BiometricsIcon
import org.dhis2.commons.biometrics.BiometricsPreference
import org.dhis2.commons.prefs.PreferenceProvider
import org.dhis2.usescases.biometrics.BiometricsConfig
import org.dhis2.usescases.biometrics.BiometricsConfigRepository
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import timber.log.Timber
import java.lang.Exception

class BiometricsConfigRepositoryImpl(
    private val d2: D2,
    private val preferenceProvider: PreferenceProvider,
    private val biometricsConfigApi: BiometricsConfigApi
) : BiometricsConfigRepository {

    override fun sync() {
        try {
            val response = biometricsConfigApi.getData().execute()

            val configOptions = response.body()

            if (response.isSuccessful && configOptions != null) {
                val config = getSelectedConfig(configOptions)

                preferenceProvider.setValue(BiometricsPreference.PROJECT_ID, config.projectId)
                preferenceProvider.setValue(
                    BiometricsPreference.CONFIDENCE_SCORE_FILTER,
                    config.confidenceScoreFilter ?: 0
                )

                val icon =
                    BiometricsIcon.values()
                        .firstOrNull { it.name == config.icon?.toUpperCase() }?.name
                        ?: BiometricsIcon.FINGERPRINT.name

                preferenceProvider.setValue(BiometricsPreference.ICON, icon)
                preferenceProvider.setValue(
                    BiometricsPreference.LAST_VERIFICATION_DURATION,
                    config.lastVerificationDuration ?: 0
                )

                Timber.d("downloadBiometricsConfig!")
                Timber.d("orgUnitGroup: ${config.orgUnitGroup}")
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

    private fun getSelectedConfig(configOptions: List<BiometricsConfig>): BiometricsConfig {
        val userOrgUnitGroups =
            d2.organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .withOrganisationUnitGroups()
                .blockingGet().flatMap { ou ->
                    if (ou.organisationUnitGroups() != null) ou.organisationUnitGroups()!!
                        .map { ouGroup -> ouGroup.uid() }
                    else listOf()
                }.distinct()

        val userOrgUnitGroupsInConfig =
            userOrgUnitGroups.filter{ouGroup -> configOptions.any{config -> config.orgUnitGroup == ouGroup}}

        val defaultConfig =
            configOptions.find { it.orgUnitGroup.toLowerCase() == "default" }

        if (defaultConfig == null) {
            val error = "There are not a default biometrics config"
            Timber.e(error)
            throw Exception(error)
        }

        return if (userOrgUnitGroupsInConfig.size != 1) defaultConfig else
            configOptions.find { it.orgUnitGroup == userOrgUnitGroupsInConfig[0] }
                ?: defaultConfig
    }
}