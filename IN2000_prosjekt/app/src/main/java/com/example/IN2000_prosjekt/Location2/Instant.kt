package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Instant(
    @SerializedName("details")
    val details: Details
)