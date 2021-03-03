package com.example.IN2000_prosjekt


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.findFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import com.example.IN2000_prosjekt.Data.OceanForecast
import com.example.IN2000_prosjekt.Database.DBhandler
import com.example.IN2000_prosjekt.Database.Punkt
import com.example.IN2000_prosjekt.Location2.LocationAPI2
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.hjem_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainViewModel: ViewModel() {
    var oceanData = MutableLiveData<OceanForecast>()
    var locationData = MutableLiveData<LocationAPI2>()
    var oceanTegneData = MutableLiveData<OceanForecast>()
    var locationTegneData = MutableLiveData<LocationAPI2>()



    private val permissionId = 42
    lateinit var currentLocation: Location

    //Variabler for aa gi brukeren data i infoboks
    private var vanntemp = ""
    private var stromning = ""
    private var windSpeed = 0.0
    private var bolgeHoyde = ""
    private var retning = ""
    private var bolgeVei = 0.0

    //Strings
    private var dato2 = ""
    private var tempviser = ""
    private var stromningviser = ""
    private var bolgeviser = ""
    private var bolgeHoydeViser = ""
    private var klokkeviser = ""

    //Hvis det er en tom string så får vi ikke havdata. Kunne vært boolean
    private var fangNull = "H"

    //Brukes for aa klokkefunksjonen - passe paa at tiden ikke overstrider det vi har data for
    private var sjekkTid = ""
    private var antPluss = 0
    private var k = 0

    //Variabel for aa tegne rute, skal ikke tegnes for forste Punkt er tegnet
    var forst = true
    var forsteTrykk = true

    //Hvis det skjer en feil så blir feil på. Hvis tegnemodus er på så er tegne true
    var feil = false
    var tegne = false

    //Siste kordinat som vi fikk fra bruker
    private var sisteKordinat = LatLng(0.0, 0.0)

    //Variabler for aa sjekke rutens trygghetsgrad:
    private var forrigeVind = 0.0
    private var mellomVind = 0.0
    private var mellomVind2 = 0.0
    private var mellomVind3 = 0.0
    private var currVind = 0.0
    private var forrigeTemp = 0.0
    private var mellomTemp = 0.0
    private var mellomTemp2 = 0.0
    private var mellomTemp3 = 0.0
    private var currTemp = 0.0
    private var vindRetning = 0.0
    private var mellomV = false
    private var mellomT = false
    private var mellomV2 = false
    private var mellomT2 = false
    private var mellomV3 = false
    private var mellomT3 = false
    private var lat2 = 0.0
    private var lng2 = 0.0
    private var lat3 = 0.0
    private var lng3 = 0.0
    private var lat = 0.0
    private var lng = 0.0
    private var ferdig = false

    //Variabler for klokkefunksjonen:
    private val mndMedTrettiDager = listOf(4, 6, 9, 11)
    private val mndMedTrettienDager = listOf(1, 3, 5, 7, 8, 10, 12)
    private var nyTid = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
    private var nyDag = SimpleDateFormat("dd", Locale.getDefault()).format(Date()).toInt()
    private var nyMaaned = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toInt()
    private var strMnd = ""

    //Statiske variabler
    companion object {
        private var listOfCordinatas = mutableListOf<LatLng>()

        //Vi er klar over at vi ikke burde ha referanser til aktivitet i ViewModel
        //Men vi fikk ikke tid til å gjøre det uten
        @SuppressLint("StaticFieldLeak")
        var activity: Activity? = null
        var map: GoogleMap? = null
        @SuppressLint("StaticFieldLeak")
        var fusedLocationClient : FusedLocationProviderClient? = null
        private var listOfLines = mutableListOf<Polyline>()
        var database: DBhandler? = null
        private var listOfMarkers = mutableListOf<Marker>()
        private var listofRealCordinatas = mutableListOf<LatLng>()
        private var listOfEkstraMarkers = mutableListOf<Marker>()

        var setNmr = 0

        var listOFsavedCoord = mutableListOf<LatLng>()
    }

    fun getListOfMarkers(): MutableList<Marker>{return listOfMarkers}
    fun getPermissionId(): Int{return permissionId}

    //Endrer på fargen på tegneknappen får å indikere for brukeren om tegne funksjonen er aktiv eller ikke.
    fun tegne() {
        if(!tegne){
            Toast.makeText(activity,"Tegnemodus: På",Toast.LENGTH_SHORT).show()
            ImageViewCompat.setImageTintList(
                activity!!.fabEdit,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity!!.applicationContext,
                        R.color.varselGronn
                    )
                )
            )
        }else{
            endreData()
            Toast.makeText(activity,"Tegnemodus: Av",Toast.LENGTH_SHORT).show()
            ImageViewCompat.setImageTintList(
                activity!!.fabEdit,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity!!.applicationContext,
                        R.color.colorAccent
                    )
                )
            )
        }

        tegne = !tegne
    }

    //Metode som brukes i MainActivity for oversikt over views
    fun viewHenter(newActivity: Activity) {
        activity = newActivity
    }

    //Gjør feil om til false
    fun fiksFeil() {
        feil = false
    }

    //resetter data
    fun endreData() {
        forst = true
        oceanTegneData = MutableLiveData()
        locationTegneData = MutableLiveData()
        MainRepository.clearData()
    }

    fun hentData(latLng: LatLng) {
        sisteKordinat = latLng
        oceanData = MainRepository.hentOceanData(latLng)
        locationData = MainRepository.hentLocationData(latLng)
    }

    //Henter data til tegning av rute
    private fun hentTegneData(latLng: LatLng) {
        oceanTegneData = MainRepository.hentOceanData(latLng)
        locationTegneData = MainRepository.hentLocationData(latLng)
    }


    //Metode som tar bort linjene fra kartet


    fun clearMap(map: GoogleMap) {
        map.clear()
        listOfMarkers.clear()
        listOfCordinatas.clear()
        listOfLines.clear()
        if(setNmr == 0){
            listofRealCordinatas.clear()
        }
        endreData()
        fiksFeil()
        forrigeVind = 0.0
        mellomVind = 0.0
        mellomVind2 = 0.0
        mellomVind3 = 0.0
        currVind = 0.0
        forrigeTemp = 0.0
        mellomTemp = 0.0
        mellomTemp2 = 0.0
        mellomTemp3 = 0.0
        currTemp = 0.0
        vindRetning = 0.0
        mellomV = false
        mellomT = false
        mellomV2 = false
        mellomT2 = false
        mellomV3 = false
        mellomT3 = false
        forst = true
        forsteTrykk = true
        lat2 = 0.0
        lng2 = 0.0
        lat3 = 0.0
        lng3 = 0.0
        lat = 0.0
        lng = 0.0
    }

    //Henter data fra API, om det er et Punkt som det inne finnes data informeres brukeren med toast
    fun settOceanVariabler(it: OceanForecast) {
        fangNull = ""

        try {
            vanntemp = it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content
            bolgeHoyde = it.moxForecast[k].metnoOceanForecast.moxSignificantTotalWaveHeight.content
            stromning = it.moxForecast[k].metnoOceanForecast.moxSeaCurrentDirection.content
            fangNull = "H"
        } catch (e: NullPointerException) {
            Toast.makeText(activity, "Ingen data på denne lokasjonen", Toast.LENGTH_SHORT).show()
            fangNull = ""
        }
    }


    /*
    fjerner forrige linje, markør og koordinat lagt til på kartet.
     */
    fun fjernForrige(){
        if(listOfCordinatas.size > 0) {
            listOfCordinatas.removeAt(listOfCordinatas.size - 1)
        }
        else{
            forsteTrykk = true
        }
        if(listOfLines.size > 0){
            listOfLines[listOfLines.size-1].remove()
            listOfLines.removeAt(listOfLines.size-1)
        }
        else{
            forsteTrykk = true
        }
        if(listOfEkstraMarkers.size > 0){
            print("\n\n\nflere ekstra markers\n\n\n")
            listOfEkstraMarkers[listOfEkstraMarkers.size-1].remove()
            listOfEkstraMarkers.removeAt(listOfEkstraMarkers.size-1)
        }
        if(listOfMarkers.size > 0){
            print("\n\n\nflere markers\n\n\n")
            listOfMarkers[listOfMarkers.size-1].remove()
            listOfMarkers.removeAt(listOfMarkers.size-1)
        }
        else{
            forsteTrykk = true
        }
        if(listofRealCordinatas.size > 0){
            listofRealCordinatas.removeAt(listofRealCordinatas.size-1)
        }
        else{
            forsteTrykk = true
        }

    }

    //Setter varseltrekanten i infoboksen til rett farge ut i fra trygghetsgradering
    private fun oppdaterFarge() {
        try {
            if (windSpeed >= 6.0 || vanntemp.toDouble() <= 12.0) {
                ImageViewCompat.setImageTintList(
                    activity!!.Trekant,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.varselRod
                        )
                    )
                )
            } else if ((6.0 > windSpeed && windSpeed >= 5.0) || (12.0 < vanntemp.toDouble() && vanntemp.toDouble() < 15.0) || bolgeHoyde.toDouble() >= 1.0) {
                ImageViewCompat.setImageTintList(
                    activity!!.Trekant,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.varselGul
                        )
                    )
                )
            } else {
                ImageViewCompat.setImageTintList(
                    activity!!.Trekant,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.varselGronn
                        )
                    )
                )
            }
        } catch (e: NumberFormatException) {
            ImageViewCompat.setImageTintList(
                activity!!.Trekant,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        activity!!.applicationContext,
                        R.color.varselGul
                    )
                )
            )
        }
    }

    //Viser riktig data i infoboksen
    fun visVaer(pin: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            if (fangNull == "H") {
                oppdaterFarge()
                settMaaned()
                try {
                    klokkeviser = sjekkTid + ":00 - " + (sjekkTid.toInt() + 1) + ":00"
                    activity?.klokkeviser?.text = klokkeviser
                }
                catch (e:Exception){
                    print(e)
                }

                tempviser = "Vanntemperatur: $vanntemp°C"
                activity?.tempviser?.text = tempviser

                stromningviser = "Stromninger: $stromning $retning"
                activity?.stromningviser?.text = stromningviser

                bolgeviser = "Vind: $windSpeed m/s $retning"
                activity?.bolgeviser?.text = bolgeviser

                bolgeHoydeViser = "Bølgehøyde: " + bolgeHoyde + "m"
                activity?.bolgeHoydeViser?.text = bolgeHoydeViser

                activity?.vaermelding?.visibility = View.VISIBLE
                if (!pin) {
                    activity?.pin?.visibility = View.VISIBLE
                }

                activity?.Trekant?.visibility = View.VISIBLE

                dato2 = "$nyDag.$strMnd"
                activity?.dato2?.text = dato2
            }
        }
    }

    //Setterr graden som leses inn fra API-et til retning
    fun settLocVariabler(it: LocationAPI2) {
        windSpeed = it.properties.timeseries[k].data.instant.details.windSpeed
        bolgeVei = it.properties.timeseries[k].data.instant.details.windFromDirection

        if (bolgeVei < 22.5 || bolgeVei > 335) {
            retning = "N"
        } else if (bolgeVei > 22.5 && bolgeVei < 67.5) {
            retning = "NE"
        } else if (bolgeVei > 67.5 && bolgeVei < 112.5) {
            retning = "E"
        } else if (bolgeVei > 112.5 && bolgeVei < 147.5) {
            retning = "SE"
        } else if (bolgeVei > 147.5 && bolgeVei < 202.5) {
            retning = "S"
        } else if (bolgeVei > 202.5 && bolgeVei < 247.5) {
            retning = "SW"
        } else if (bolgeVei > 247.5 && bolgeVei < 302.5) {
            retning = "W"
        } else {
            retning = "NW"
        }
        if (activity?.vaermelding?.isVisible == true) {
            oppdaterVaer()
        }
    }



        //Gjor slik at infoboksen blir borte fra skjermen
        fun gjorUsynlig() {
            activity?.klokkeviser?.text = ""
            activity?.vaermelding?.visibility = View.INVISIBLE
            activity?.stromningviser?.text = ""
            activity?.tempviser?.text = ""
            activity?.bolgeviser?.text = ""
            activity?.bolgeHoydeViser?.text = ""
            activity?.dato2?.text = ""
            activity?.pin?.visibility = View.INVISIBLE
            activity?.Trekant?.visibility = View.GONE
        }


        /*Denne metoden kalles når brukeren laster inn "HjemFragment" foruten når brukeren henter en lagret rute.
        Metoden tegner alle linjer og markører som var på kartet før "HjemFragment" ble avsluttet.
         */
        fun tegnTidligere(googleMap: GoogleMap) {

            print("\n\n\n\nlinje punkter: ")
            print(listOfLines.size)
            print("\n\n\n\n")
            if (listOfLines.size != 0) {
                forsteTrykk = false
                for (linjeTeller in 0 until listOfLines.size) {
                    val linje = listOfLines[linjeTeller]
                    listOfLines[linjeTeller] = googleMap.addPolyline(
                        PolylineOptions()
                            .add(
                                linje.points[0],
                                linje.points[1]
                            )
                            .width(linje.width)
                            .color(linje.color)
                    )
                    linje.remove()
                }
                for (markTeller in 0 until listOfMarkers.size) {
                    val marker = listOfMarkers[markTeller]
                    val markerOptions = MarkerOptions()
                    listOfCordinatas[markTeller] = marker.position
                    listofRealCordinatas.add(marker.position)
                    markerOptions.position(marker.position)
                    markerOptions.visible(marker.isVisible)
                    markerOptions.anchor(0.5f, 0.5f)
                    markerOptions.icon(
                        generateBitmapDescriptorFromRes(
                            activity!!.baseContext
                        )
                    )
                    marker.remove()
                    listOfMarkers[markTeller] = googleMap.addMarker(markerOptions)
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(listOfCordinatas[0]))
            }
        }

        /*
        Kalles på når brukeren trykker på ferdig-knappen.
        lager markører basert på tidligere markører laget ved klikking.
        Markørene vises på kartet, og kan dermed trykkes på for å få data fra koordinatet til markøren
         */
        fun linjerFerdig(mMap: GoogleMap) {

            for (markTeller in 0 until listOfMarkers.size) {
                val mark = MarkerOptions()
                val marker= listOfMarkers[markTeller]
                mark.visible(true)
                mark.position(marker.position)
                mark.anchor(0.5f, 0.5f)
                mark.icon(
                    generateBitmapDescriptorFromRes(
                        activity!!.baseContext
                    )
                )
                marker.remove()
                listOfMarkers[markTeller] = mMap.addMarker(mark)
            }
        }


        /*
        Lager og returnerer en BitMapDescriptor som brukes for markørene
        BitMapDescriptoren blir laget ved å hente dataen fra en Vector fil, og bruker deretter draweble for å tegne et bitmap.
        inspirert av https://gist.github.com/EfeBudak/a9ec13d9d582c6cc7f11156e6775ea47
         */
        private fun generateBitmapDescriptorFromRes(
            context: Context
        ): BitmapDescriptor {
            val resId = R.drawable.ic_pin
            val drawable = ContextCompat.getDrawable(context, resId)
            drawable!!.setBounds(
                0,
                0,
                drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }



        //Kalles paa dersom brukeren klikker på kartet. Starter hentingen av data og lager en markør som kan brukes senere på kartet.
        fun klikket(latLng: LatLng, googleMap: GoogleMap) {
            if (!feil) {
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.visible(false)
                markerOptions.anchor(0.5f, 0.5f)
                markerOptions.icon(
                    generateBitmapDescriptorFromRes(
                        activity!!.baseContext
                    )
                )
                listOfMarkers.add(googleMap.addMarker(markerOptions))
                listOfCordinatas.add(latLng)
                listofRealCordinatas.add(latLng)
                hentTegneData(latLng)
            }
        }

        /*Setter verdier til globale variabler instansiert tidligere.
        Metoden danner og henter data fra 3 koordinater mellom de to punktene som blir brukt for å tegne linjene.
        */
        fun settPunkterHav(it: OceanForecast) {
            feil = false
            val size = listOfLines.size
            print("\n\n\n\n$size\n\n\n")
            /*
            En when metode blir brukt for å sjekke hvilke av de globale variablene som skal gis verdi fra dataen.
            */
            when {

                forsteTrykk -> {
                    print("\n\n\n dette skal ikke skje mer enn en gang\n\n\n")
                    try {
                        currTemp =
                            it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content.toDouble()
                    } catch (e: NullPointerException) {
                        print(e)
                        feil = true
                        return
                    }
                }
                mellomT -> {
                    try {
                        mellomTemp =
                            it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content.toDouble()
                        mellomT = false
                    } catch (e: NullPointerException) {
                        print(e)
                        feil = true
                        return
                    }
                }

                mellomT2 -> {
                    try {
                        mellomTemp2 =
                            it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content.toDouble()
                        mellomT2 = false
                        mellomT = true
                    } catch (e: NullPointerException) {
                        print(e)
                        feil = true
                        return
                    }
                }

                mellomT3 -> {
                    try {
                        mellomTemp3 =
                            it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content.toDouble()
                        mellomT3 = false
                        mellomT2 = true
                    } catch (e: NullPointerException) {
                        print(e)
                        feil = true
                        return
                    }
                }

                else -> {
                    try {
                        forrigeTemp = currTemp
                        currTemp =
                            it.moxForecast[k].metnoOceanForecast.moxSeaTemperature.content.toDouble()
                        mellomT3 = true
                    } catch (e: NullPointerException) {
                        print(e)
                        feil = true
                        return
                    }
                }
            }
        }


        /*Setter verdier til globale variabler instansiert tidligere.
        Dersom ingen punkter er trykket på tidligere setter den kun den første av variablene og kaller ikke på metoden som lager en Polyline på kartet.
        Ellers repiters metoden flere ganger ved å kalle på tegneData flere ganger.
        Metoden danner og henter data fra 3 koordinater mellom de to punktene som blir brukt for å tegne linjene.
        */
        fun settPunkterVind(it: LocationAPI2, mMap: GoogleMap) {
            windSpeed = it.properties.timeseries[k].data.instant.details.windSpeed
            vindRetning = it.properties.timeseries[k].data.instant.details.windFromDirection

            if (feil) { //Dersom brukeren har trykket på land vil "feil" være sann
                clearMap(mMap)
                return
            }
            /*
            En when metode blir brukt for å sjekke hvilke av de globale variablene som skal gis verdi fra dataen.
            */
            when {
                 forsteTrykk -> {
                     currVind =
                         it.properties.timeseries[0].data.instant.details.windSpeed

                 }
                mellomV -> {
                    mellomVind = it.properties.timeseries[k].data.instant.details.windSpeed
                    linjeFarge(mMap)
                    mellomV = false

                }
                mellomV2 -> {
                    mellomVind2 = it.properties.timeseries[k].data.instant.details.windSpeed
                    //linjeFarge(mMap)
                    mellomV2 = false
                    mellomV = true
                    val mellomLL = LatLng(lat, lng)
                    val mark = MarkerOptions()
                    mark.position(mellomLL)
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    mark.visible(false)
                    mMap.addMarker(mark)
                    hentTegneData(mellomLL)

                }
                mellomV3 -> {
                    mellomVind3 = it.properties.timeseries[k].data.instant.details.windSpeed
                    //linjeFarge(mMap)
                    mellomV3 = false
                    mellomV2 = true
                    val mellom2 = LatLng(lat2, lng2)
                    val mark = MarkerOptions()
                    mark.position(mellom2)
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    mark.visible(false)
                    mMap.addMarker(mark)
                    hentTegneData(mellom2)
                }
                else -> {
                    forrigeVind = currVind
                    currVind = it.properties.timeseries[k].data.instant.details.windSpeed
                    mellomV3 = true

                    /*
                    De neste linjene med kode regner ut koordinat posisjonene som brukes for å samle gjennomsnittsdata mellom de to trykkede punktene
                    og kaller deretter på hentTegneData metoden igjen med de nye koordinatene.
                     */
                    val latDifforg =
                        abs(listOfCordinatas[listOfCordinatas.size - 1].latitude - listOfCordinatas[listOfCordinatas.size - 2].latitude)
                    val lngDifforg =
                        abs(listOfCordinatas[listOfCordinatas.size - 1].longitude - listOfCordinatas[listOfCordinatas.size - 2].longitude)
                    val latDiff = latDifforg / 2
                    val lngDiff = lngDifforg / 2

                    if (listOfCordinatas[listOfCordinatas.size - 1].longitude > listOfCordinatas[listOfCordinatas.size - 2].longitude) {
                        lng = listOfCordinatas[listOfCordinatas.size - 2].longitude + lngDiff
                        lng2 = lng - (lngDiff / 2)
                        lng3 = lng + (lngDiff / 2)
                    } else {
                        lng = listOfCordinatas[listOfCordinatas.size - 1].longitude + lngDiff
                        lng2 = lng + (lngDiff / 2)
                        lng3 = lng - (lngDiff / 2)
                    }

                    if (listOfCordinatas[listOfCordinatas.size - 1].latitude > listOfCordinatas[listOfCordinatas.size - 2].latitude) {
                        lat = listOfCordinatas[listOfCordinatas.size - 2].latitude + latDiff
                        lat2 = lat - (latDiff / 2)
                        lat3 = lat + (latDiff / 2)
                    } else {
                        lat = listOfCordinatas[listOfCordinatas.size - 1].latitude + latDiff
                        lat2 = lat + (latDiff / 2)
                        lat3 = lat - (latDiff / 2)
                    }

                    val mellom3 = LatLng(lat3, lng3)
                    val mark = MarkerOptions()
                    mark.position(mellom3)
                    mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    mark.visible(false)
                    mMap.addMarker(mark)
                    hentTegneData(mellom3)
                }
            }

            forsteTrykk = false

        }

        //Oppdaterer linjen til rett farge og legger dem til kartet
        private fun linjeFarge(googleMap: GoogleMap) {

            val vindStyrke = (forrigeVind + currVind + mellomVind + mellomVind2 + mellomVind3) / 5
            val temp = (forrigeTemp + currTemp + mellomTemp + mellomTemp2 + mellomTemp3) / 5


            if (vindStyrke >= 6.0 || temp <= 12.0) {
                listOfLines.add(
                    googleMap.addPolyline(
                        PolylineOptions()
                            .add(
                                listOfCordinatas[listOfCordinatas.size - 1],
                                listOfCordinatas[listOfCordinatas.size - 2]
                            )
                            .width(8f)
                            .color(Color.RED)
                    )
                )
            } else if ((6.0 > vindStyrke && vindStyrke >= 5.0) || (12.0 < temp && temp <= 15.0)  || bolgeHoyde.toDouble() >= 1.0) {

                listOfLines.add(
                    googleMap.addPolyline(
                        PolylineOptions()
                            .add(
                                listOfCordinatas[listOfCordinatas.size - 1],
                                listOfCordinatas[listOfCordinatas.size - 2]
                            )
                            .width(8f)
                            .color(Color.YELLOW)
                    )
                )
            } else {
                listOfLines.add(
                    googleMap.addPolyline(
                        PolylineOptions()
                            .add(
                                listOfCordinatas[listOfCordinatas.size - 1],
                                listOfCordinatas[listOfCordinatas.size - 2]
                            )
                            .width(8f)
                            .color((Color.GREEN))
                    )
                )

            }
            forsteTrykk = false
        }


        //Initiallierer klokken og sørger for at henting av data gjøres fra riktig tidspunkt basert på hva brukeren har satt på klokken.
        fun settOppKlokke(it: OceanForecast) {
            k = 0
            while (true) {
                try {
                    sjekkTid =
                        it.moxForecast[k].metnoOceanForecast.moxValidTime.gmlTimePeriod.gmlBegin
                    val sjekkDag = sjekkTid.substring(8, 10)

                    sjekkTid = sjekkTid.substring(11, 13)

                    if (sjekkTid.toInt() == nyTid && sjekkDag.toInt() == nyDag) {
                        break
                    } else {
                        k++
                    }
                } catch (e: Exception) {
                    print(e)
                    return
                }
            }
        }

        //Viser klokken naar brukeren trykker paa klokken i fab-menyen. Starter paa naatid
        private fun visKlokke() {
            activity?.klokkebakgrunn?.visibility = View.VISIBLE
            activity?.minusKnapp?.visibility = View.VISIBLE
            activity?.plussKnapp?.visibility = View.VISIBLE
            activity?.dato?.visibility = View.VISIBLE

            var toastTid = nyTid.toString()
            var toastDato = nyDag.toString()

            if (nyTid < 10) {
                toastTid = "0$toastTid:00"
            } else {
                toastTid += ":00"
            }

            toastDato = if (nyMaaned < 10) {
                "$toastDato.0$nyMaaned"
            } else {
                "$toastDato.$nyMaaned"
            }
            activity?.dato?.text = toastDato
            activity?.tid?.text = toastTid

        }

        //Fjerner eller syneliggjør klokken fra skjermen
        fun oppdaterKlokke() {
            if (activity?.klokkebakgrunn?.isVisible == true) {
                skjulKlokke()
            } else {
                visKlokke()
            }
        }

        //Tar bort klokken fra skjermen naar brukeren skal bruke andre funksjoner
        fun skjulKlokke() {
            activity?.klokkebakgrunn?.visibility = View.INVISIBLE
            activity?.tid?.text = ""
            activity?.minusKnapp?.visibility = View.GONE
            activity?.plussKnapp?.visibility = View.GONE
            activity?.dato?.visibility = View.GONE
        }


        //Oppdaterer vaeret mens/naar klokken har blitt endret
        private fun oppdaterVaer() {
            CoroutineScope(Dispatchers.Main).launch {
                oppdaterFarge()
                settMaaned()


                if (nyTid < 10) {
                   try {
                       klokkeviser =
                           "0" + nyTid.toString() + ":00 - " + (nyTid + 1).toString() + ":00"
                       activity?.klokkeviser?.text = klokkeviser
                   }
                   catch (e:Exception){
                       print(e)
                   }

                } else {
                    klokkeviser = nyTid.toString() + ":00 - " + (nyTid + 1).toString() + ":00"
                    activity?.klokkeviser?.text = klokkeviser

                }
                dato2 = "$nyDag.$strMnd"
                activity?.dato2?.text = dato2

                tempviser = "Vanntemperatur: $vanntemp°C"
                activity?.tempviser?.text = tempviser

                stromningviser = "Stromninger: $stromning $retning"
                activity?.stromningviser?.text = stromningviser

                bolgeviser = "Vind: $windSpeed m/s $retning"
                activity?.bolgeviser?.text = bolgeviser

                bolgeHoydeViser = "Bolgehøyde: " + bolgeHoyde + "m"
                activity?.bolgeHoydeViser?.text = bolgeHoydeViser

                activity?.vaermelding?.visibility = View.VISIBLE

            }
        }

        //Setter verdien til måneden for å sørge for å sjekke om klokken skal gå til neste dato etter 30, 31, 28 eller 29 dager
        private fun settMaaned() {
            when (nyMaaned) {
                1 -> strMnd = "Januar"
                2 -> strMnd = "Februar"
                3 -> strMnd = "Mars"
                4 -> strMnd = "April"
                5 -> strMnd = "Mai"
                6 -> strMnd = "Juni"
                7 -> strMnd = "Juli"
                8 -> strMnd = "August"
                9 -> strMnd = "September"
                10 -> strMnd = "Oktober"
                11 -> strMnd = "November"
                12 -> strMnd = "Desember"
            }
        }

        //øker variablen vist på klokke-Viewet og oppdaterer dataen basert på den nye tiden
        fun plussKlokka() {
            if (antPluss < 48) {
                nyTid++
                if (nyTid >= 24) {
                    nyDag++
                    nyTid = 0
                    if (nyDag == 31 && nyMaaned in mndMedTrettiDager) {
                        nyDag = 1
                        nyMaaned++
                    } else if (nyDag == 32 && nyMaaned in mndMedTrettienDager) {
                        nyDag = 1
                        nyMaaned++
                    } else if (nyDag == 29 && nyMaaned == 2) {
                        nyDag = 1
                        nyMaaned++
                    }
                }
                antPluss++
            }
            hentData(sisteKordinat)

            visKlokke()
        }

        //minsker variablen vist på klokke-Viewet og oppdaterer dataen basert på den nye tiden
        fun minusKlokka() {
            if (antPluss > 0) {
                nyTid--
                if (nyTid == -1) {
                    nyTid = 23
                    nyDag--
                    if (nyDag == 0 && nyMaaned - 1 in mndMedTrettiDager) {
                        nyDag = 30
                        nyMaaned--
                    } else if (nyDag == 0 && nyMaaned - 1 in mndMedTrettienDager) {
                        nyDag = 31
                        nyMaaned--
                    } else if (nyDag == 0 && nyMaaned - 1 == 2) {
                        nyDag = 28
                        nyMaaned--
                    }
                }
                antPluss--
            }
            ferdig = false
            hentData(sisteKordinat)

            visKlokke()
        }
    //henter Database klassen
    fun passDB(data: DBhandler) {
        database = data
    }


    //lagrer en rute
    fun lagre(navn: String, beskrivelse: String, view:View) {






        val liste = database!!.readDistinct()
        if(listofRealCordinatas.size<2){
            Toast.makeText(view.context, "Du har ikke laget en rute", Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(view.findFragment())
                .navigate(R.id.nav_hjem, null)
            return

        }
        if(liste.size>20){
            Toast.makeText(view.context, "For mange lagrede ruter", Toast.LENGTH_SHORT).show()
            return
        }
        if(beskrivelse.length<250) {
            if (navn in liste) {
                Toast.makeText(view.context, "Navn allerede i bruk", Toast.LENGTH_SHORT).show()

            } else {
                for (kordinat in listofRealCordinatas) {
                    val punkt = Punkt()
                    punkt.lat = kordinat.latitude
                    punkt.long = kordinat.longitude
                    punkt.navn = navn
                    database!!.insertData(punkt)
                    database!!.insertBeskrivelse(navn, beskrivelse)
                    Toast.makeText(view.context, "Rute lagret", Toast.LENGTH_SHORT).show()
                    NavHostFragment.findNavController(view.findFragment())
                        .navigate(R.id.nav_lagret, null)
                }
                listofRealCordinatas.clear()
            }

        }
        else{
            Toast.makeText(view.context, "Beskrivelsen er for lang", Toast.LENGTH_SHORT).show()
        }
    }

    //setter punkter inn i listen listofSavedCoord fra databasen
    fun addpoints(liste: MutableList<Punkt>) {
        setNmr = 1
        for (u in liste) {

            listOFsavedCoord.add(LatLng(u.lat, u.long))

        }
    }



    fun getSavedPoints(): MutableList<LatLng> {


        return listOFsavedCoord
    }

    //returnerer setnmr som har kontroll på om vi tegner en lagret rute
    fun getSetnmr(): Int {
        return setNmr
    }

    //resetter alt som brukes for å tegne en allerede lagret rute
    fun resetlistOFsavedCoord() {
        listOFsavedCoord.clear()
        listofRealCordinatas.clear()
        setNmr = 0

    }


    //Funksjoner som får lokasjon til bruker

    //Inspirert av https://www.androdocs.com/kotlin/getting-current-location-latitude-longitude-in-android-using-kotlin.html
    //og https://developer.android.com/training/location/retrieve-current
    fun getLatLng(): LatLng{
        return LatLng(currentLocation.latitude,currentLocation.longitude)
    }

    //Initialiserer map og FusedLocationClient
    fun locationHenter(mMap: GoogleMap) {
        map = mMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        getLastLocation()
    }

    //Ber om permissions hvis det ikke er gitt
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )

    }

    //Sjekker permissions
    private fun checkPermissions(): Boolean {

        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }





    //Sjekker om lokasjon er på
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity!!.applicationContext!!.getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Henter siste lokasjon
    fun getLastLocation() = if (checkPermissions()) {
        if (isLocationEnabled()) {
            //Får warning men ikke sikker på hvordan vi skal fikse det.
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location : Location? ->

                if (location == null) {

                    requestNewLocationData()
                } else {

                    currentLocation = location
                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude,currentLocation.longitude), 11.0f))

                }

            }
        } else {
            Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
            //startActivity(intent)
        }
    } else {
        requestPermissions()
    }

    //Spørr om lokasjon hvis lokasjon var null
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity!!)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    //Når vi får ny lokasjon så kjører denne og lagrer lokasjonen
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            currentLocation = locationResult.lastLocation
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude,currentLocation.longitude), 11.0f))

        }
    }
}