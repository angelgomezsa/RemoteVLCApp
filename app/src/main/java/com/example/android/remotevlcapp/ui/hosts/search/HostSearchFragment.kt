package com.example.android.remotevlcapp.ui.hosts.search

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.event.EventObserver
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_host_search.*
import javax.inject.Inject

class HostSearchFragment : MainNavigationFragment(), HostSearchAdapter.OnHostClickListener {

    companion object {
        private const val PASSWORD_DIALOG = "password_dialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HostSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(HostSearchViewModel::class.java)
        return inflater.inflate(R.layout.fragment_host_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE
        retry.visibility = View.INVISIBLE
        val adapter = HostSearchAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.GONE
        searchMessage.text = context?.getText(R.string.searching_hosts)

        viewModel.sweepResult.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = View.GONE
            retry.visibility = View.VISIBLE
            if (it is Result.Success) {
                searchMessage.text = context?.getString(R.string.found_hosts)
                adapter.submitList(it.data)
                recyclerView.visibility = View.VISIBLE
            } else if (it is Result.Error) {
                searchMessage.text = it.exception.message
                recyclerView.visibility = View.GONE
            }
        })

        viewModel.hostConnected.observe(viewLifecycleOwner, EventObserver { hasConnection ->
            if (hasConnection) {
                findNavController().popBackStack()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        skip.setOnClickListener {
            findNavController().navigate(HostSearchFragmentDirections.toAddHostFragment())
        }

        retry.setOnClickListener {
            searchMessage.text = context?.getText(R.string.searching_hosts)
            recyclerView.visibility = View.GONE
            retry.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            viewModel.sweep()
        }
    }

    override fun onClick(host: HostInfo) {
        PasswordDialog(host).show(childFragmentManager, PASSWORD_DIALOG)
    }

    class PasswordDialog(private val host: HostInfo) : DialogFragment() {
        @SuppressLint("InflateParams")
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val view =
                requireParentFragment().layoutInflater.inflate(R.layout.dialog_password, null)

            return MaterialAlertDialogBuilder(context)
                .setTitle(context?.getText(R.string.password_dialog_title))
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val password = view.findViewById<TextInputEditText>(R.id.passwordInput)
                    val frag = parentFragment as HostSearchFragment
                    host.password = password.text.toString()
                    frag.viewModel.checkHostConnection(host)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }
    }
}