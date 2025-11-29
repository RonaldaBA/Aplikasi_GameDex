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

    private var fullList: List<CheapSharkDeal> = emptyList()

    private val api by lazy { CheapSharkAPI.create() }

    // hanya store Steam(1), GOG(7), Epic(25)
    private val storeIds = listOf(1, 7, 25)

    private val steamApi by lazy { SteamStoreApi.create() }

    // cache: map appid -> SteamPriceOverview
    private val steamPriceCache = mutableMapOf<String, SteamPriceOverview?>()

    // KEMBALIKAN KE TOAST: Mengklik item hanya akan menampilkan Toast
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
        var steamSelected = false
        var gogSelected = false
        var epicSelected = false

        binding.btnSteam.setOnClickListener {
            steamSelected = !steamSelected
            applyStoreFilterButtons(steamSelected, gogSelected, epicSelected)
        }

        binding.btnGOG.setOnClickListener {
            gogSelected = !gogSelected
            applyStoreFilterButtons(steamSelected, gogSelected, epicSelected)
        }

        binding.btnEpic.setOnClickListener {
            epicSelected = !epicSelected
            applyStoreFilterButtons(steamSelected, gogSelected, epicSelected)
        }


    }

    private fun loadDeals() {
        binding.progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) fetch deals per store
                val deferreds = storeIds.map { id ->
                    async { api.getDeals(storeID = id, pageSize = 50) }
                }
                val lists = deferreds.map { it.await() }
                val merged: List<CheapSharkDeal> = lists
                    .flatten()
                    .distinctBy { it.dealID }
                    .sortedByDescending { (it.savings ?: "0").toDoubleOrNull() ?: 0.0 }

                // 2) steam appids
                val steamAppIds = merged
                    .filter { it.storeID == "1" }
                    .mapNotNull { it.gameID?.takeIf { id -> id.isNotBlank() } }
                    .distinct()

                // 3) fetch steam prices only for uncached items
                val idsToFetch = steamAppIds.filter { !steamPriceCache.containsKey(it) }
                if (idsToFetch.isNotEmpty()) {
                    val fetchDeferred = idsToFetch.map { appid ->
                        async {
                            try {
                                val resp = steamApi.getAppDetails(appid, country = "ID", lang = "en")
                                resp[appid]?.data?.price_overview
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                    val results = fetchDeferred.map { it.await() }
                    idsToFetch.forEachIndexed { idx, id -> steamPriceCache[id] = results[idx] }
                }

                // 4) simpan & tampilkan
                fullList = merged   // simpan untuk filtering

                adapter.setData(merged, steamPriceCache) // tampilkan default list
                 // jika chip sudah dicentang, langsung filter

            } catch (_: Exception) {
                // error handling optional
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun applyStoreFilterButtons(steam: Boolean, gog: Boolean, epic: Boolean) {
        if (fullList.isEmpty()) return

        val selected = mutableListOf<String>()
        if (steam) selected.add("1")
        if (gog) selected.add("7")
        if (epic) selected.add("25")

        val filtered =
            if (selected.isEmpty()) fullList
            else fullList.filter { it.storeID in selected }

        adapter.setData(filtered, steamPriceCache)
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
