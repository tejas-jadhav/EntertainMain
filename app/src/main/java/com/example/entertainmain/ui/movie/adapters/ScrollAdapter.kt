package com.example.entertainmain.ui.movie.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.databinding.ItemImageCardSmallBinding

class ScrollAdapter(private val fragment: Fragment, initialMovies: List<Movie> = listOf()) :
    RecyclerView.Adapter<ScrollAdapter.ScrollRecyclerViewHolder>() {
    inner class ScrollRecyclerViewHolder(val binding: ItemImageCardSmallBinding) :
        RecyclerView.ViewHolder(binding.root)

    val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    init {
        differ.submitList(initialMovies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrollRecyclerViewHolder {
        return ScrollRecyclerViewHolder(
            ItemImageCardSmallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ScrollRecyclerViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]

        holder.binding.apply {
            tvCardTitle.text = currentMovie.titleText.text
            tvReleaseYear.text = currentMovie.releaseYear?.year.toString()
            val rankString = "${position + 1}."
            tvMovieRank.text = rankString
            currentMovie.ratingsSummary?.aggregateRating?.let {
                ratingBar.rating = it.toFloat() / 2
            }
        }

        currentMovie.primaryImage?.url?.let {
            holder.binding.imageProgressBar.visibility = View.VISIBLE
            holder.binding.ivCardImage.scaleType = ImageView.ScaleType.CENTER_INSIDE

            Glide.with(fragment)
                .load(it)
                .override(160, 260)
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
                        holder.binding.imageProgressBar.visibility = View.GONE
                        holder.binding.ivCardImage.setImageResource(R.drawable.no_img_available)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.imageProgressBar.visibility = View.GONE
                        holder.binding.ivCardImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }
                })
                .into(holder.binding.ivCardImage)
        }
        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(currentMovie)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Movie) -> Unit)? = null

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }
}