package com.example.IN2000_prosjekt.Favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.IN2000_prosjekt.R

class FavouritesFragment: Fragment() {

    private val fav = FavorittViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val favouritesView = inflater.inflate(R.layout.favourites_fragment, container, false)
        fav.init(favouritesView,this)
        return favouritesView

    }
}