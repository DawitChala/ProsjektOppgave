package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("timeseries")
    val timeseries: List<Timesery>
)