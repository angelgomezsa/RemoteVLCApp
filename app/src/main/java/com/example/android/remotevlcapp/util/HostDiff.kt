package com.example.android.remotevlcapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.android.model.HostInfo

object HostDiff : DiffUtil.ItemCallback<HostInfo>() {

    override fun areItemsTheSame(oldItem: HostInfo, newItem: HostInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HostInfo, newItem: HostInfo): Boolean {
        return oldItem == newItem
    }
}