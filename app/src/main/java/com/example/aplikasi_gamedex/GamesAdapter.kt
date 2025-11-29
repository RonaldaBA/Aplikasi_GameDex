package com.example.aplikasi_gamedex

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aplikasi_gamedex.databinding.ItemGameBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.models.SteamPriceOverview
import java.util.Locale
import kotlin.math.roundToInt

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

            // Normal price (strike-through) shown only if different
            binding.tvNormalPrice.text = if (deal.normalPrice != deal.salePrice && deal.normalPrice.isNotBlank()) {
                formatToIDR(deal.normalPrice)
            } else {
                ""
            }
            binding.tvNormalPrice.paintFlags = binding.tvNormalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // SAVINGS / DISCOUNT
            val savingsText = computeSavingsText(deal)
            if (savingsText != null) {
                // ensure a TextView tvSavings exists in item layout (ItemGameBinding.tvSavings)
                binding.tvSavings?.let {
                    it.text = savingsText
                    it.visibility = android.view.View.VISIBLE
                }
            } else {
                binding.tvSavings?.let {
                    it.visibility = android.view.View.GONE
                }
            }

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

    private fun computeSavingsText(deal: CheapSharkDeal): String? {
        // 1) try savings field (often a string like "20.00")
        deal.savings?.takeIf { it.isNotBlank() }?.let { s ->
            val pct = s.toDoubleOrNull()
            if (pct != null) {
                // round to int if nearly integer, else show one decimal
                return formatPercent(pct)
            }
        }

        // 2) try calculate from normalPrice and salePrice
        val normal = deal.normalPrice.toDoubleOrNull() ?: parsePriceIgnoringNonDigits(deal.normalPrice)
        val sale = deal.salePrice.toDoubleOrNull() ?: parsePriceIgnoringNonDigits(deal.salePrice)

        if (normal != null && sale != null && normal > 0.0 && normal > sale) {
            val savingsPct = (1.0 - (sale / normal)) * 100.0
            return formatPercent(savingsPct)
        }

        // 3) no info
        return null
    }

    private fun formatPercent(value: Double): String {
        val rounded = (value * 10.0).roundToInt() / 10.0
        // if integer like 20.0 => show "20% OFF", else "20.5% OFF"
        return if (rounded % 1.0 == 0.0) {
            "-${rounded.toInt()}%"
        } else {
            String.format(Locale.getDefault(), "-%.1f%%", rounded)
        }
    }

    /**
     * Helper to try parse strings like "12.99" or "$12.99" or "USD 12.99" or "12,99"
     */
    private fun parsePriceIgnoringNonDigits(raw: String): Double? {
        if (raw.isBlank()) return null
        // remove currency symbols and spaces, replace comma with dot
        val cleaned = raw.replace(Regex("[^0-9,\\.]"), "").replace(',', '.')
        return cleaned.toDoubleOrNull()
    }
}