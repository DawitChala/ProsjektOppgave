package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxForecast(
    @SerializedName("metno:OceanForecast")
    val metnoOceanForecast: MetnoOceanForecast
)