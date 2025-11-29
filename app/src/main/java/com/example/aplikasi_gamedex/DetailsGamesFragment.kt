package com.example.aplikasi_gamedex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.aplikasi_gamedex.databinding.FragmentDetailGameBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.network.CheapSharkAPI
import kotlinx.coroutines.launch

class DetailsGamesFragment : Fragment() {

    private var _binding: FragmentDetailGameBinding? = null
    private val binding get() = _binding!!

    private var gameTitleArg: String? = null
    private var gameIDArg: String? = null

    private val dealsAdapter = DealsAdapter()
    private val cheapSharkApi by lazy { CheapSharkAPI.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { bundle ->
            gameIDArg = bundle.getString("gameID") ?: bundle.getString("gameId")
            gameTitleArg = bundle.getString("title") ?: bundle.getString("gameTitle")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDeals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeals.adapter = dealsAdapter
        binding.rvDeals.isNestedScrollingEnabled = false

        binding.gameTitle.text = gameTitleArg ?: "Unknown Game"

        val gameId = gameIDArg
        val gameTitle = gameTitleArg

        if (!gameId.isNullOrBlank() && !gameTitle.isNullOrBlank()) {
            // Panggil loadDeals dengan gameID untuk Steam/GOG dan title untuk Epic
            loadDeals(gameId, gameTitle)
        } else {
            binding.gameTitle.text = "No game ID or title provided."
        }
    }

    private fun loadDeals(gameId: String, gameTitleArg: String) {
        lifecycleScope.launch {
            try {
                val allDeals = mutableListOf<CheapSharkDeal>()

                // Steam (storeID = 1)
                val steamDeals = cheapSharkApi.getDeals(storeID = 1, pageSize = 50)
                allDeals.addAll(steamDeals)

                // Epic Games (storeID = 2) — ambil semua, filter fleksibel nanti
                val epicDeals = cheapSharkApi.getDeals(storeID = 2, pageSize = 50)
                allDeals.addAll(epicDeals)

                // GOG (storeID = 7)
                val gogDeals = cheapSharkApi.getDeals(storeID = 7, pageSize = 50)
                allDeals.addAll(gogDeals)

                // Filter:
                val filteredDeals = allDeals.filter { deal ->
                    // Steam & GOG: filter pakai gameID
                    deal.gameID?.trim()?.equals(gameId.trim(), ignoreCase = true) == true
                            // Epic: filter pakai title
                            || deal.title?.contains(gameTitleArg, ignoreCase = true) == true
                }

                // Banner game pertama
                filteredDeals.firstOrNull()?.thumb?.let { url ->
                    binding.gameBanner.load(url)
                }

                // Kirim ke adapter
                dealsAdapter.submitList(filteredDeals)

                if (filteredDeals.isEmpty()) {
                    binding.gameTitle.text = "No deals found for this game."
                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.gameTitle.text = "Failed to load deals."
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}