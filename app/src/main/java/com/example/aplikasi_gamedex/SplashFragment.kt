package com.example.aplikasi_gamedex

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import android.os.Handler
import android.os.Looper

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler(Looper.getMainLooper()).postDelayed({
            if (onBoardingFinished()){
                findNavController().navigate(R.id.action_SplashFragment_to_SalesFragment)
            } else{
                findNavController().navigate(R.id.action_SplashFragment_to_ViewPagerFragment)
            }
        }, 3000)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    companion object {
        private const val PREFS_NAME = "onboarding_prefs"
    }

    private fun onBoardingFinished(): Boolean{
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}