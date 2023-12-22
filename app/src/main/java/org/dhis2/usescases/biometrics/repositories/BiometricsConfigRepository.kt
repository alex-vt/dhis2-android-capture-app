package org.dhis2.usescases.biometrics.repositories

import org.dhis2.usescases.biometrics.entities.BiometricsConfig

interface BiometricsConfigRepository {
    fun sync()
    fun getUserOrgUnitGroups(): List<String>
    fun getBiometricsConfigs(): List<BiometricsConfig>
    fun saveSelectedConfig(config: BiometricsConfig)
}