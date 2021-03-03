package com.example.IN2000_prosjekt.Favourites


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.IN2000_prosjekt.R
import kotlinx.android.synthetic.main.favoritt_element.view.*


class FavorittAdapter(private val favoritter : MutableList<FavorittElement>, private val cl: OnFavorittItemClickListener):
    RecyclerView.Adapter<FavorittAdapter.FavorittViewHolder>(){

    //Inspirert av https://developer.android.com/guide/topics/ui/layout/recyclerview
    class FavorittViewHolder(itemview : View): RecyclerView.ViewHolder(itemview){
        private var tittel = itemView.navn

        private var info = itemView.beskrivelse
        private var knapp:ImageView? = null


        fun initialize (item: FavorittElement, action: OnFavorittItemClickListener) {
            tittel.text = item.tittel
            info.text = item.info
            knapp = itemView.findViewById(R.id.button)
            val rutetittel = item.tittel

            knapp!!.setOnClickListener {
                action.onDel(rutetittel)

            }
            itemView.setOnClickListener {

                action.onItemClick(rutetittel)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorittViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favoritt_element, parent, false)


        return FavorittViewHolder(view)
    }

    override fun getItemCount() = favoritter.size


    override fun onBindViewHolder(h: FavorittViewHolder, position: Int) {
        val fav = favoritter[position]
        h.itemView.navn.text = fav.tittel
        h.itemView.beskrivelse.text = fav.info

        h.initialize(fav, cl)

    }
}

//blir overskrevet når adapteren bli initsialisert
interface OnFavorittItemClickListener {
    fun onItemClick(tittel:String)
    fun onDel(tittel:String)
}
