package com.example.android.remotevlcapp.ui.hosts.search

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.core.result.EventObserver
import com.example.android.core.result.Result
import com.example.android.model.HostInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_host_search.*
import javax.inject.Inject

class HostSearchFragment : MainNavigationFragment(), HostSearchAdapter.OnHostClickListener {

    companion object {
        private const val PASSWORD_DIALOG = "password_dialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HostSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform(requireContext())
        exitTransition =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, false).apply {
                addTarget(R.id.host_search_root)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(HostSearchViewModel::class.java)
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
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.password_dialog_title)
                .setMessage(R.string.password_dialog_msg)
                .setView(R.layout.dialog_password)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    val passwordInput =
                        (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.passwordInput)
                    val password = passwordInput?.text.toString()
                    host.password = password
                    val viewModel = (parentFragment as HostSearchFragment).viewModel
                    viewModel.checkHostConnection(host)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        }
    }
}