package ru.molinov.pagingreddit.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.molinov.pagingreddit.data.Post
import ru.molinov.pagingreddit.databinding.RecyclerItemBinding
import javax.inject.Inject

class MainAdapter @Inject constructor() :
    PagingDataAdapter<Post, MainAdapter.DataViewHolder>(DiffUtils) {

    object DiffUtils : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            RecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply { itemView.setOnClickListener { onClick(it) } }
    }

    inner class DataViewHolder(private val binding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(post: Post) = with(binding) {
            description.text = post.title
            stars.text = post.total_awards_received.toString()
            comments.text = post.num_comments
        }

        override fun onClick(view: View?) {
            Toast.makeText(
                view?.context,
                "author is ${getItem(absoluteAdapterPosition)?.author}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
