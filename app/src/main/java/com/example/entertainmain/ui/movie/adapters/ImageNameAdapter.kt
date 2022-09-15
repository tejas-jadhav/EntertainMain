package com.example.entertainmain.ui.movie.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.example.entertainmain.data.model.Edge
import com.example.entertainmain.databinding.ItemImageAndNameBinding

class ImageNameAdapter(private val context: Context): RecyclerView.Adapter<ImageNameAdapter.ImageNameViewHolder>() {
    inner class ImageNameViewHolder(val binding: ItemImageAndNameBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object: DiffUtil.ItemCallback<Edge>() {
        override fun areItemsTheSame(oldItem: Edge, newItem: Edge): Boolean {
            return oldItem.node.name.id == newItem.node.name.id
        }

        override fun areContentsTheSame(oldItem: Edge, newItem: Edge): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageNameViewHolder {
        return ImageNameViewHolder(
            ItemImageAndNameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageNameViewHolder, position: Int) {
        val currentEdge = differ.currentList[position]
        currentEdge.node.name.nameText?.text?.let {
            holder.binding.tvNameOfPerson.text = it
        }
        currentEdge.node.name.primaryImage?.url?.let {
            Glide.with(context)
                .load(it)
                .override(50, 50)
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
                        holder.binding.ivPersonImage.setImageResource(R.drawable.ic_baseline_person_24)
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
                .into(holder.binding.ivPersonImage)
        } ?: holder.binding.ivPersonImage.setImageResource(R.drawable.ic_baseline_person_24)
    }

    override fun getItemCount() = differ.currentList.size
}