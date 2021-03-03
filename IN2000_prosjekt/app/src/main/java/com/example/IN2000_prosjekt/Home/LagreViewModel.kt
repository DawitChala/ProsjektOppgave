package com.example.IN2000_prosjekt.Home

import android.view.View
import android.widget.TextView
import com.example.IN2000_prosjekt.MainViewModel
import com.example.IN2000_prosjekt.R

class  LagreViewModel {

    private val mainview = MainViewModel()
    fun faaIgang(view: View) {
        /*setter onclick lagreknapp som vil lagre alt som står i tekboksene beskrivelse og tittel
        gjøres ved å kalle på metoden lagre i viewmodel.*/

        val lagre = view.findViewById<View>(R.id.lagreKnapp)
        val beskrivelse = view.findViewById<TextView>(R.id.skrivBeskrivelse)
        val tittel = view.findViewById<TextView>(R.id.skrivTittel)
        lagre.setOnClickListener {
            print("\n\n\n\nfewfwefwef\n\n\n\n")
            val str: String = beskrivelse.text.toString()
            val str1: String = tittel.text.toString()
            mainview.lagre(str1, str, view)

        }
    }

}
