package com.example.android.remotevlcapp.ui.hosts.configuration

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.core.domain.host.AuthenticationException
import com.example.android.core.result.EventObserver
import com.example.android.model.HostInfo
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_host_configuration.*
import kotlinx.android.synthetic.main.host_form.*
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class HostConfigurationFragment : MainNavigationFragment() {

    companion object {
        private const val CONNECTION_DIALOG = "connection_dialog"
        private const val ERROR_DIALOG = "error_dialog"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HostConfigurationViewModel
    private var hostId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.X, true).apply {
                addTarget(R.id.host_configuration_root)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(HostConfigurationViewModel::class.java)
        return inflater.inflate(R.layout.fragment_host_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().apply {
            val args = HostConfigurationFragmentArgs.fromBundle(this)
            hostId = args.hostId
            nameInput.setText(args.hostName)
            addressInput.setText(args.hostAddress)
            portInput.setText(args.hostPort)
            passwordInput.setText(args.hostPassword)
        }

        viewModel.error.observe(viewLifecycleOwner, EventObserver { exception ->
            dissmissConnectionDialog()
            Timber.d(exception)
            when (exception) {
                is UnknownHostException -> address.error = exception.message
                is ConnectException,
                is SocketTimeoutException -> {
                    ErrorDialog(context!!.getString(R.string.unable_to_resolve_host_msg))
                        .show(childFragmentManager, ERROR_DIALOG)
                }
                is AuthenticationException -> password.error = exception.message
            }
        })

        viewModel.hostEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack(R.id.navigation_hosts, false)
        })

        save.setOnClickListener {
            clearTextInputErrors()
            val hostName = nameInput.text.toString()
            val hostAddress = addressInput.text.toString()
            val hostPort = portInput.text.toString()
            val hostPassword = passwordInput.text.toString()

            if (hostName.isBlank()) {
                name.error = context?.getString(R.string.host_name_error_msg)
                return@setOnClickListener
            }
            if (hostAddress.isBlank()) {
                address.error = context?.getString(R.string.host_address_error_msg)
                return@setOnClickListener

            }
            if (hostPort.isBlank()) {
                port.error = context?.getString(R.string.port_error_msg)
                return@setOnClickListener

            }
            if (hostPassword.isBlank()) {
                password.error = context?.getString(R.string.password_error_msg)
                return@setOnClickListener
            }

            val host = HostInfo(hostId, hostAddress, hostPort, hostPassword, hostName)
            val connectionDialog = ConnectionDialog(host).apply { isCancelable = false }
            connectionDialog.show(childFragmentManager, CONNECTION_DIALOG)
        }
    }

    private fun clearTextInputErrors() {
        name.error = null
        address.error = null
        port.error = null
        password.error = null
    }

    private fun dissmissConnectionDialog() {
        val dialog = childFragmentManager.findFragmentByTag(CONNECTION_DIALOG) ?: return
        (dialog as ConnectionDialog).dismiss()
    }

    class ConnectionDialog(private val host: HostInfo) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val alertDialog = MaterialAlertDialogBuilder(context)
                .setMessage(R.string.connect_dialog_message)
                .setView(R.layout.dialog_connection)
                .create()

            alertDialog.setOnShowListener {
                val viewModel = (parentFragment as HostConfigurationFragment).viewModel
                viewModel.checkHostConnection(host)
            }

            return alertDialog
        }
    }

    class ErrorDialog(private val message: String) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        }
    }
}
