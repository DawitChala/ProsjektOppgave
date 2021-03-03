package com.example.IN2000_prosjekt.Info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.IN2000_prosjekt.R
import com.example.IN2000_prosjekt.Settings.SettingsViewModel

class VarslingerFragment : Fragment() {
    private val settingsViewModel : SettingsViewModel = SettingsViewModel()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settingsViewModel.endreTekst()
    }

    override fun onStop() {
        super.onStop()
        settingsViewModel.endreTekst()
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.endreTekst()
    }

    override fun onStart() {
        super.onStart()
        settingsViewModel.endreTekst()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val infoView = inflater.inflate(R.layout.varslinger_fragment, container, false)
        settingsViewModel.endreTekst()

        return infoView
    }

}