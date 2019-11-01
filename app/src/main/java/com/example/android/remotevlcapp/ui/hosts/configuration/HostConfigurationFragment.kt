package com.example.android.remotevlcapp.ui.hosts.configuration

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.remotevlcapp.R
import com.example.android.remotevlcapp.domain.host.AuthenticationException
import com.example.android.remotevlcapp.event.EventObserver
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.ui.MainNavigationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HostConfigurationViewModel

    private var hostId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HostConfigurationViewModel::class.java)
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

        viewModel.error.observe(this, EventObserver { exception ->
            dissmissConnectionDialog()
            Timber.d(exception)
            when (exception) {
                is UnknownHostException -> address.error = exception.message
                is ConnectException,
                is SocketTimeoutException -> Toast.makeText(
                    context,
                    context?.getString(R.string.unable_to_resolve_host_msg),
                    Toast.LENGTH_SHORT
                )
                    .show()
                is AuthenticationException -> password.error = exception.message
            }
        })

        viewModel.hostEvent.observe(this, EventObserver {
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
            ConnectionDialog(host).show(childFragmentManager, CONNECTION_DIALOG)
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
            val view =
                requireParentFragment().layoutInflater.inflate(R.layout.dialog_connection, null)

            val dialog = MaterialAlertDialogBuilder(context)
                .setMessage(context?.getString(R.string.connect_dialog_message))
                .setView(view)
                .setCancelable(false)
                .create()

            dialog.setOnShowListener {
                val frag = parentFragment as HostConfigurationFragment
                frag.viewModel.checkHostConnection(host)
            }

            dialog.setCanceledOnTouchOutside(false)
            return dialog
        }
    }
}
