package com.example.aplikasi_gamedex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_gamedex.databinding.FragmentSalesBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.models.SteamPriceOverview
import com.example.aplikasi_gamedex.network.CheapSharkAPI
import com.example.aplikasi_gamedex.network.SteamStoreApi
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdListener
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private var fullList: List<CheapSharkDeal> = emptyList()

    private val api by lazy { CheapSharkAPI.create() }
    private val steamApi by lazy { SteamStoreApi.create() }

    // Store IDs
    private val storeIds = listOf(1, 7, 25)

    // Cache Steam price
    private val steamPriceCache = mutableMapOf<String, SteamPriceOverview?>()

    // Adapter
    private val adapter = GamesAdapter(onClick = { deal ->
        val bundle = Bundle().apply {
            putParcelable("deal", deal)
        }
        findNavController().navigate(R.id.DetailsGamesFragment, bundle)
    })


    // FILTER STATE (Class-level)
    private var steamSelected = false
    private var gogSelected = false
    private var epicSelected = false

    private var searchQuery: String = ""

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

        // SEARCH LISTENER
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query ?: ""
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText ?: ""
                applyFilters()
                return true
            }
        })

        // BUTTON FILTER LISTENERS
        binding.btnSteam.setOnClickListener {
            steamSelected = !steamSelected
            binding.btnSteam.isSelected = steamSelected
            applyFilters()
        }

        binding.btnGOG.setOnClickListener {
            gogSelected = !gogSelected
            binding.btnGOG.isSelected = gogSelected
            applyFilters()
        }

        binding.btnEpic.setOnClickListener {
            epicSelected = !epicSelected
            binding.btnEpic.isSelected = epicSelected
            applyFilters()
        }

        val adView = binding.adView

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("AdMob", "Banner loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("AdMob", "Banner failed: ${error.message} (${error.code})")
            }
        }

        // Load ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

    }

    private fun loadDeals() {
        binding.progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val deferreds = storeIds.map { id ->
                    async { api.getDeals(storeID = id, pageSize = 50) }
                }

                val lists = deferreds.map { it.await() }
                val merged = lists
                    .flatten()
                    .distinctBy { it.dealID }
                    .sortedByDescending { (it.savings ?: "0").toDoubleOrNull() ?: 0.0 }

                // Fetch Steam app IDs
                val steamAppIds = merged
                    .filter { it.storeID == "1" }
                    .mapNotNull { it.gameID?.takeIf { id -> id.isNotBlank() } }
                    .distinct()

                val idsToFetch = steamAppIds.filter { !steamPriceCache.containsKey(it) }

                if (idsToFetch.isNotEmpty()) {
                    val fetchDeferred = idsToFetch.map { appid ->
                        async {
                            try {
                                val resp = steamApi.getAppDetails(appid, "ID", "en")
                                resp[appid]?.data?.price_overview
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    val results = fetchDeferred.map { it.await() }
                    idsToFetch.forEachIndexed { idx, id -> steamPriceCache[id] = results[idx] }
                }

                fullList = merged
                adapter.setData(merged, steamPriceCache)

            } catch (_: Exception) {
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    // COMBINED FILTER
    private fun applyFilters() {
        if (fullList.isEmpty()) return

        // 1. Filter by store
        val selectedStores = mutableListOf<String>()
        if (steamSelected) selectedStores.add("1")
        if (gogSelected) selectedStores.add("7")
        if (epicSelected) selectedStores.add("25")

        var filtered = if (selectedStores.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.storeID in selectedStores }
        }

        // 2. Filter by search
        val q = searchQuery.lowercase().trim()
        if (q.isNotEmpty()) {
            filtered = filtered.filter {
                it.title?.lowercase()?.contains(q) == true
            }
        }

        adapter.setData(filtered, steamPriceCache)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
