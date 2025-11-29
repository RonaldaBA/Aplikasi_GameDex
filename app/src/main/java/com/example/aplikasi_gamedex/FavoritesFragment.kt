package com.example.aplikasi_gamedex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_gamedex.databinding.FragmentFavoritesBinding
import com.example.aplikasi_gamedex.models.CheapSharkDeal
import com.example.aplikasi_gamedex.repository.FavoritesRepository

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FavoritesAdapter
    private var favorites: List<CheapSharkDeal> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoritesAdapter(
            items = emptyList(),
            onClick = { deal ->
                // TODO: navigate to detail game
            },
            onRemoveClick = { deal ->
                FavoritesRepository.removeFavorite(deal)
                loadFavorites()
            }
        )

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        favorites = FavoritesRepository.getFavorites()

        if (favorites.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
            adapter.setData(favorites)
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
