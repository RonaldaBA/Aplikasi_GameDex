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
import com.example.aplikasi_gamedex.repository.FavoritesRepository
import kotlinx.coroutines.launch

class DetailsGamesFragment : Fragment() {

    private var _binding: FragmentDetailGameBinding? = null
    private val binding get() = _binding!!

    private var dealArg: CheapSharkDeal? = null

    private val dealsAdapter = DealsAdapter()
    private val cheapSharkApi by lazy { CheapSharkAPI.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data deal dari Bundle
        dealArg = arguments?.getParcelable("deal")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        val deal = dealArg

        if (deal != null) {
            // Set title & banner
            binding.gameTitle.text = deal.title
            binding.gameBanner.load(deal.thumb)

            // Set icon awal
            updateFavoriteIcon(deal)

            // Favorite button click
            binding.btnFavorite.setOnClickListener {
                if (FavoritesRepository.isFavorite(deal.dealID)) {
                    FavoritesRepository.removeFavorite(deal)
                } else {
                    FavoritesRepository.addFavorite(deal)
                }
                updateFavoriteIcon(deal)
            }

            // Load deals lain (Steam/GOG by gameID, Epic by title)
            loadDeals(deal.gameID, deal.title)
        } else {
            binding.gameTitle.text = "Game data not found."
        }
    }

    private fun updateFavoriteIcon(deal: CheapSharkDeal) {
        val isFav = FavoritesRepository.isFavorite(deal.dealID)
        binding.btnFavorite.setImageResource(
            if (isFav) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    private fun loadDeals(gameId: String?, title: String) {
        lifecycleScope.launch {
            try {
                val allDeals = mutableListOf<CheapSharkDeal>()

                // Steam (1)
                allDeals += cheapSharkApi.getDeals(storeID = 1, pageSize = 50)

                // Epic (25)
                allDeals += cheapSharkApi.getDeals(storeID = 25, pageSize = 50)

                // GOG (7)
                allDeals += cheapSharkApi.getDeals(storeID = 7, pageSize = 50)

                // Filter deals:
                val filteredDeals = allDeals.filter { deal ->
                    // Steam & GOG → pakai gameID
                    deal.gameID == gameId ||
                            // Epic → pakai title
                            deal.title.contains(title, ignoreCase = true)
                }

                // Tampilkan di adapter
                dealsAdapter.submitList(filteredDeals)

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
