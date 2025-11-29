package com.example.aplikasi_gamedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikasi_gamedex.models.CheapSharkDeal

class FavoritesAdapter(
    private var items: List<CheapSharkDeal>,
    private val onClick: (CheapSharkDeal) -> Unit,
    private val onRemoveClick: (CheapSharkDeal) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumb: ImageView = itemView.findViewById(R.id.imgThumb)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvStore: TextView = itemView.findViewById(R.id.tvStore)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_game, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title ?: "-"

        holder.tvStore.text = when (item.storeID) {
            "1" -> "Steam"
            "7" -> "GOG"
            "25" -> "Epic Games Store"
            else -> "Unknown"
        }

        holder.tvPrice.text = "Rp ${item.salePrice ?: item.normalPrice ?: "-"}"

        if (!item.thumb.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(item.thumb)
                .into(holder.imgThumb)
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.btnRemove.setOnClickListener { onRemoveClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun setData(newList: List<CheapSharkDeal>) {
        items = newList
        notifyDataSetChanged()
    }
}
