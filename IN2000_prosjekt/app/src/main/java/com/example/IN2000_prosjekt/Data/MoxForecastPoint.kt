package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxForecastPoint(
    @SerializedName("gml:Point")
    val gmlPoint: GmlPoint
)