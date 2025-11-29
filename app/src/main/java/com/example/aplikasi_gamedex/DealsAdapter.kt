package com.example.aplikasi_gamedex

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aplikasi_gamedex.databinding.ItemDealBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.models.SteamPriceOverview

class DealsAdapter : RecyclerView.Adapter<DealsAdapter.VH>() {

    private val items = mutableListOf<CheapSharkDeal>()
    var steamPrices = mutableMapOf<String, SteamPriceOverview>()

    inner class VH(val v: View) : RecyclerView.ViewHolder(v) {
        val storeOrigin: TextView = v.findViewById(R.id.storeOrigin)
        val salePrice: TextView = v.findViewById(R.id.salePrice)
        val normalPrice: TextView = v.findViewById(R.id.normalPrice)
        val savings: TextView = v.findViewById(R.id.savings)
        val metacritic: TextView = v.findViewById(R.id.metacritic)
        val releaseDate: TextView = v.findViewById(R.id.releaseDate)
        val dealRating: TextView = v.findViewById(R.id.dealRating)
        val btnOpenStore: Button = itemView.findViewById(R.id.btnOpenStore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_deal, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val deal = items[position]

        // Store name
        val storeId = deal.storeID?.toIntOrNull() ?: -1
        holder.storeOrigin.text = getStoreName(deal.storeID)

        // Sale price: Steam localized price first, fallback ke IDR
        val salePriceText = if (deal.storeID == "1" && !deal.gameID.isNullOrBlank()) {
            val sp = steamPrices[deal.gameID]
            sp?.let { formatSteamPrice(it, holder.itemView.context) } ?: formatToIDR(deal.salePrice)
        } else {
            formatToIDR(deal.salePrice)
        }
        holder.salePrice.text = "Sale Price: $salePriceText"

        // Normal price: format ke IDR dan strike-through jika berbeda dari sale
        val normalPriceText = if (!deal.normalPrice.isNullOrBlank() && deal.normalPrice != deal.salePrice) {
            holder.normalPrice.paintFlags = holder.normalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            formatToIDR(deal.normalPrice)
        } else {
            holder.normalPrice.paintFlags = holder.normalPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            ""
        }
        holder.normalPrice.text = if (normalPriceText.isNotEmpty()) "Normal Price: $normalPriceText" else ""

        // Savings
        holder.savings.text = "Savings: ${deal.savings ?: "0"}%"

        // Metacritic / Release / Deal rating
        holder.metacritic.text = "Metacritic: ${deal.metacriticLink ?: "N/A"}"
        holder.releaseDate.text = "Release: ${deal.releaseDate ?: "N/A"}"
        holder.dealRating.text = "Deal Rating: ${deal.dealRating ?: "N/A"}"

        // Tombol Go to Store
        holder.btnOpenStore.setOnClickListener {
            val url = getStoreUrl(deal.storeID, deal.dealID, deal.gameID)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(url)
            holder.itemView.context.startActivity(intent)
        }
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

    private fun getStoreUrl(storeIdRaw: String?, dealId: String?, gameId: String?): String {
        return when (storeIdRaw?.trim()?.toIntOrNull()) {
            1 -> "https://www.cheapshark.com/redirect?dealID=$dealId"     // Steam
            7 -> "https://www.cheapshark.com/redirect?dealID=$dealId"              // GOG
            25 -> "https://www.cheapshark.com/redirect?dealID=$dealId" // Epic
            else -> "https://www.cheapshark.com/redirect?dealID=$dealId"      // fallback
        }
    }

}

