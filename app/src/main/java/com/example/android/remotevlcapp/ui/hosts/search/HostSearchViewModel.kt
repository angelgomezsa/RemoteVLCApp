package com.example.android.remotevlcapp.ui.hosts.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.remotevlcapp.domain.host.AddHostUseCase
import com.example.android.remotevlcapp.domain.host.CheckHostConnectionUseCase
import com.example.android.remotevlcapp.event.Event
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.HostInfo
import com.example.android.remotevlcapp.util.PortSweeper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HostSearchViewModel @Inject constructor(
    private val checkHostConnectionUseCase: CheckHostConnectionUseCase,
    private val addHostUseCase: AddHostUseCase,
    context: Context
) : ViewModel() {

    private val portSweeper = PortSweeper(context)

    private val _sweepResult = MutableLiveData<Result<List<HostInfo>>>()
    val sweepResult: LiveData<Result<List<HostInfo>>> = _sweepResult

    private val _hostConnected = MutableLiveData<Event<Boolean>>()
    val hostConnected: LiveData<Event<Boolean>> = _hostConnected

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    init {
        sweep()
    }

    fun sweep() {
        viewModelScope.launch(Dispatchers.Default) {
            _sweepResult.postValue(portSweeper.sweep())
        }
    }

    fun checkHostConnection(host: HostInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = checkHostConnectionUseCase(host)
            if (result is Result.Success) addHost(result.data)
            else if (result is Result.Error) {
                _errorMessage.postValue(Event(result.exception.message ?: "Error"))
            }
        }
    }

    private suspend fun addHost(host: HostInfo) {
        val result = addHostUseCase(host)
        if (result is Result.Success) _hostConnected.postValue(Event(result.data))
        else if (result is Result.Error) {
            _errorMessage.postValue(Event(result.exception.message ?: "Error"))
        }
    }
}
