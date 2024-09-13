package org.dhis2.data.biometrics

import org.dhis2.usescases.biometrics.entities.BiometricsParentChildConfig
import retrofit2.Call
import retrofit2.http.GET

interface BiometricsParentChildConfigApi {
    @GET("dataStore/simprints/parentChildConfig")
    fun getData(): Call<BiometricsParentChildConfig>
}