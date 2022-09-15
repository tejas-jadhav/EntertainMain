package com.example.entertainmain.ui.movie

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.entertainmain.R
import com.example.entertainmain.databinding.FragmentMainMovieBinding

class MainMovieFragment: Fragment(R.layout.fragment_main_movie) {
    private lateinit var mBinding: FragmentMainMovieBinding
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentMainMovieBinding.bind(view)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //        binding navHost with bottom nav
        mNavController = (mBinding.moviesNavHostFragment.getFragment() as NavHostFragment).navController
        mBinding.bnvMovie.setupWithNavController(mNavController)
    }
}