package com.agilbudiprasetyo.newsapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agilbudiprasetyo.newsapp.R
import com.agilbudiprasetyo.newsapp.data.local.entity.NewsEntity
import com.agilbudiprasetyo.newsapp.databinding.ItemNewsLayoutBinding
import com.agilbudiprasetyo.newsapp.utils.DateFormatter
import com.agilbudiprasetyo.newsapp.utils.withDateFormat
import com.bumptech.glide.Glide

class BookmarkAdapter(private val onBookmarkClicked: (NewsEntity) -> Unit): ListAdapter<NewsEntity, BookmarkAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    inner class MyViewHolder(val binding: ItemNewsLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: NewsEntity) {
            val image: Any? = if (data.urlToImage.isNullOrEmpty()) {
                ContextCompat.getDrawable(itemView.context, R.mipmap.ic_no_image)
            } else {
                data.urlToImage
            }
            val author = if (data.author.isNullOrEmpty()) "Anonymous" else data.author
            Log.e("ADAPTER-BOOKMARK", data.toString())

            with(binding) {
                Glide.with(itemView.context)
                    .load(image)
                    .centerCrop()
                    .into(ivItemNewsImage)
                tvItemNewsAuthor.text = author
                tvItemNewsTitle.text = data.title
                tvItemNewsDate.text = DateFormatter.formatDate(data.publishedAt)
                when (data.isBookmarked) {
                    true -> ivItemNewsBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_baseline_bookmarked
                        )
                    )
                    false -> ivItemNewsBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_baseline_bookmark
                        )
                    )
                }
            }
            itemView.setOnClickListener { onItemClickCallback?.onItemClicked(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        val ivBookmark = holder.binding.ivItemNewsBookmark
        if (data != null) {
            holder.bind(data)
        }
        ivBookmark.setOnClickListener { onBookmarkClicked(data) }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data:NewsEntity)
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsEntity>(){
            override fun areItemsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NewsEntity, newItem: NewsEntity): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }
}