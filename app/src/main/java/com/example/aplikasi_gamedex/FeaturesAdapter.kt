package com.example.aplikasi_gamedex

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeaturesAdapter :
    RecyclerView.Adapter<FeaturesAdapter.VH>() {

    private val items = mutableListOf<String>()

    inner class VH(val v: TextView) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = TextView(parent.context).apply {
            setPadding(16, 8, 16, 8)
            setTextColor(Color.WHITE)
            textSize = 14f
        }
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.v.text = "• " + items[position]
    }

    override fun getItemCount() = items.size

    fun submitList(new: List<String>) {
        items.clear()
        items.addAll(new)
        notifyDataSetChanged()
    }
}
