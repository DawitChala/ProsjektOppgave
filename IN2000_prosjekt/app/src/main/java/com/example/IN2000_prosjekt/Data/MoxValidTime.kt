package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MoxValidTime(
    @SerializedName("gml:TimePeriod")
    val gmlTimePeriod: GmlTimePeriod
)