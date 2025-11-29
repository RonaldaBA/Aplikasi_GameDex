package com.example.aplikasi_gamedex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_gamedex.databinding.FragmentSalesBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.network.CheapSharkAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.aplikasi_gamedex.network.SteamStoreApi
import com.example.aplikasi_gamedex.models.SteamPriceOverview

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private val api by lazy { CheapSharkAPI.create() }

    // hanya store Steam(1), GOG(7), Epic(25)
    private val storeIds = listOf(1, 7, 25)

    private val steamApi by lazy { SteamStoreApi.create() }

    // cache: map appid -> SteamPriceOverview
    private val steamPriceCache = mutableMapOf<String, SteamPriceOverview?>()

    // adapter now accepts an optional steamPriceMap
    private val adapter = GamesAdapter(onClick = { deal ->
        Toast.makeText(requireContext(), "Klik: ${deal.title}", Toast.LENGTH_SHORT).show()
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        loadDeals()
    }

    private fun loadDeals() {
        binding.progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) fetch deals from CheapShark per store (as before)
                val deferreds = storeIds.map { id ->
                    async { api.getDeals(storeID = id, pageSize = 50) }
                }
                val lists = deferreds.map { it.await() }
                val merged: List<CheapSharkDeal> = lists
                    .flatten()
                    .distinctBy { it.dealID }
                    .sortedByDescending { (it.savings ?: "0").toDoubleOrNull() ?: 0.0 }

                // 2) collect unique steam appids from merged (storeID == "1")
                val steamAppIds = merged
                    .filter { it.storeID == "1" }            // Steam deals only
                    .mapNotNull { it.gameID?.takeIf { id -> id.isNotBlank() } }
                    .distinct()

                // 3) fetch steam prices in parallel, but only for IDs not in cache
                val idsToFetch = steamAppIds.filter { !steamPriceCache.containsKey(it) }
                if (idsToFetch.isNotEmpty()) {
                    val fetchDeferred = idsToFetch.map { appid ->
                        async {
                            try {
                                val resp = steamApi.getAppDetails(appid, country = "ID", lang = "en")
                                val appResp = resp[appid]
                                appResp?.data?.price_overview
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                    val results = fetchDeferred.map { it.await() }
                    // populate cache
                    idsToFetch.forEachIndexed { idx, id -> steamPriceCache[id] = results[idx] }
                }

                // 4) pass merged deals and cache map to adapter
                adapter.setData(merged, steamPriceCache)
            } catch (_: Exception) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Gagal ambil data: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.recycler.scrollToPosition(0)
    }

    override fun onResume() {
        super.onResume()
        binding.recycler.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
