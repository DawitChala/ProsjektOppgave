package com.example.IN2000_prosjekt.Favourites

import android.view.View
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.IN2000_prosjekt.Database.DBhandler
import com.example.IN2000_prosjekt.MainViewModel
import com.example.IN2000_prosjekt.R


class FavorittViewModel {
    //init setter igang alle knappene som er i favorittadaptern og setter opp recyclerviewet.
    //overskriver også klikk funskjonene som er definert inne i apadteren.
    fun init(view: View, fragment: FavouritesFragment){
        val db  = DBhandler(view.context!!)
        val mainview = MainViewModel()
        val liste = db.readDistinct()
        val liste2: MutableList<FavorittElement> = ArrayList()
        for(navn in liste){
            liste2.add(FavorittElement(navn,db.readDesc(navn)))
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = FavorittAdapter(liste2, object : OnFavorittItemClickListener {
            override fun onItemClick(tittel:String) {
                mainview.addpoints(DBhandler(view.context!!).readData(tittel))
                findNavController(fragment).navigate(R.id.nav_hjem, null)
            }
            override fun onDel(tittel: String) {
                db.reomoveName(tittel)
                init(view,fragment)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(view.context!!, LinearLayoutManager.VERTICAL ,false)
    }
}