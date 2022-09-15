package com.example.entertainmain.ui.movie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.entertainmain.R
import com.example.entertainmain.databinding.FragmentWatchListMoviesBinding
import com.example.entertainmain.ui.movie.adapters.WatchListToWatchAdapter
import com.google.android.material.tabs.TabLayout

class WatchListMoviesFragment: BaseMoviesFragment(R.layout.fragment_watch_list_movies) {
    private lateinit var mBinding: FragmentWatchListMoviesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentWatchListMoviesBinding.bind(view)

        setUpViewPager()
    }

    private fun setUpViewPager() {
        val toWatchAdapter = WatchListToWatchAdapter(requireActivity())
        mBinding.vpTabs.apply {
            adapter = toWatchAdapter
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mBinding.tlWatchlist.getTabAt(position)?.select()
                }
            })
        }
        mBinding.tlWatchlist.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mBinding.vpTabs.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
}