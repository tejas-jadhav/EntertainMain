package com.example.entertainmain.ui.movie.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.entertainmain.R
import com.example.entertainmain.data.model.Movie
import com.example.entertainmain.databinding.ItemMovieListBinding
import java.io.File

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    inner class ListViewHolder(val binding: ItemMovieListBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemMovieListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentMovie = differ.currentList[position]

        holder.binding.tvListTitle.text = currentMovie.titleText.text


        currentMovie?.savedImageFilePath?.let { filePath ->
            val uri = File(filePath).toUri()
            holder.binding.ivMovie.setImageURI(uri)
        } ?: holder.binding.ivMovie.setImageResource(R.drawable.no_img_available)

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(currentMovie)
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    private var onItemClickListener: ((Movie) -> Unit)? = null

    fun setOnItemClickListener(callback: (Movie) -> Unit) {
        onItemClickListener = callback
    }
}