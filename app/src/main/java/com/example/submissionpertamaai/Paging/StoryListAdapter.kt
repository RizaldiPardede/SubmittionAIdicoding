package com.example.submissionpertamaai.Paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionpertamaai.ListStoryItem
import com.example.submissionpertamaai.R
import com.example.submissionpertamaai.databinding.StorycardBinding

class StoryListAdapter :
    PagingDataAdapter<ListStoryItem, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class MyViewHolder (private val binding: StorycardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(data: ListStoryItem) {
            binding.username.text = data.name.toString()

            Glide.with(itemView)
                .load(data.photoUrl)
                .error(R.drawable.ic_launcher_background)
                .into(binding.photo)

        }

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener { getItem(position)?.let { it1 ->
                onItemClickCallback.onItemClicked(
                    it1
                )
            } }
        }
    }
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StorycardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }



    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
