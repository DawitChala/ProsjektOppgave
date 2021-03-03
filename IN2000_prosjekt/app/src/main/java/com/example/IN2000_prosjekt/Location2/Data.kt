package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("instant")
    val instant: Instant
)