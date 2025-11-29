package com.example.aplikasi_gamedex.onboarding.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikasi_gamedex.R
import androidx.viewpager2.widget.ViewPager2

class FirstPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first_page, container,false)
        val nextButton = view.findViewById<View>(R.id.next)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        nextButton.setOnClickListener {
            viewPager?.currentItem = 1
        }
        return view
    }

}