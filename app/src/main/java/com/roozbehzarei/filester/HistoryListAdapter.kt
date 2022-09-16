package com.roozbehzarei.filester

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.roozbehzarei.filester.database.File
import com.roozbehzarei.filester.databinding.FileItemViewBinding

class HistoryListAdapter(private val onItemClicked: (File) -> Unit) :
    ListAdapter<File, HistoryListAdapter.FileViewHolder>(DiffCallback()) {

    private lateinit var binding: FileItemViewBinding

    class FileViewHolder(private val binding: FileItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: File) {
            binding.apply {
                textName.text = item.fileName
                textSize.text = item.fileSize.toString() + " MB"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = FileItemViewBinding.inflate(layoutInflater, parent, false)
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