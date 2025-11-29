package com.example.aplikasi_gamedex.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikasi_gamedex.R
import com.example.aplikasi_gamedex.onboarding.pages.FirstPage
import com.example.aplikasi_gamedex.onboarding.pages.SecondPage
import com.example.aplikasi_gamedex.onboarding.pages.ThirdPage
import androidx.viewpager2.widget.ViewPager2

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList: ArrayList<Fragment> = arrayListOf(
            FirstPage(),
            SecondPage(),
            ThirdPage()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter

        return view

    }

}