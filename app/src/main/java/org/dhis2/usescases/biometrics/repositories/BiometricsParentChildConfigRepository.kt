package org.dhis2.usescases.biometrics.repositories

import org.dhis2.usescases.biometrics.entities.BiometricsParentChildConfig

interface BiometricsParentChildConfigRepository {
    fun sync()
    fun get(): BiometricsParentChildConfig
}