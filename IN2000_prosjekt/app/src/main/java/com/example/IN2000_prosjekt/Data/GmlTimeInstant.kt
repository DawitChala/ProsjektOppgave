package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class GmlTimeInstant(
    @SerializedName("gml:id")
    val gmlId: String,
    @SerializedName("gml:timePosition")
    val gmlTimePosition: String
)