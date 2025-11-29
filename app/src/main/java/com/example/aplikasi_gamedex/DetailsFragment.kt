package com.example.aplikasi_gamedex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.aplikasi_gamedex.databinding.FragmentDetailGameBinding
import com.example.aplikasi_gamedex.network.SteamStoreApi
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailGameBinding? = null
    private val binding get() = _binding!!

    // Gunakan by navArgs() yang aman, tetapi periksa argumennya
    private val args: DetailsFragmentArgs by navArgs()
    private val steamApi by lazy { SteamStoreApi.create() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // HANYA JALANKAN JIKA gameID ADA
        // Ini akan mencegah crash jika halaman ini dibuka secara langsung
        if (args.gameID.isNotEmpty()) {
            loadGameDetails(args.gameID)
        }
    }

    private fun loadGameDetails(gameId: String) {
        lifecycleScope.launch {
            try {
                val response = steamApi.getAppDetails(gameId, country = "ID", lang = "en")
                val gameDetails = response[gameId]?.data
                if (gameDetails != null) {
                    binding.gameTitle.text = gameDetails.name
                    binding.gameBanner.load(gameDetails.header_image)
                    binding.minRequirementsText.text = gameDetails.pc_requirements?.minimum ?: "N/A"
                } else {
                    // Handle error or no data case
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
