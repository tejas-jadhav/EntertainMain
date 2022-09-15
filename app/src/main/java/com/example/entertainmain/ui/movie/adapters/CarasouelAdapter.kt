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
import com.example.entertainmain.databinding.ItemCarasouelBinding
import com.example.entertainmain.databinding.ItemCarasouelItemBinding

class CarasouelAdapter(private val fragment: Fragment, initialMovies: List<Movie> = listOf()) :
    RecyclerView.Adapter<CarasouelAdapter.CarasouelViewHolder>() {
    inner class CarasouelViewHolder(val binding: ItemCarasouelItemBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarasouelViewHolder {
        return CarasouelViewHolder(
            ItemCarasouelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CarasouelViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]
        holder.binding.apply {
            tvCarasouelItemTitle.text = currentMovie.titleText.text
            val releaseYear = "(${currentMovie.releaseYear?.year.toString()})"
            tvCarasouelItemYear.text = releaseYear
            currentMovie.ratingsSummary?.aggregateRating?.let {
                carasouelRatingBar.rating = it.toFloat() / 2
            }
        }

        currentMovie?.primaryImage?.let {
            holder.binding.carasouelProgressBar.visibility = View.VISIBLE

            Glide.with(fragment).load(it.url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.gray_block)
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.carasouelProgressBar.visibility = View.GONE
                        holder.binding.ivCarasouel.setImageResource(R.drawable.no_img_available)
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.carasouelProgressBar.visibility = View.GONE
                        holder.binding.ivCarasouel.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }
                })
                .into(holder.binding.ivCarasouel)
        } ?: holder.binding.ivCarasouel.setImageResource(R.drawable.no_img_available)

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