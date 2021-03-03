package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class GmlPoint(
    @SerializedName("gml:id")
    val gmlId: String,
    @SerializedName("gml:pos")
    val gmlPos: String,
    @SerializedName("srsName")
    val srsName: String
)