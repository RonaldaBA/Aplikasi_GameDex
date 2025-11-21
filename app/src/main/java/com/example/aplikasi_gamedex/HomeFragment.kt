package com.example.aplikasi_gamedex

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikasi_gamedex.databinding.FragmentHomeBinding
import androidx.core.net.toUri

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSteam.setOnClickListener {
            val steamWebUrl = "https://store.steampowered.com/"
            val steamUri = "steam://store/".toUri()

            val intent = Intent(Intent.ACTION_VIEW, steamUri).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }

            // Pengecekan apakah perangkat memiliki aplikasinya
            try {
                startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                // Jika aplikasi tidak ada, buka web browser
                val webIntent = Intent(Intent.ACTION_VIEW, steamWebUrl.toUri())
                startActivity(webIntent)
            }
        }

        binding.buttonEpicGames.setOnClickListener {
            val epicWeb = "https://store.epicgames.com/"
            val intent = Intent(Intent.ACTION_VIEW, epicWeb.toUri()).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            startActivity(intent)
        }

        binding.buttonGOG.setOnClickListener {
            val gogWeb = "https://www.gog.com/"
            val intent = Intent(Intent.ACTION_VIEW, gogWeb.toUri()).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}