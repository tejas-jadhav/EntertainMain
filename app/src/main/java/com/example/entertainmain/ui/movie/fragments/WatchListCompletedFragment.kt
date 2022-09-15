package com.example.entertainmain.ui.movie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.entertainmain.R
import com.example.entertainmain.databinding.FragmentWatchListCompletedBinding
import com.example.entertainmain.ui.movie.adapters.DeleteListAdapter

class WatchListCompletedFragment : BaseMoviesFragment(R.layout.fragment_watch_list_completed) {
    private lateinit var mBinding: FragmentWatchListCompletedBinding
    private lateinit var deleteListAdapter: DeleteListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentWatchListCompletedBinding.bind(view)

        setUpCompletedRecyclerView()
        observeCompletedWatchlist()
    }

    private fun setUpCompletedRecyclerView() {
        deleteListAdapter = DeleteListAdapter()
        mBinding.rvWatchlist.apply {
            adapter = deleteListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        deleteListAdapter.setOnDeleteClickListener { watchlist ->
            moviesViewModel.deleteWatchlist(watchlist)
        }
    }

    private fun observeCompletedWatchlist() {
        moviesViewModel.getWatchlistWatched().observe(viewLifecycleOwner) { completedWatchlist ->
            mBinding.tvNoWatchlist.visibility = View.GONE
            deleteListAdapter.differ.submitList(completedWatchlist)
            if (completedWatchlist.isEmpty()) {
                mBinding.tvNoWatchlist.visibility = View.VISIBLE
            }
        }
    }

}