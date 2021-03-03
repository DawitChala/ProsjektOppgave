package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxMeanTotalWaveDirection(
    @SerializedName("content")
    val content: String,
    @SerializedName("uom")
    val uom: String
)