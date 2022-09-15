package com.example.entertainmain.ui.movie.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
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
import com.example.entertainmain.databinding.ItemImageCardBinding

class SlideAdapter(private val fragment: Fragment, initialMovies: List<Movie> = listOf()) :
    RecyclerView.Adapter<SlideAdapter.SliderViewHolder>() {

    private val TAG = "SlideAdapter"

    inner class SliderViewHolder(val binding: ItemImageCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffUtilCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)

    init {
        differ.submitList(initialMovies)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            ItemImageCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]
        holder.binding.apply {
            tvCardTitle.text = currentMovie.titleText.text
            currentMovie.releaseYear?.year?.let {
                val releaseYear = "(${currentMovie.releaseYear.year})"
                tvReleaseYear.text = releaseYear
            }
            ivCardImage.setImageResource(R.drawable.nav_bg)

            currentMovie.ratingsSummary?.aggregateRating?.let {
                ratingBar.rating = it.toFloat() / 2
            }
        }
        currentMovie?.primaryImage?.let {
            holder.binding.imageProgressBar.visibility = View.VISIBLE
            holder.binding.ivCardImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            Glide.with(fragment)
                .load(it.url)
                .override(310, 380)
                .skipMemoryCache(true)
                .placeholder(R.drawable.gray_block)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
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
        } ?: holder.binding.ivCardImage.setImageResource(R.drawable.no_img_available)

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