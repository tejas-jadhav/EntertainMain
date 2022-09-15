package com.example.entertainmain.ui.movie.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.entertainmain.data.model.WatchList
import com.example.entertainmain.databinding.ItemWatchListToWatchBinding

class CheckedListAdapter: RecyclerView.Adapter<CheckedListAdapter.CheckedListViewHolder>() {
    inner class CheckedListViewHolder(val binding: ItemWatchListToWatchBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<WatchList>() {
        override fun areItemsTheSame(oldItem: WatchList, newItem: WatchList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WatchList, newItem: WatchList): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedListViewHolder {
        return CheckedListViewHolder(
            ItemWatchListToWatchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CheckedListViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.binding.tvWatchlistTitle.text = currentItem.movieTitle
        holder.binding.cbWatched.isChecked = currentItem.watched
        holder.binding.cbWatched.setOnClickListener {
            onCheckClickListener?.let {
                it(currentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onCheckClickListener: ((WatchList) -> Unit)? = null

    fun setOnCheckClickListener(callback: (WatchList) -> Unit) {
        onCheckClickListener = callback
    }

}