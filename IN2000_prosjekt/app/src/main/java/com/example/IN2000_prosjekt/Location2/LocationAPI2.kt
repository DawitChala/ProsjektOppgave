package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class LocationAPI2(
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("type")
    val type: String
)