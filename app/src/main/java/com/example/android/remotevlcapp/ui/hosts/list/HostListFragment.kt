package com.example.android.remotevlcapp.ui.hosts.list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_host_list.*
import javax.inject.Inject

class HostListFragment : MainNavigationFragment(), HostListAdapter.OnHostClickListener {

    companion object {
        private const val DELETE_DIALOG_FRAGMENT = "delete_dialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HostListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HostListViewModel::class.java)
        return inflater.inflate(R.layout.fragment_host_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HostListAdapter(this)
        adapter.currentHostId = viewModel.currentHostId
        recyclerView.adapter = adapter

        viewModel.hosts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(HostListFragmentDirections.toHostSearch())
        }
    }

    override fun onEdit(host: HostInfo) {
        findNavController().navigate(
            HostListFragmentDirections.toHostConfiguration(
                host.id,
                host.name,
                host.address,
                host.port,
                host.password
            )
        )
    }

    override fun onDelete(host: HostInfo) {
        HostDeleteDialog(host).show(childFragmentManager, DELETE_DIALOG_FRAGMENT)
    }

    override fun onClick(host: HostInfo) {
        viewModel.switchHost(host)
        findNavController().popBackStack()
    }

    class HostDeleteDialog(private val host: HostInfo) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(context)
                .setTitle(context?.getString(R.string.host_delete_dialog_title))
                .setMessage(context?.getString(R.string.host_delete_dialog_msg))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    val frag = parentFragment as HostListFragment
                    frag.viewModel.deleteHost(host)
                }
                .setNegativeButton(android.R.string.no, null)
                .create()
        }
    }
}

