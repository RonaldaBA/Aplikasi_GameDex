package com.example.aplikasi_gamedex.onboarding.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.aplikasi_gamedex.R
import android.content.Context


class ThirdPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_page, container,false)
        val finishButton = view.findViewById<View>(R.id.finish)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        finishButton.setOnClickListener {
            findNavController().navigate(R.id.action_ViewPagerFragment_to_SalesFragment)
            onBoardingFinished()
        }
        return view
    }

    companion object {
        private const val PREFS_NAME = "onboarding_prefs"
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}