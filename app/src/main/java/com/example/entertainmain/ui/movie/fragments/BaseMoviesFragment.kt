package com.example.entertainmain.ui.movie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.entertainmain.ui.MainActivity
import com.example.entertainmain.ui.viewmodels.MovieViewModel

open class BaseMoviesFragment(layoutId: Int): Fragment(layoutId) {

    lateinit var moviesViewModel: MovieViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesViewModel = (activity as MainActivity).moviesViewModel

    }

}