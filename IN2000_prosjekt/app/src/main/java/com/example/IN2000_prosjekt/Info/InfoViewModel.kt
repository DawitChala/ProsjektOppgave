package com.example.IN2000_prosjekt.Info

import android.app.Activity
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.ViewModel
import com.example.IN2000_prosjekt.R
import kotlinx.android.synthetic.main.info_fragment.*


class InfoViewModel : ViewModel() {

    companion object {
        var activity: Activity? = null
    }

    fun viewHenter(newActivity: Activity) {
        activity = newActivity
    }

    fun settScroll() {
        activity?.vettreglerTekst?.movementMethod = ScrollingMovementMethod()
    }

    //Oppdaterer teksten i textView dersom brukeren bytter tab
    fun endreTilVettregler() {
        activity?.hoyrelinje?.background = getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_morklinje
        )
        activity?.venstrelinje?.background = getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_lyslinje
        )
        activity?.vettTittel?.text = activity!!.applicationContext.getString(R.string.vettTittel)
        activity?.vettreglerTekst?.text = activity!!.applicationContext.getString(R.string.vettreglerTekst)
        activity?.forberedelser?.setTextAppearance(R.style.tittelMork)
        activity?.vettregler?.setTextAppearance(R.style.tittelLys)
        activity?.vettreglerTekst?.scrollTo(0, 0)
    }

    fun endreTilForberedelser() {
        activity?.hoyrelinje?.background = getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_lyslinje
        )
        activity?.venstrelinje?.background = getDrawable(
            activity!!.applicationContext,
            R.drawable.ic_morklinje
        )
        activity?.vettTittel?.text = activity!!.applicationContext.getString(R.string.forbeTittel)
        activity?.vettreglerTekst?.text = activity!!.applicationContext.getString(R.string.forbredelserTekst)
        activity?.forberedelser?.setTextAppearance(R.style.tittelLys)
        activity?.vettregler?.setTextAppearance(R.style.tittelMork)
        activity?.vettreglerTekst?.scrollTo(0, 0)
    }
}
