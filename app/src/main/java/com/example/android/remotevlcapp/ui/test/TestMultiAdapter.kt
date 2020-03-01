package com.example.android.remotevlcapp.ui.test

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.core.result.Result
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import kotlinx.android.synthetic.main.item_browse.view.*

class TestMultiAdapter : RecyclerView.Adapter<TestMultiViewHolder>() {

    val differ = AsyncListDiffer<Any>(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestMultiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_browse -> {
                val itemView = inflater.inflate(viewType, parent, false)
                TestMultiViewHolder.FileInfoViewHolder(itemView)
            }
            R.layout.browse_loading,
            R.layout.test_no_connection -> {
                val itemView = inflater.inflate(viewType, parent, false)
                TestMultiViewHolder.InformationViewHolder(itemView)
            }
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: TestMultiViewHolder, position: Int) {
        when (holder) {
            is TestMultiViewHolder.FileInfoViewHolder -> holder.bind(differ.currentList[position] as FileInfo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is FileInfo -> R.layout.item_browse
            is Result.Loading -> R.layout.browse_loading
            is Result.Error -> R.layout.test_no_connection
            else -> throw IllegalStateException("Unknown view type at position $position")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

sealed class TestMultiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class FileInfoViewHolder(
        itemView: View
    ) : TestMultiViewHolder(itemView) {

        fun bind(file: FileInfo) {
            val icon = if (file.type == "dir" || file.name == "..") {
                itemView.context.resources.getDrawable(R.drawable.ic_folder, null)
            } else {
                itemView.context.resources.getDrawable(R.drawable.ic_file, null)
            }

            itemView.icon.setImageDrawable(icon)
            itemView.name.text = file.name

        }
    }

    class InformationViewHolder(itemView: View) : TestMultiViewHolder(itemView)
}

/**
 * Diff items presented by this adapter.
 */
object DiffCallback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FileInfo && newItem is FileInfo ->
                oldItem.path == newItem.path
            oldItem is Result.Error && newItem is Result.Error -> oldItem == newItem
            oldItem is Result.Loading && newItem is Result.Loading -> oldItem == newItem
            else -> false
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FileInfo && newItem is FileInfo -> oldItem == newItem
            oldItem is Result.Error && newItem is Result.Error -> oldItem == newItem
            oldItem is Result.Loading && newItem is Result.Loading -> oldItem == newItem
            else -> true
        }
    }
}