package com.example.entertainmain.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.databinding.ItemMovieListBinding

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(val binding: ItemMovieListBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            ItemMovieListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]

        holder.binding.tvListTitle.text = currentMovie.titleText.text
        currentMovie.primaryImage?.url?.let {
            Glide.with(holder.binding.root)
                .load(it)
                .override(130, 190)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.gray_block)
                .error(R.drawable.no_img_available)
                .into(holder.binding.ivMovie)
        } ?: holder.binding.ivMovie.setImageResource(R.drawable.no_img_available)

        holder.binding.root.setOnClickListener {
            onSearchResultClickListener?.let {  it(currentMovie) }
        }
    }

    private var onSearchResultClickListener: ((Movie) -> Unit)? = null

    fun setOnSearchResultClickListener(callback: (Movie) -> Unit) {
        onSearchResultClickListener = callback
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }
}