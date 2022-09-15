package com.example.entertainmain.ui.movie

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.data.repository.MovieRepository
import com.example.entertainmain.data.source.db.AppDatabase
import com.example.entertainmain.databinding.ActivityMovieDetailBinding
import com.example.entertainmain.databinding.MovieDetailBinding
import com.example.entertainmain.ui.movie.adapters.ImageNameAdapter
import com.example.entertainmain.ui.viewmodels.MovieDetailViewModelProviderFactory
import com.example.entertainmain.ui.viewmodels.MovieDetailsViewModel
import com.example.entertainmain.utils.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        const val MOVIE_KEY = "movie"
        const val MOVIE_ID_KEY = "movie_id"
    }

    private lateinit var mBinding: ActivityMovieDetailBinding
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private lateinit var movieDetailBinding: MovieDetailBinding
    private lateinit var castAdapter: ImageNameAdapter
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMovieDetailBinding.inflate(layoutInflater)

        movieDetailBinding = mBinding.bottomSheetDetails
        setContentView(mBinding.root)

        setUpViewMovieDetailsViewModel()
        setUpActionButtons()
        setUpImageAndNameRecyclerView()
        observeCastInfo()
    }


    private fun setUpViewMovieDetailsViewModel() {
        val movieRepository = MovieRepository(AppDatabase(this))
        var gotMovie: Movie? = null
        val movieId = intent.getStringExtra(MOVIE_ID_KEY) as String?
        if (movieId != null) {
            lifecycleScope.launch {
                movieRepository.getFavoriteMovieById(movieId)
                    .observe(this@MovieDetailActivity) { resultMovie ->
                        movie = resultMovie
                        bindBaseMovieData()
                        movieDetailsViewModel.currentMovieId = resultMovie.id
                        movieDetailsViewModel.getMovieCastInfo(resultMovie.id)
                    }
            }
        } else {
            movie = intent.getSerializableExtra(MOVIE_KEY) as Movie
            gotMovie = movie
            bindBaseMovieData()
        }

        val movieDetailsViewModelFactory =
            MovieDetailViewModelProviderFactory(application, movieRepository, gotMovie?.id ?: "")
        movieDetailsViewModel =
            ViewModelProvider(this, movieDetailsViewModelFactory)[MovieDetailsViewModel::class.java]
    }

    private fun setUpImageAndNameRecyclerView() {
        castAdapter = ImageNameAdapter(this)
        movieDetailBinding.rvCastDetails.apply {
            adapter = castAdapter
            layoutManager =
                LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeCastInfo() {
        movieDetailsViewModel.currentCastInfo.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    movieDetailBinding.pbForCast.visibility = View.GONE
                    movieDetailBinding.tvNoConnection.visibility = View.GONE
                    response.data?.let { castInfoResponse ->
                        castAdapter.differ.submitList(castInfoResponse.results.cast.edges)
                    }
                }
                is Resource.Loading -> {
                    movieDetailBinding.pbForCast.visibility = View.VISIBLE
                    movieDetailBinding.tvNoConnection.visibility = View.GONE

                }
                is Resource.Error -> {
                    movieDetailBinding.pbForCast.visibility = View.GONE
                    movieDetailBinding.tvNoConnection.visibility = View.VISIBLE
                    Snackbar.make(movieDetailBinding.root, response.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpActionButtons() {
        mBinding.ibBackButton.setOnClickListener {
            onBackPressed()
        }

        mBinding.ibFavoriteButton.setOnClickListener {
            movieDetailsViewModel.addMovieToFavorites(movie, this)
            Snackbar.make(mBinding.root, "Added to favorites", Snackbar.LENGTH_SHORT).show()
        }

        mBinding.ibWatchListButton.setOnClickListener {
            val watchList = WatchList(
                movie.id,
                movie.titleText.text,
                watched = false
            )
            movieDetailsViewModel.upsertWatchlist(watchList)
            Snackbar.make(mBinding.root, "Added to Watchlist", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun bindBaseMovieData() {
        Log.d("movies", "bindBaseMovieData: Movie $movie")
        if (movie.savedImageFilePath != null) {
            val uri = File(movie.savedImageFilePath).toUri()
            mBinding.ivMainImage.setImageURI(uri)
        } else {
            movie.primaryImage?.url?.let {
                Glide.with(this)
                    .load(it)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .placeholder(R.drawable.gray_block)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            mBinding.ivMainImage.setImageResource(R.drawable.no_img_available)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(mBinding.ivMainImage)
            } ?: mBinding.ivMainImage.setImageResource(R.drawable.no_img_available)

        }


        movieDetailBinding.tvMovieTitle.text = movie.titleText.text
        movieDetailBinding.tvPlotText.text = movie.plot?.plotText?.plainText
        movie.ratingsSummary?.aggregateRating?.let {
            movieDetailBinding.ratingBarDetail.rating = it.toFloat() / 2
            val rating = "$it/10"
            movieDetailBinding.tvImdbRating.text = rating
        }
        movie.genres?.genres?.forEach { genre ->
            val newChip = Chip(this)
            newChip.apply {
                text = genre.text
                chipStrokeWidth = 1.5f
                setTextColor(ContextCompat.getColor(this@MovieDetailActivity, R.color.primaryColor))
                setChipStrokeColorResource(R.color.primaryColor)
                setChipBackgroundColorResource(R.color.transparent)
            }
            movieDetailBinding.chipGroup.addView(newChip)
        }
    }
}