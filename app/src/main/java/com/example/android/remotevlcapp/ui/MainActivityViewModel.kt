package com.example.android.remotevlcapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.remotevlcapp.domain.server.ObserveCurrentStatusUseCase
import com.example.android.remotevlcapp.domain.server.SendCommandParameters
import com.example.android.remotevlcapp.domain.server.SendCommandUseCase
import com.example.android.remotevlcapp.domain.settings.GetKeepScreenOnUseCase
import com.example.android.remotevlcapp.domain.settings.ObserveThemeUseCase
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    getCurrentStatusUseCase: ObserveCurrentStatusUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    private val getKeepScreenOnUseCase: GetKeepScreenOnUseCase,
    private val sendCommandUseCase: SendCommandUseCase
) : ViewModel() {

    val status: LiveData<Result<Status>> = getCurrentStatusUseCase.execute()
    val theme: LiveData<String> = observeThemeUseCase.execute()

    val shouldKeepScreenOn: Boolean
        get() = getKeepScreenOnUseCase.execute()

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