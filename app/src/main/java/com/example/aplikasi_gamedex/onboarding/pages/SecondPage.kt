package com.example.aplikasi_gamedex.onboarding.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.aplikasi_gamedex.R

class SecondPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_page, container,false)
        val nextButton = view.findViewById<View>(R.id.next2)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        nextButton.setOnClickListener {
            viewPager?.currentItem = 2
        }
        return view
    }

}