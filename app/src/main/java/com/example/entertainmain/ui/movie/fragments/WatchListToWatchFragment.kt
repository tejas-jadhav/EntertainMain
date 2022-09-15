package com.example.entertainmain.ui.movie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.entertainmain.R
import com.example.entertainmain.databinding.FragmentWatchListToWatchBinding
import com.example.entertainmain.databinding.ItemWatchListToWatchBinding
import com.example.entertainmain.ui.movie.adapters.CheckedListAdapter

class WatchListToWatchFragment : BaseMoviesFragment(R.layout.fragment_watch_list_to_watch) {

    private lateinit var mBinding: FragmentWatchListToWatchBinding
    private lateinit var toWatchAdapter: CheckedListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentWatchListToWatchBinding.bind(view)

        setUpToWatchRecyclerView()
        observeToWatchList()
    }

    private fun setUpToWatchRecyclerView() {
        toWatchAdapter = CheckedListAdapter()
        mBinding.rvWatchlist.apply {
            adapter = toWatchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        toWatchAdapter.setOnCheckClickListener { watchlist ->
            watchlist.watched = true
            moviesViewModel.upsertWatchlist(watchlist)
        }
    }

    private fun observeToWatchList() {
        moviesViewModel.getWatchlistToWatch().observe(viewLifecycleOwner) { movies ->
            mBinding.tvNoWatchlist.visibility = View.GONE
            toWatchAdapter.differ.submitList(movies)
            if (movies.isEmpty()) {
                mBinding.tvNoWatchlist.visibility = View.VISIBLE
            }
        }
    }
}