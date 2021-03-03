package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Timesery(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("time")
    val time: String
)