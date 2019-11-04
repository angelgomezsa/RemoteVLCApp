package com.example.android.remotevlcapp.ui.hosts.list

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.android.model.HostInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.util.HostDiff
import kotlinx.android.synthetic.main.item_host.view.*

class HostListAdapter(private val listener: OnHostClickListener) :
    ListAdapter<HostInfo, HostListViewHolder>(HostDiff) {

    var currentHostId: Int = -1

    interface OnHostClickListener {
        fun onEdit(host: HostInfo)
        fun onDelete(host: HostInfo)
        fun onClick(host: HostInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_host, parent, false)
        return HostListViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: HostListViewHolder, position: Int) {
        holder.bind(getItem(position))
        val context = holder.itemView.context
        if (currentHostId == getItem(position).id) {
            holder.itemView.background = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.remote_button_ripple,
                null
            )
        } else {
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            holder.itemView.setBackgroundResource(outValue.resourceId)
        }
    }
}

class HostListViewHolder(
    itemView: View,
    private val listener: HostListAdapter.OnHostClickListener
) : ViewHolder(itemView) {

    fun bind(host: HostInfo) {
        val context = itemView.context
        itemView.name.text = host.name
        itemView.address.text = host.address
        itemView.menu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.host_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit_host -> {
                        listener.onEdit(host)
                        true
                    }
                    R.id.remove_host -> {
                        listener.onDelete(host)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        itemView.setOnClickListener {
            listener.onClick(host)
        }
    }
}
