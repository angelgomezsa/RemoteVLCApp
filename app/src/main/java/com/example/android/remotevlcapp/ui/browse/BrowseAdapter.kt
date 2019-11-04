package com.example.android.remotevlcapp.ui.browse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.core.result.Result
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.browse.BrowseViewHolder.FileInfoViewHolder
import com.example.android.remotevlcapp.ui.browse.BrowseViewHolder.InformationViewHolder
import com.example.android.remotevlcapp.util.formatFileSize
import com.example.android.remotevlcapp.util.formatFileTime
import kotlinx.android.synthetic.main.item_browse.view.*

class BrowseAdapter(private val listener: OnFileClickListener) :
    RecyclerView.Adapter<BrowseViewHolder>() {

    val differ = AsyncListDiffer<Any>(this, DiffCallback)

    interface OnFileClickListener {
        fun onClick(file: FileInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_browse -> {
                val itemView = inflater.inflate(viewType, parent, false)
                FileInfoViewHolder(itemView, listener)
            }
            R.layout.browse_loading,
            R.layout.browse_no_connection -> {
                val itemView = inflater.inflate(viewType, parent, false)
                InformationViewHolder(itemView)
            }
            else -> throw IllegalStateException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        when (holder) {
            is FileInfoViewHolder -> holder.bind(differ.currentList[position] as FileInfo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is FileInfo -> R.layout.item_browse
            is Result.Loading -> R.layout.browse_loading
            is Result.Error -> R.layout.browse_no_connection
            else -> throw IllegalStateException("Unknown view type at position $position")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

sealed class BrowseViewHolder(itemView: View) : ViewHolder(itemView) {

    class FileInfoViewHolder(
        itemView: View,
        private val listener: BrowseAdapter.OnFileClickListener
    ) : BrowseViewHolder(itemView) {

        fun bind(file: FileInfo) {
            val icon = if (file.type == "dir" || file.name == "..") {
                itemView.context.resources.getDrawable(R.drawable.ic_folder, null)
            } else {
                itemView.context.resources.getDrawable(R.drawable.ic_file, null)
            }

            itemView.icon.setImageDrawable(icon)
            itemView.name.text = file.name
            file.size?.let {
                if (it != 0L && file.name != "..") {
                    itemView.size.text = formatFileSize(it)
                } else {
                    itemView.size.text = ""
                }
            }
            file.modification_time?.let {
                if (it != 0 && file.name != "..") {
                    itemView.modification_time.text = formatFileTime(it)
                } else {
                    itemView.modification_time.text = ""
                }
            }
            itemView.setOnClickListener {
                listener.onClick(file)
            }
        }
    }

    class InformationViewHolder(itemView: View) : BrowseViewHolder(itemView)
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