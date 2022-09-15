package com.example.entertainmain.ui.movie.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.entertainmain.ui.movie.fragments.WatchListCompletedFragment
import com.example.entertainmain.ui.movie.fragments.WatchListToWatchFragment

class WatchListToWatchAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> WatchListToWatchFragment()
            1 -> WatchListCompletedFragment()
            else -> WatchListToWatchFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}