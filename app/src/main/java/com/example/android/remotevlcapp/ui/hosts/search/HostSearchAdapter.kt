package com.example.android.remotevlcapp.ui.hosts.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.util.HostDiff
import kotlinx.android.synthetic.main.item_host.view.*

class HostSearchAdapter(
    private val listener: OnHostClickListener
) : ListAdapter<HostInfo, HostSearchViewHolder>(HostDiff) {

    interface OnHostClickListener {
        fun onClick(host: HostInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostSearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_host_search, parent, false)
        return HostSearchViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: HostSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HostSearchViewHolder(
    itemView: View,
    private val listener: HostSearchAdapter.OnHostClickListener
) : RecyclerView.ViewHolder(itemView) {

    fun bind(host: HostInfo) {
        itemView.name.text = host.name
        itemView.address.text = host.address
        itemView.setOnClickListener {
            listener.onClick(host)
        }
    }
}