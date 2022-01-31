package com.example.android_course.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android_course.R
import com.example.android_course.entity.Post
import javax.inject.Inject

class PostAdapter @Inject constructor() :
    PagingDataAdapter<Post, PostAdapter.PostViewHolder>(PostComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (viewType) {
            0 -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_post_text, parent, false)
                PostTextViewHolder(itemView)
            }
            else -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_post_image, parent, false)
                PostImageViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            return if (it.imageUrl == null)
                0
            else
                1
        }
        return 0
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        when (holder) {
            is PostImageViewHolder -> {
                getItem(position)?.let {
                    holder.titleTextView.text = it.title
                    holder.textTextView.text = it.text ?: "---"
                    holder.linkTextView.text = it.linkUrl ?: "---"
                    Glide.with(holder.postImageView)
                        .load(it.imageUrl)
                        .into(holder.postImageView)
                    holder.postImageView.setBackgroundResource(R.drawable.post_background)
                }
            }
            is PostTextViewHolder -> {
                getItem(position)?.let {
                    holder.titleTextView2.text = it.title
                    holder.textTextView2.text = it.text ?: "---"
                    holder.linkTextView2.text = it.linkUrl ?: "---"
                }
            }
        }
    }


    object PostComparator : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem == newItem
    }

    open class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val avatarImageView = itemView.findViewById<ImageView>(R.id.avatarImageView)
        //val firstRowView = itemView.findViewById<TextView>(R.id.first_row)
        //val secondRowView = itemView.findViewById<TextView>(R.id.second_row)

    }

    class PostImageViewHolder(itemView: View) : PostViewHolder(itemView) {
        val postImageView = itemView.findViewById<ImageView>(R.id.postImageView)
        val titleTextView = itemView.findViewById<TextView>(R.id.post_title)
        val textTextView = itemView.findViewById<TextView>(R.id.post_text)
        val linkTextView = itemView.findViewById<TextView>(R.id.post_link)
    }

    class PostTextViewHolder(itemView: View) : PostViewHolder(itemView) {
        val titleTextView2 = itemView.findViewById<TextView>(R.id.post_title_2)
        val textTextView2 = itemView.findViewById<TextView>(R.id.post_text_2)
        val linkTextView2 = itemView.findViewById<TextView>(R.id.post_link_2)
    }
}