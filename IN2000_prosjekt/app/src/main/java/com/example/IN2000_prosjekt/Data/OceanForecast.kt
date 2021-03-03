package com.example.IN2000_prosjekt.Data
import com.google.gson.annotations.SerializedName

data class OceanForecast(
    @SerializedName("gml:description")
    val gmlDescription: String,
    @SerializedName("gml:id")
    val gmlId: String,
    @SerializedName("mox:forecast")
    val moxForecast: List<MoxForecast>,
    @SerializedName("mox:forecastPoint")
    val moxForecastPoint: MoxForecastPoint,
    @SerializedName("mox:issueTime")
    val moxIssueTime: MoxIssueTime,
    @SerializedName("mox:nextIssueTime")
    val moxNextIssueTime: MoxNextIssueTime,
    @SerializedName("mox:observedProperty")
    val moxObservedProperty: MoxObservedProperty,
    @SerializedName("mox:procedure")
    val moxProcedure: MoxProcedure,
    @SerializedName("xmlns:gml")
    val xmlnsGml: String,
    @SerializedName("xmlns:metno")
    val xmlnsMetno: String,
    @SerializedName("xmlns:mox")
    val xmlnsMox: String,
    @SerializedName("xmlns:xlink")
    val xmlnsXlink: String,
    @SerializedName("xsi:schemaLocation")
    val xsiSchemaLocation: String
)