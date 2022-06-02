package com.rouzbehzarei.filester.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rouzbehzarei.filester.database.File
import com.rouzbehzarei.filester.databinding.HistoryItemViewBinding

class HistoryListAdapter(private val onItemClicked: (File) -> Unit) :
    ListAdapter<File, HistoryListAdapter.FileViewHolder>(DiffCallback()) {

    private lateinit var binding: HistoryItemViewBinding

    class FileViewHolder(val binding: HistoryItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: File) {
            binding.apply {
                textName.text = item.fileName
                textSize.text = item.fileSize.toString() + " MB"
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = HistoryItemViewBinding.inflate(layoutInflater, parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem)
        }
        holder.bind(currentItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<File>() {

        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }

    }

}