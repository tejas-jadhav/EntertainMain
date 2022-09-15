package com.example.entertainmain.ui.movie.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.databinding.ItemWatchListCompletedBinding

class DeleteListAdapter: RecyclerView.Adapter<DeleteListAdapter.DeleteListViewHolder>() {
    inner class DeleteListViewHolder(val binding: ItemWatchListCompletedBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<WatchList>() {
        override fun areItemsTheSame(oldItem: WatchList, newItem: WatchList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WatchList, newItem: WatchList): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteListViewHolder {
        return DeleteListViewHolder(
            ItemWatchListCompletedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeleteListViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.binding.tvWatchlistTitle.text = currentItem.movieTitle
        holder.binding.ibDelete.setOnClickListener {
            onDeleteClickListener?.let {
                it(currentItem)
            }
        }
    }

    private var onDeleteClickListener: ((WatchList) -> Unit)? = null

    fun setOnDeleteClickListener(callback: (WatchList) -> Unit) {
        onDeleteClickListener = callback
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}