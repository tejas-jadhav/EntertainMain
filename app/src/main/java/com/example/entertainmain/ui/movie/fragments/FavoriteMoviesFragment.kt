package com.example.entertainmain.ui.movie.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.databinding.FragmentFavoriteMoviesBinding
import com.example.entertainmain.ui.movie.MovieDetailActivity
import com.example.entertainmain.ui.movie.adapters.ListAdapter

class FavoriteMoviesFragment : BaseMoviesFragment(R.layout.fragment_favorite_movies) {
    private lateinit var mBinding: FragmentFavoriteMoviesBinding
    private lateinit var listAdapter: ListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentFavoriteMoviesBinding.bind(view)

        setUpFavoritesRecyclerView()
        observeFavoriteMovies()
    }

    private fun setUpFavoritesRecyclerView() {
        listAdapter = ListAdapter()
        listAdapter.setOnItemClickListener(onMovieItemClickListener)
        mBinding.rvFavList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun observeFavoriteMovies() {
        moviesViewModel.getFavoriteMovies().observe(viewLifecycleOwner) { movies ->
            mBinding.tvNoFavs.visibility = View.GONE
            listAdapter.differ.submitList(movies)
            if (movies.isEmpty()) {
                mBinding.tvNoFavs.visibility = View.VISIBLE
            }
        }
    }

    private val onMovieItemClickListener: (Movie) -> Unit = { movie ->
        val detailActivityIntent = Intent(requireContext(), MovieDetailActivity::class.java)
        detailActivityIntent.putExtra(MovieDetailActivity.MOVIE_ID_KEY, movie.id)
        Log.d("movies", "onMovieItemClickListener: ")
        startActivity(detailActivityIntent)
    }
}