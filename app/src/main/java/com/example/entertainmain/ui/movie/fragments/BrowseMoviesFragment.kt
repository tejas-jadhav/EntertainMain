package com.example.entertainmain.ui.movie.fragments

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.data.model.MoviesListResponse
import com.example.entertainmain.databinding.FragmentBrowseMoviesBinding
import com.example.entertainmain.databinding.RvImageCardSlideBinding
import com.example.entertainmain.ui.movie.MovieDetailActivity
import com.example.entertainmain.ui.movie.adapters.CarasouelAdapter
import com.example.entertainmain.ui.movie.adapters.ScrollAdapter
import com.example.entertainmain.ui.movie.adapters.SlideAdapter
import com.example.entertainmain.utils.Constants
import com.example.entertainmain.utils.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class BrowseMoviesFragment : BaseMoviesFragment(R.layout.fragment_browse_movies) {

    private lateinit var mBinding: FragmentBrowseMoviesBinding
    private lateinit var topRatedMoviesAdapter: ScrollAdapter
    private lateinit var trendingMoviesAdapter: SlideAdapter
    private lateinit var popularMoviesAdapter: SlideAdapter
    private lateinit var carasouelAdapter: CarasouelAdapter

    private val TAG = "BrowseMovieFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentBrowseMoviesBinding.bind(view)
        setUpMainCarasouelViewPager()
        setUpTrendingRecyclerView()
        setUpPopularRecyclerView()
        setUpTopRatedMoviesRecyclerView()

        observeCarasouelMovies()
        observeTopRatedMovies()
        observeSlidingMovies(
            moviesViewModel.trendingMovies,
            trendingMoviesAdapter,
            mBinding.rvTrendingSlide
        )
        observeSlidingMovies(
            moviesViewModel.popularMovies,
            popularMoviesAdapter,
            mBinding.rvPopularSlide
        )
    }

    private fun observeCarasouelMovies() {
        moviesViewModel.carasouelMovies.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    mBinding.vpMainCarasouel.placeHolderItem.root.visibility = View.GONE
                    response.data?.let { moviesResponse ->
                        carasouelAdapter.differ.submitList(moviesResponse.movies)
                    }
                }
                is Resource.Loading -> {
                    mBinding.vpMainCarasouel.placeHolderItem.root.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    mBinding.vpMainCarasouel.placeHolderItem.root.visibility = View.GONE
                    Log.e(TAG, "observeCarasouelMovies: ${response.message}")
                    Snackbar.make(mBinding.root, response.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun observeSlidingMovies(
        observableMovieList: MutableLiveData<Resource<MoviesListResponse>>,
        slideAdapter: SlideAdapter,
        binding: RvImageCardSlideBinding
    ) {
        observableMovieList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.slideProgressBar.visibility = View.GONE
                    response.data?.let { moviesResponse ->
                        slideAdapter.differ.submitList(moviesResponse.movies)
                    }
                }
                is Resource.Loading -> {
                    binding.slideProgressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.slideProgressBar.visibility = View.GONE
                    Log.e(TAG, "observeSlidingMovies: ${response.message}")
                }
            }
        }
    }

    private fun observeTopRatedMovies() {
        moviesViewModel.topRatedMovies.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    mBinding.rvTopRated.scrollProgressBar.visibility = View.GONE
                    response.data?.let { moviesResponse ->
                        topRatedMoviesAdapter.differ.submitList(moviesResponse.movies.toList())
                    }
                }
                is Resource.Loading -> {
                    mBinding.rvTopRated.scrollProgressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    mBinding.rvTopRated.scrollProgressBar.visibility = View.GONE
                    Log.e(TAG, "observeTopRatedMovies: ${response.message}")
                }
            }
        }
    }


    private fun setUpMainCarasouelViewPager() {
        carasouelAdapter = CarasouelAdapter(this)
        carasouelAdapter.setOnItemClickListener(onMovieItemClickListener)
        mBinding.vpMainCarasouel.vpCarasouel.apply {
            adapter = carasouelAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        TabLayoutMediator(
            mBinding.vpMainCarasouel.tlDots,
            mBinding.vpMainCarasouel.vpCarasouel
        ) { _, _ -> }.attach()
    }

    private fun setUpTrendingRecyclerView() {
        trendingMoviesAdapter = SlideAdapter(this)
        trendingMoviesAdapter.setOnItemClickListener(onMovieItemClickListener)
        mBinding.rvTrendingSlide.tvSeparatorTitle.setText(R.string.trending_movies)
        mBinding.rvTrendingSlide.rvImageCardSlide.apply {
            adapter = trendingMoviesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpPopularRecyclerView() {
        popularMoviesAdapter = SlideAdapter(this)
        popularMoviesAdapter.setOnItemClickListener(onMovieItemClickListener)
        mBinding.rvPopularSlide.tvSeparatorTitle.setText(R.string.popular_movies)
        mBinding.rvPopularSlide.rvImageCardSlide.apply {
            adapter = popularMoviesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setUpTopRatedMoviesRecyclerView() {
        topRatedMoviesAdapter = ScrollAdapter(this)
        topRatedMoviesAdapter.setOnItemClickListener(onMovieItemClickListener)
        mBinding.rvTopRated.tvSeparatorTitle.setText(R.string.top_rated_movies)

        mBinding.rvTopRated.rvScroll.apply {
            adapter = topRatedMoviesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addOnScrollListener(topRatedRecyclerViewScrollListener)
        }
    }

    private val onMovieItemClickListener = { movie: Movie ->
//        Toast.makeText(requireContext(), movie.titleText.text, Toast.LENGTH_SHORT).show()
        val detailActivityIntent = Intent(requireContext(), MovieDetailActivity::class.java)
        detailActivityIntent.putExtra(MovieDetailActivity.MOVIE_KEY, movie)
        startActivity(detailActivityIntent)
    }
    private var isTopRatedRecyclerViewScrolling = false
    private var isTopRatedMoviesLoading = false
    private var isTopRatedMoviesLastPage = false

    private val topRatedRecyclerViewScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isTopRatedRecyclerViewScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = mBinding.rvTopRated.rvScroll.layoutManager as GridLayoutManager
            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isTopRatedMoviesLoading && !isTopRatedMoviesLastPage
            val isAtLastItem = firstItemVisiblePosition + visibleItemCount >= totalItemCount
            val isNotBeginning = firstItemVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.MOVIES_PER_PAGE

            val shouldPaginate = (isNotLoadingAndNotLastPage && isAtLastItem
                    && isNotBeginning && isTotalMoreThanVisible && isTopRatedRecyclerViewScrolling)

            if (shouldPaginate) {
                moviesViewModel.getTopRatedMovies()
                Log.d(TAG, "onScrolled: Reached last so paginate now")
                isTopRatedRecyclerViewScrolling = false
            }
        }
    }

}