package com.example.IN2000_prosjekt.Location2
import com.google.gson.annotations.SerializedName

data class Units(
    @SerializedName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: String,
    @SerializedName("air_temperature")
    val airTemperature: String,
    @SerializedName("air_temperature_max")
    val airTemperatureMax: String,
    @SerializedName("air_temperature_min")
    val airTemperatureMin: String,
    @SerializedName("cloud_area_fraction")
    val cloudAreaFraction: String,
    @SerializedName("cloud_area_fraction_high")
    val cloudAreaFractionHigh: String,
    @SerializedName("cloud_area_fraction_low")
    val cloudAreaFractionLow: String,
    @SerializedName("cloud_area_fraction_medium")
    val cloudAreaFractionMedium: String,
    @SerializedName("dew_point_temperature")
    val dewPointTemperature: String,
    @SerializedName("fog_area_fraction")
    val fogAreaFraction: String,
    @SerializedName("precipitation_amount")
    val precipitationAmount: String,
    @SerializedName("precipitation_amount_max")
    val precipitationAmountMax: String,
    @SerializedName("precipitation_amount_min")
    val precipitationAmountMin: String,
    @SerializedName("probability_of_precipitation")
    val probabilityOfPrecipitation: String,
    @SerializedName("probability_of_thunder")
    val probabilityOfThunder: String,
    @SerializedName("relative_humidity")
    val relativeHumidity: String,
    @SerializedName("ultraviolet_index_clear_sky")
    val ultravioletIndexClearSky: String,
    @SerializedName("wind_from_direction")
    val windFromDirection: String,
    @SerializedName("wind_speed")
    val windSpeed: String,
    @SerializedName("wind_speed_of_gust")
    val windSpeedOfGust: String
)