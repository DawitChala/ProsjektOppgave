package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Details(
    @SerializedName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: Double,
    @SerializedName("air_temperature")
    val airTemperature: Double,
    @SerializedName("cloud_area_fraction")
    val cloudAreaFraction: Double,
    @SerializedName("cloud_area_fraction_high")
    val cloudAreaFractionHigh: Double,
    @SerializedName("cloud_area_fraction_low")
    val cloudAreaFractionLow: Double,
    @SerializedName("cloud_area_fraction_medium")
    val cloudAreaFractionMedium: Double,
    @SerializedName("dew_point_temperature")
    val dewPointTemperature: Double,
    @SerializedName("relative_humidity")
    val relativeHumidity: Double,
    @SerializedName("wind_from_direction")
    val windFromDirection: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double
)