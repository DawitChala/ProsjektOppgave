package com.example.IN2000_prosjekt.Home

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.IN2000_prosjekt.Database.DBhandler
import com.example.IN2000_prosjekt.MainViewModel
import com.example.IN2000_prosjekt.R
import com.example.IN2000_prosjekt.Settings.SettingsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.hjem_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HjemFragment: Fragment(), OnMapReadyCallback {

    //For FAB menu:
    private var isMenuOpen = false

    //Kart:
    private lateinit var mMap: GoogleMap
    private var forst2 = true
    private val viewModel: MainViewModel by viewModels()
    private val setMod = SettingsViewModel()
    private var bool = true
    private var zoomf: Float = 5.0f
    private var startpunkt = LatLng(0.0, 0.0)



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        viewModel.passDB(DBhandler(requireContext()))
        //viewModel.passDB(DBhandler(context!!))
        setMod.endreTekst()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val hjemView = inflater.inflate(R.layout.hjem_fragment, container, false)

        //FAB-Menu:
        val fab = hjemView!!.findViewById(R.id.fabMenu) as FloatingActionButton
        val openFab = AnimationUtils.loadAnimation(activity, R.anim.open_fab)
        val hideFab = AnimationUtils.loadAnimation(activity, R.anim.hide_fab)
        val rotateClockwise = AnimationUtils.loadAnimation(activity, R.anim.rotate_clockwise)
        val rotateBack = AnimationUtils.loadAnimation(activity, R.anim.rotate_back)
        val pluss = hjemView.findViewById(R.id.plussKnapp) as Button
        val minus = hjemView.findViewById(R.id.minusKnapp) as Button
        val trekant = hjemView.findViewById(R.id.Trekant) as ImageView
        val ferdig = hjemView.findViewById<Button>(R.id.ferdig)



        ferdig.setOnClickListener {
            ferdig.isVisible = false
            viewModel.linjerFerdig(mMap)
        }

        trekant.setOnClickListener {
            findNavController().navigate(R.id.nav_varslinger, null)
        }

        pluss.setOnClickListener {
            viewModel.plussKlokka()
        }

        minus.setOnClickListener {
            viewModel.minusKlokka()
        }



        fab.setOnClickListener {
            if (isMenuOpen) {
                fabEdit.startAnimation(hideFab)
                fabUndo.startAnimation(hideFab)
                fabSave.startAnimation(hideFab)
                fabTime.startAnimation(hideFab)
                fabMenu.startAnimation(rotateClockwise)
                viewModel.skjulKlokke()
                isMenuOpen = false
            } else {
                fabEdit.startAnimation(openFab)
                fabUndo.startAnimation(openFab)
                fabSave.startAnimation(openFab)
                fabTime.startAnimation(openFab)
                fabMenu.startAnimation(rotateBack)
                //viewModel.gjorUsynlig()

                fabEdit.isClickable
                fabUndo.isClickable
                fabSave.isClickable
                fabTime.isClickable

                isMenuOpen = true
            }

            fabEdit.setOnClickListener {
                viewModel.tegne()
            }

            fabUndo.setOnClickListener {
                viewModel.fjernForrige()
            }

            fabSave.setOnClickListener {
                findNavController().navigate(R.id.nav_lagre, null)
            }

            fabTime.setOnClickListener {

                viewModel.oppdaterKlokke()

            }
        }

        return hjemView
    }


    override fun onMapReady(map: GoogleMap?) {
        lateinit var job: Job
        viewModel.endreData()
        map?.let {
            if(viewModel.getSetnmr() == 0) {
                viewModel.tegnTidligere(it)
                for(mark in viewModel.getListOfMarkers()){
                    if(!mark.isVisible){
                        ferdig?.isVisible = true
                    }
                }
            }
            it.setOnMarkerClickListener { it1 ->
                viewModel.gjorUsynlig()
                bool = false
                viewModel.hentData(it1.position)
                viewModel.oceanData.observe(this, Observer {
                    job = CoroutineScope(Main).launch {
                        viewModel.settOppKlokke(it)
                    }

                    CoroutineScope(Main).launch {
                        job.join()
                        viewModel.settOceanVariabler(it)
                    }

                })

                viewModel.locationData.observe(this, Observer {
                    CoroutineScope(Main).launch {
                        try {
                            job.join()
                        }catch (e : Exception){
                            print(e)
                        }
                        viewModel.settLocVariabler(it)
                    }
                })
                map.animateCamera(CameraUpdateFactory.newLatLng(it1.position))
                viewModel.visVaer(true)
                pin.isVisible = false
                false
            }
            mMap = it
            viewModel.locationHenter(map)
            startpunkt = LatLng(61.704694, 8.929784)  // Vågå kommune
            //startpunkt = LatLng(59.9088888, 10.7274329)  // Oslo
            hentStart()
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(startpunkt))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startpunkt, 5.0f))
            setMapStyle(mMap)

            if (setMod.getDarkMode()) {
                setMapStyleDark(mMap)

            } else {
                setMapStyle(mMap)

            }



            viewModel.fiksFeil()
            viewModel.gjorUsynlig()
            viewModel.skjulKlokke()




            //under så vil lagrede ruter bli tegnet opp, de vil bli tegnet opp som om de ble klikket opp av en bruker
            //Vi må ha med en delay  fordi dataen ikke vil være oppdatert uten, slik at vi ikke ville fått tegnet hvis vi ikke hadde
            //hatt den med
            if (viewModel.getSetnmr() == 1) {
                viewModel.clearMap(mMap)
                val listemdePunkter = viewModel.getSavedPoints()
                CoroutineScope(Main).launch {

                    for (punkter in listemdePunkter) {
                        delay(1000)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(punkter, 11.0f))
                        viewModel.fiksFeil()
                        viewModel.gjorUsynlig()
                        viewModel.skjulKlokke()
                        if (viewModel.forst) {
                            viewModel.endreData()
                            //viewModel.forst = false
                        }
                        //viewModel.clearMap(mMap)
                        viewModel.klikket(punkter, map)

                        if (viewModel.forst) {

                            obs()

                            viewModel.forst = false
                        }
                    }
                    CoroutineScope(Main).launch {
                        delay(500)
                        viewModel.linjerFerdig(mMap)
                        viewModel.resetlistOFsavedCoord()
                    }
                }

            }



            map.setOnMapClickListener { latLng ->
                viewModel.fiksFeil()
                viewModel.gjorUsynlig()
                viewModel.skjulKlokke()

                if (!viewModel.tegne) {
                    bool = true
                    viewModel.hentData(latLng)
                    forst2 = true

                    viewModel.oceanData.observe(this, Observer {

                        job = CoroutineScope(Main).launch {
                            viewModel.settOppKlokke(it)
                        }


                        CoroutineScope(Main).launch {
                            job.join()
                            viewModel.settOceanVariabler(it)
                        }
                    })

                    viewModel.locationData.observe(this, Observer {
                        CoroutineScope(Main).launch {
                            try {
                                job.join()
                            }catch (e : Exception){
                                print(e)
                            }
                            viewModel.settLocVariabler(it)
                        }
                    })

                    if(map.cameraPosition.zoom > 11.0f){
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    }else{
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f))
                    }
                    viewModel.visVaer(false)


                } else {
                    if(viewModel.forsteTrykk){
                        print("\n\n\nforst\n\n\n")
                    }
                    ferdig!!.isVisible = true

                    if (viewModel.forst) {
                        ferdig!!.isVisible = true
                        viewModel.endreData()
                        //viewModel.forst = false
                    }
                    //viewModel.clearMap(mMap)

                    viewModel.klikket(latLng, map)


                    if (viewModel.forst) {
                        obs()
                    }



                    if(map.cameraPosition.zoom > 11.0f){
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    }else{
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f))
                    }
                }
                map.setOnCameraMoveStartedListener {
                    viewModel.gjorUsynlig()
                    viewModel.skjulKlokke()
                }
            }
        }
    }

    private fun obs() {
        viewModel.oceanTegneData.observe(this, Observer {
            viewModel.feil = false
            viewModel.settOppKlokke(it)
            viewModel.settPunkterHav(it)
        })

        viewModel.locationTegneData.observe(this, Observer {
            viewModel.settPunkterVind(it, mMap)
        })

    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setMapStyleDark(map: GoogleMap) {
        try {
            val success =
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        activity,
                        R.raw.map_style_dark
                    )
                )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }


    private fun hentStart() {
        try {
            startpunkt = viewModel.getLatLng()  // Nåværende posisjon
            zoomf = 11.0f
        } catch (e: Exception) {
            print(e)
        }

    }
}
