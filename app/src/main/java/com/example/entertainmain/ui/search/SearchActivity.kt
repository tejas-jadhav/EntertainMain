package com.example.entertainmain.ui.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entertainmain.R
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.data.source.db.AppDatabase
import com.example.entertainmain.databinding.ActivitySearchBinding
import com.example.entertainmain.ui.movie.MovieDetailActivity
import com.example.entertainmain.ui.search.adapters.SearchAdapter
import com.example.entertainmain.ui.search.viewmodels.SearchViewModel
import com.example.entertainmain.ui.search.viewmodels.SearchViewModelFactory
import com.example.entertainmain.utils.Constants
import com.example.entertainmain.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySearchBinding
    private lateinit var mSearchView: SearchView
    private lateinit var searchQuery: String
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.searchToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpSearchViewModel()
        setUpSearchRecyclerView()
        observeSearchResults()

        // Verify the action and get the query
        handleIntent(intent)
        performSearch()
    }

    private fun setUpSearchViewModel() {
        val movieRepository = MovieRepository(AppDatabase(this))
        val factory = SearchViewModelFactory(application, movieRepository)
        searchViewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private fun setUpSearchRecyclerView() {
        searchAdapter = SearchAdapter()
        mBinding.rvSearchList.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
        searchAdapter.setOnSearchResultClickListener { movie ->
            val detailsActivityIntent = Intent(this, MovieDetailActivity::class.java)
            detailsActivityIntent.putExtra(MovieDetailActivity.MOVIE_KEY, movie)
            startActivity(detailsActivityIntent)
        }
    }

    private fun observeSearchResults() {
        searchViewModel.searchResult.observe(this) { moviesListResource ->
            Log.d(
                TAG,
                "observeSearchResults: In response handler with data ${moviesListResource.toString()}"
            )
            when (moviesListResource) {
                is Resource.Success -> {
                    mBinding.pbSearching.visibility = View.GONE
                    mBinding.tvNotFound.visibility = View.GONE
                    moviesListResource.data?.movies?.let {
                        searchAdapter.differ.submitList(it)
                        if (it.isEmpty()) {
                            mBinding.tvNotFound.visibility = View.VISIBLE
                            mBinding.tvNotFound.setText(R.string.movie_not_found)
                        }
                    }
                }
                is Resource.Loading -> {
                    mBinding.pbSearching.visibility = View.VISIBLE
                    mBinding.tvNotFound.visibility = View.GONE
                }
                is Resource.Error -> {
                    Log.d(TAG, "observeSearchResults: ${moviesListResource.message.toString()}")
                    mBinding.pbSearching.visibility = View.GONE
                    mBinding.tvNotFound.visibility = View.VISIBLE
                    mBinding.tvNotFound.text = moviesListResource.message
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_movie_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val menuSearchItem = menu?.findItem(R.id.miSearchMovies) as MenuItem
        mSearchView = menuSearchItem.actionView as SearchView
        mSearchView.setSearchableInfo(
            searchManager.getSearchableInfo(componentName)
        )
        var searchDelayJob: Job? = null
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchQuery = it
                    performSearch()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchDelayJob?.cancel()
                searchDelayJob = MainScope().launch {
                    delay(Constants.SEARCH_DELAY)
                    newText?.let {
                        if (it.isNotEmpty() && it.isNotBlank()) {
                            searchQuery = it
                            performSearch()
                        }
                    }
                }
                return true
            }
        })

        menuSearchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                finish()
                return true
            }
        })

        mSearchView.setQuery(searchQuery, false)
        menuSearchItem.expandActionView()
        mSearchView.setQuery(searchQuery, false)
        mBinding.root.requestFocus()

        return true
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchQuery = query
            }
            performSearch()
        }
    }

    private fun performSearch() {
        searchViewModel.searchMovieByTitle(searchQuery)
    }


    private var isScrolling = false
    private var isLastPage = false
    private var isLoading = false

    private val onSearchRecyclerViewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = mBinding.rvSearchList.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.MOVIES_PER_PAGE

            val shouldScroll = isNotLoadingAndNotLastPage && isAtLastItem && isNotBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldScroll) {
                searchViewModel.searchMovieByTitle(searchQuery)
                isScrolling = false
            }
        }
    }
}