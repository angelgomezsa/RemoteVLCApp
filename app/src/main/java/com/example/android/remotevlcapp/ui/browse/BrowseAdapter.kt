package com.example.android.remotevlcapp.ui.browse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.util.formatFileSize
import com.example.android.remotevlcapp.util.formatFileTime
import kotlinx.android.synthetic.main.item_browse.view.*

class BrowseAdapter(private val listener: OnFileClickListener) :
    ListAdapter<FileInfo, BrowseViewHolder>(FileInfoDiff) {

    interface OnFileClickListener {
        fun onClick(file: FileInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_browse, parent, false)
        return BrowseViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BrowseViewHolder(
    itemView: View,
    private val listener: BrowseAdapter.OnFileClickListener
) : ViewHolder(itemView) {

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


/**
 * Diff items presented by this adapter.
 */
object FileInfoDiff : DiffUtil.ItemCallback<FileInfo>() {
    override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem.path == newItem.path
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
        return oldItem == newItem
    }
}