package com.example.IN2000_prosjekt

import androidx.lifecycle.MutableLiveData
import com.example.IN2000_prosjekt.Data.OceanForecast
import com.example.IN2000_prosjekt.Location2.LocationAPI2
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


object MainRepository {
    private var oceanData = MutableLiveData<OceanForecast>()
    private var locationData = MutableLiveData<LocationAPI2>()

    fun hentOceanData(latLng: LatLng): MutableLiveData<OceanForecast> {
        val lat = latLng.latitude
        val lng = latLng.longitude
        val gson = Gson()
        var test: OceanForecast

        var response: String
        val oceanURL =
            "https://in2000-apiproxy.ifi.uio.no/weatherapi/oceanforecast/0.9/.json?lat=$lat&lon=$lng"

        CoroutineScope(Main).launch {
            try {
                response =
                    Fuel.get(oceanURL)
                        .awaitString()

                test = gson.fromJson(response, OceanForecast::class.java)

                oceanData.postValue(test)

            } catch (e: Exception) {
                print(e)
            }

        }
        return oceanData
    }


    fun hentLocationData(latLng: LatLng): MutableLiveData<LocationAPI2> {
        val lat = latLng.latitude
        val lng = latLng.longitude
        val gson = Gson()
        var loc: LocationAPI2

        val locURL =
            "https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/compact.json?altitude=1&lat=$lat&lon=$lng"


        CoroutineScope(Main).launch {
            try {
                val response =
                    Fuel.get(locURL)
                        .awaitString()

                loc = gson.fromJson(response, LocationAPI2::class.java)

                locationData.postValue(loc)


            } catch (e: Exception) {
                print(e)
            }
        }
        return locationData
    }


    fun clearData() {
        oceanData = MutableLiveData()
        locationData = MutableLiveData()

    }
}