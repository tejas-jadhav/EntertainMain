package com.example.entertainmain.ui

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.entertainmain.R
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.data.source.db.AppDatabase
import com.example.entertainmain.databinding.ActivityMainBinding
import com.example.entertainmain.ui.search.SearchActivity
import com.example.entertainmain.ui.viewmodels.MovieViewModel
import com.example.entertainmain.ui.viewmodels.MovieViewModelProviderFactory


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    private val TAG = "MovieActivity"
    lateinit var moviesViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpActionBar()
        setUpMoviesViewModel()
    }

    private fun setUpMoviesViewModel() {
        val movieRepository = MovieRepository(AppDatabase(this))
        val movieViewModelProviderFactory = MovieViewModelProviderFactory(application, movieRepository)
        moviesViewModel = ViewModelProvider(this, movieViewModelProviderFactory)[MovieViewModel::class.java]
    }

    private fun setUpActionBar() {
        mNavController = (mBinding.drawerHostFragment.getFragment() as NavHostFragment).navController
        setSupportActionBar(mBinding.toolBar)
        val toggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, mBinding.toolBar, R.string.nav_open, R.string.nav_close)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        mBinding.navView.setupWithNavController(mNavController)
        mBinding.navView.menu.getItem(2).setOnMenuItemClickListener {
            Toast.makeText(this, "Change Theme", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_movie_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchComponentName = ComponentName(this, SearchActivity::class.java)

        val searchView = menu?.findItem(R.id.miSearchMovies)?.actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(searchComponentName)
        )

        return true
    }



}