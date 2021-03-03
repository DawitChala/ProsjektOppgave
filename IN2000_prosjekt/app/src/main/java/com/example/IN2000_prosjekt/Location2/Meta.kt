package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("units")
    val units: Units,
    @SerializedName("updated_at")
    val updatedAt: String
)