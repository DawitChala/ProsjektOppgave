package com.example.IN2000_prosjekt.Settings



import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.IN2000_prosjekt.R
import kotlinx.android.synthetic.main.settings_fragment.*


class SettingsViewModel: ViewModel() {

    //statiske variabler
    companion object {

        var fontSize =0
        @SuppressLint("StaticFieldLeak")
        private var hovedAktiviteter : Activity? = null
        @SuppressLint("StaticFieldLeak")
        var infoV: View? = null
        var sharedPref12: SharedPreferences? = null
        var SPdark: SharedPreferences? = null
        var start = true


    }



    //minsker font størrelsen og lagrer den i en sharedPreferance
    fun decreaseFontSize(){
        if(sharedPref12?.getInt("plass", 0) != 0){
            fontSize = sharedPref12?.getInt("plass", 0)!!
        }
        if(fontSize > 5) {
            fontSize--
        }
        with(sharedPref12?.edit()) {
            this?.putInt("plass", fontSize)
            this?.commit()
        }
        endreTekst()
    }

    //øker font størrelsen og lagrer den i en sharedPreferance
    fun increaseFontSize(){
        if(sharedPref12?.getInt("plass", 0) != 0){
            fontSize = sharedPref12?.getInt("plass", 0)!!
        }
        if (fontSize < 10) {
            fontSize++
        }
        with(sharedPref12?.edit()) {
            this?.putInt("plass", fontSize)
            this?.commit()
        }
        endreTekst()
    }

    // endrer Boolean verdien darkmode når "switchen" blir flippet
    fun darkmode(check: Boolean){
        with(SPdark?.edit()){
            this?.putBoolean("dark", check)
            this?.commit()
        }
    }

    /*
    henter Boolean verdien for å sjekke om hjem fragmentet skal sette mapStylen i dark mode eller ikke
     */
    fun getDarkMode():Boolean{ return SPdark!!.getBoolean("dark", false) }

    fun passMain(aktiviter:Activity){
        hovedAktiviteter = aktiviter
        sharedPref12 = hovedAktiviteter?.getPreferences(Context.MODE_PRIVATE)
        SPdark = hovedAktiviteter?.getPreferences(Context.MODE_PRIVATE)
    }

    fun passInfo(info: View){
        infoV = info
    }

    /*
    henter tekststørrelser og endrer eksisterende tekster til tilsvarende størrelser
     */
    fun endreTekst() {

//        with(sharedPref12?.edit()) {
//            this?.putInt("plass", fontSize)
//            this?.commit()
//    }

        print("\n\n\n\nendret\n\n\n")
        start = false

        val derSkriftVar5 = sharedPref12!!.getInt("plass", 0)+5
        val finalFontSizeAsString5 = "$derSkriftVar5.00f"
        val finalFontSize5 : Float = finalFontSizeAsString5.toFloat()

        val derSkriftVar12 = sharedPref12!!.getInt("plass", 0)+12
        val finalFontSizeAsString12 = "$derSkriftVar12.00f"
        val finalFontSize12 : Float = finalFontSizeAsString12.toFloat()

        val derSkriftVar14 = sharedPref12!!.getInt("plass", 0)+14
        val finalFontSizeAsString14 = "$derSkriftVar14.00f"
        val finalFontSize14 : Float = finalFontSizeAsString14.toFloat()

        val derSkriftVar18 = sharedPref12!!.getInt("plass", 0)+18
        val finalFontSizeAsString18 = "$derSkriftVar18.00f"
        val finalFontSize18 : Float = finalFontSizeAsString18.toFloat()

        val derSkriftVar20 = sharedPref12!!.getInt("plass", 0)+20
        val finalFontSizeAsString20 = "$derSkriftVar20.00f"
        val finalFontSize20 : Float = finalFontSizeAsString20.toFloat()


        hovedAktiviteter?.findViewById<TextView>(R.id.dato)?.textSize = finalFontSize12
        hovedAktiviteter?.findViewById<TextView>(R.id.dato2)?.textSize = finalFontSize5
        hovedAktiviteter?.findViewById<TextView>(R.id.tempviser)?.textSize = finalFontSize5
        hovedAktiviteter?.findViewById<TextView>(R.id.bolgeviser)?.textSize = finalFontSize5
        hovedAktiviteter?.findViewById<TextView>(R.id.stromningviser)?.textSize = finalFontSize5
        hovedAktiviteter?.findViewById<TextView>(R.id.bolgeHoydeViser)?.textSize = finalFontSize5
        hovedAktiviteter?.findViewById<TextView>(R.id.navn)?.textSize = finalFontSize20
        hovedAktiviteter?.findViewById<TextView>(R.id.beskrivelse)?.textSize = finalFontSize14
        hovedAktiviteter?.findViewById<TextView>(R.id.rodTekst)?.textSize = finalFontSize14
        hovedAktiviteter?.findViewById<TextView>(R.id.gronnTekst)?.textSize = finalFontSize14
        hovedAktiviteter?.findViewById<TextView>(R.id.oransjeTekst)?.textSize = finalFontSize14



        //ting i settings som endrer størrelse
        hovedAktiviteter?.type_size?.setTextSize(TypedValue.COMPLEX_UNIT_SP,finalFontSize14)
        hovedAktiviteter?.findViewById<TextView>(R.id.darkTekst)?.textSize = finalFontSize14
        if(infoV != null) {
            infoV!!.findViewById<TextView>(R.id.forberedelser).textSize = finalFontSize14
            infoV!!.findViewById<TextView>(R.id.vettregler).textSize = finalFontSize14
            infoV!!.findViewById<TextView>(R.id.vettTittel).textSize = finalFontSize18
            infoV!!.findViewById<TextView>(R.id.vettreglerTekst).textSize = finalFontSize14



        }
        //



    }

}
