package com.example.android.remotevlcapp.ui.test

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.model.FileInfo
import com.example.android.remotevlcapp.R
import kotlinx.android.synthetic.main.item_browse.view.*

class TestAdapter :
    ListAdapter<FileInfo, TestViewHolder>(FileInfoDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_browse, parent, false)
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TestViewHolder(
    itemView: View
) : ViewHolder(itemView) {

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