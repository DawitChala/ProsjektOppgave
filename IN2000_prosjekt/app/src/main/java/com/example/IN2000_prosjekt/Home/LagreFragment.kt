package com.example.IN2000_prosjekt.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.IN2000_prosjekt.R

class LagreFragment: Fragment() {
    private val lagreViewModel = LagreViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val lagreView = inflater.inflate(R.layout.lagre_fragment, container, false)
        print("\n\n\n\nfewfwefwef\n\n\n\n")
        lagreViewModel.faaIgang(lagreView)

        return lagreView
    }


    companion object
}