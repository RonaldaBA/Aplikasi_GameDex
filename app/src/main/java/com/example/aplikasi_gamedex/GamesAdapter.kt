package com.example.aplikasi_gamedex

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aplikasi_gamedex.databinding.ItemGameBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.models.SteamPriceOverview

class GamesAdapter(
    private val items: MutableList<CheapSharkDeal> = mutableListOf(),
    private var steamPrices: Map<String, SteamPriceOverview?> = emptyMap(),
    private val onClick: (CheapSharkDeal) -> Unit = {}
) : RecyclerView.Adapter<GamesAdapter.VH>() {

    inner class VH(private val binding: ItemGameBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deal: CheapSharkDeal) {
            binding.tvTitle.text = deal.title
            binding.tvStore.text = storeNameFromId(deal.storeID.toIntOrNull() ?: -1)

            // Prefer Steam local price if store is Steam and steamPrices contains entry
            val localPriceText = if (deal.storeID == "1" && !deal.gameID.isNullOrBlank()) {
                val sp = steamPrices[deal.gameID]
                sp?.let {
                    formatSteamPrice(it, requireContext = binding.root.context)
                }
            } else null

            // fallback: convert SalePrice (USD string) to IDR
            val priceText = localPriceText ?: formatToIDR(deal.salePrice)
            binding.tvPrice.text = priceText

            binding.tvNormalPrice.text = if (deal.normalPrice != deal.salePrice) formatToIDR(deal.normalPrice) else ""

            binding.imgThumb.load(deal.thumb) { crossfade(true) }

            binding.root.setOnClickListener { onClick(deal) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<CheapSharkDeal>, steamPriceMap: Map<String, SteamPriceOverview?> = emptyMap()) {
        items.clear()
        items.addAll(newItems)
        steamPrices = steamPriceMap
        notifyDataSetChanged()
    }

    // existing helper
    private fun storeNameFromId(id: Int): String {
        return when (id) {
            1 -> "Steam"
            7 -> "GOG"
            25 -> "Epic Games Store"
            else -> "Other"
        }
    }
}