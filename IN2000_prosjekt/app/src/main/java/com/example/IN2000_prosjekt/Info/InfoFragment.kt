package com.example.IN2000_prosjekt.Info

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.IN2000_prosjekt.R
import com.example.IN2000_prosjekt.R.layout
import com.example.IN2000_prosjekt.Settings.SettingsViewModel


class InfoFragment: Fragment() {

    private val viewModel: InfoViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

          //viewModel.settScroll()

          val infoView = inflater.inflate(layout.info_fragment, container, false)
//        val forbredelser = infoView!!.findViewById(R.id.forbredelserKnapp) as Button
          viewModel.settScroll()
          val forberedelser = infoView!!.findViewById(R.id.forberedelser) as TextView
          val vettregler = infoView.findViewById(R.id.vettregler) as TextView
            val vettreglerTekst = infoView.findViewById(R.id.vettreglerTekst) as TextView
        val setMod = SettingsViewModel()
        setMod.passInfo(infoView)
        setMod.endreTekst()
        vettreglerTekst.movementMethod= ScrollingMovementMethod()


        //Oppdaterer tekst dersom bruker klikker paa tabsene
        forberedelser.setOnClickListener {
            viewModel.endreTilForberedelser()
            setMod.endreTekst()
        }



        vettregler.setOnClickListener {
            viewModel.endreTilVettregler()
            setMod.endreTekst()
        }

        return infoView
    }
}

