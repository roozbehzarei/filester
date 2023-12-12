package com.roozbehzarei.filester

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.roozbehzarei.filester.database.File
import com.roozbehzarei.filester.databinding.FileItemViewBinding

class HistoryListAdapter(private val onItemClicked: (File, Int?) -> Unit) :
    ListAdapter<File, HistoryListAdapter.FileViewHolder>(DiffCallback()) {

    private lateinit var binding: FileItemViewBinding
    private var selectedItemPosition: Int? = null

    inner class FileViewHolder(private val binding: FileItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: File) {
            with(binding) {
                textName.text = item.fileName
                textSize.text = item.fileSize.toString() + " MB"
                if (root.isSelected) {
                    itemLayoutDivider.visibility = View.VISIBLE
                    itemActionsLayout.visibility = View.VISIBLE
                } else {
                    itemLayoutDivider.visibility = View.GONE
                    itemActionsLayout.visibility = View.GONE
                }
                itemShareViewHolder.setOnClickListener {
                    onItemClicked(item, 0)
                }
                itemCopyViewHolder.setOnClickListener {
                    onItemClicked(item, 1)
                }
                itemDeleteViewHolder.setOnClickListener {
                    onItemClicked(item, 2)
                }
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
        holder.itemView.isSelected = selectedItemPosition == holder.adapterPosition
        holder.itemView.setOnClickListener {
            selectedItemPosition = if (it.isSelected) {
                null
            } else {
                selectedItemPosition?.let { position ->
                    notifyItemChanged(position)
                }
                holder.adapterPosition
            }
            onItemClicked(currentItem, null)
            notifyItemChanged(position)
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