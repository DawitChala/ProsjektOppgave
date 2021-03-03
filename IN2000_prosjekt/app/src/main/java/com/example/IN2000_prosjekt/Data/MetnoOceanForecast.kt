package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class MetnoOceanForecast(
    @SerializedName("gml:id")
    val gmlId: String,
    @SerializedName("mox:meanTotalWaveDirection")
    val moxMeanTotalWaveDirection: MoxMeanTotalWaveDirection,
    @SerializedName("mox:seaBottomTopography")
    val moxSeaBottomTopography: MoxSeaBottomTopography,
    @SerializedName("mox:significantTotalWaveHeight")
    val moxSignificantTotalWaveHeight: MoxSignificantTotalWaveHeight,
    @SerializedName("mox:validTime")
    val moxValidTime: MoxValidTime,
    @SerializedName("mox:seaTemperature")
    val moxSeaTemperature: MoxSeaTemperature,
    @SerializedName("mox:seaCurrentDirection")
    val moxSeaCurrentDirection: MoxSeaCurrentDirection
)