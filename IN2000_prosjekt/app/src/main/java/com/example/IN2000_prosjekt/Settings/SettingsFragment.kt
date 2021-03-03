package com.example.IN2000_prosjekt.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.IN2000_prosjekt.R

class SettingsFragment: Fragment() {
    private val settingsViewModel : SettingsViewModel = SettingsViewModel()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingsViewModel.endreTekst()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel.endreTekst()
    }

    override fun onDestroy() {
        settingsViewModel.endreTekst()
        super.onDestroy()
        settingsViewModel.endreTekst()
    }

    override fun onDestroyView() {
        settingsViewModel.endreTekst()
        super.onDestroyView()
        settingsViewModel.endreTekst()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            :View?{
        super.onCreate(savedInstanceState)


        //View variabler
        val root =  inflater.inflate(R.layout.settings_fragment, container, false)
        val plussknapp = root.findViewById<View>(R.id.plussKnapp)
        val minusknapp = root.findViewById<View>(R.id.minusKnapp)
        val switch = root.findViewById<Switch>(R.id.darkSwitch)

        //påser at switchen er flyttet til "på" dersom darkmode allerede er på
        if(settingsViewModel.getDarkMode()){
            switch.isChecked = true
        }

        //kaller på darkmode metoden når switchen klikkes
        switch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.darkmode(isChecked)
        }

        //kaller på metoden som øker font størrelsen
        plussknapp.setOnClickListener {

            settingsViewModel.increaseFontSize()
        }
        //kaller på metoden som minsker font størrelsen
        minusknapp.setOnClickListener {

            settingsViewModel.decreaseFontSize()
        }

        settingsViewModel.endreTekst()



        return root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        settingsViewModel.endreTekst()
    }


}