package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxIssueTime(
    @SerializedName("gml:TimeInstant")
    val gmlTimeInstant: GmlTimeInstant
)