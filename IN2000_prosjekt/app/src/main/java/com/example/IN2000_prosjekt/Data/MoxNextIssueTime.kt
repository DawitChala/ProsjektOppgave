package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxNextIssueTime(
    @SerializedName("gml:TimeInstant")
    val gmlTimeInstant: GmlTimeInstantX
)