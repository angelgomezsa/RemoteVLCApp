package com.example.android.remotevlcapp.ui.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.remotevlcapp.domain.server.ObserveCurrentHostUseCase
import com.example.android.remotevlcapp.domain.server.ObserveCurrentStatusUseCase
import com.example.android.remotevlcapp.domain.server.SendCommandParameters
import com.example.android.remotevlcapp.domain.server.SendCommandUseCase
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class RemoteViewModel @Inject constructor(
    private val sendCommandUseCase: SendCommandUseCase,
    observeCurrentHostUseCase: ObserveCurrentHostUseCase,
    observeCurrentStatusUseCase: ObserveCurrentStatusUseCase
) : ViewModel() {

    val status: LiveData<Result<Status>> = observeCurrentStatusUseCase.execute()
    val currentHost: LiveData<HostInfo> = observeCurrentHostUseCase.execute()

    fun sendCommand(command: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendCommandUseCase.execute(SendCommandParameters(command))
        }
    }

    fun sendCommand(command: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendCommandUseCase.execute(SendCommandParameters(command, value))
        }
    }

}