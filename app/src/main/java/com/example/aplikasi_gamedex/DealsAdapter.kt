package com.example.aplikasi_gamedex

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aplikasi_gamedex.databinding.ItemDealBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal

class DealsAdapter : RecyclerView.Adapter<DealsAdapter.VH>() {

    private val items = mutableListOf<CheapSharkDeal>()

    inner class VH(val v: View) : RecyclerView.ViewHolder(v) {
        val storeOrigin: TextView = v.findViewById(R.id.storeOrigin)
        val salePrice: TextView = v.findViewById(R.id.salePrice)
        val normalPrice: TextView = v.findViewById(R.id.normalPrice)
        val savings: TextView = v.findViewById(R.id.savings)
        val metacritic: TextView = v.findViewById(R.id.metacritic)
        val releaseDate: TextView = v.findViewById(R.id.releaseDate)
        val dealRating: TextView = v.findViewById(R.id.dealRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_deal, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val deal = items[position]
        holder.storeOrigin.text = getStoreName(deal.storeID)
        holder.salePrice.text = "Sale Price: ${deal.salePrice}"
        holder.normalPrice.text = "Normal Price: ${deal.normalPrice}"
        holder.savings.text = "Savings: ${deal.savings ?: "0"}%"
        holder.metacritic.text = "Metacritic: ${deal.metacriticLink ?: "N/A"}"
        holder.releaseDate.text = "Release: ${deal.releaseDate ?: "N/A"}"
        holder.dealRating.text = "Deal Rating: ${deal.dealRating ?: "N/A"}"
        // Jika ingin tampilkan field tambahan, bisa ditambahkan ke TextView lain di item_deal.xml
    }

    override fun getItemCount() = items.size

    fun submitList(new: List<CheapSharkDeal>) {
        items.clear()
        items.addAll(new)
        notifyDataSetChanged()
    }

    private fun getStoreName(storeIdRaw: String?): String {
        // storeIdRaw kadang-nya sudah String "1" atau null
        val id = storeIdRaw?.trim()?.toIntOrNull()
        return when (id) {
            1 -> "Steam"
            7 -> "GOG"
            25 -> "Epic Games"
            else -> storeIdRaw?.let { "Store #$it" } ?: "Unknown Store"
        }
    }
}

