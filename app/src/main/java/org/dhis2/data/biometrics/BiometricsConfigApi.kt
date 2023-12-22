package org.dhis2.data.biometrics

import org.dhis2.usescases.biometrics.entities.BiometricsConfig
import retrofit2.Call
import retrofit2.http.GET

interface BiometricsConfigApi {
    @GET("dataStore/simprints/config")
    fun getData(): Call<List<BiometricsConfig>>
}