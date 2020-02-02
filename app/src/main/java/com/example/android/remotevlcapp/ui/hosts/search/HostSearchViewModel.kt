package com.example.android.remotevlcapp.ui.hosts.search

import android.content.Context
import androidx.lifecycle.*
import com.example.android.core.domain.host.AddHostUseCase
import com.example.android.core.domain.host.CheckHostConnectionUseCase
import com.example.android.core.result.Event
import com.example.android.core.result.Result
import com.example.android.core.util.PortSweeper
import com.example.android.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HostSearchViewModel @Inject constructor(
    private val checkHostConnectionUseCase: CheckHostConnectionUseCase,
    private val addHostUseCase: AddHostUseCase,
    context: Context
) : ViewModel() {

    private val portSweeper = PortSweeper(context)

    private val _hostConnected = MutableLiveData<Event<Boolean>>()
    val hostConnected: LiveData<Event<Boolean>> = _hostConnected

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _sweepResult = MutableLiveData<Result<List<HostInfo>>>()
    val sweepResult: LiveData<Result<List<HostInfo>>> = _sweepResult

    init {
        sweep()
    }

    fun sweep() {
        viewModelScope.launch {
            _sweepResult.value = portSweeper.sweep()
        }
    }

    fun checkHostConnection(host: HostInfo) {
        viewModelScope.launch {
            val result = checkHostConnectionUseCase(host)
            if (result is Result.Success) addHost(result.data)
            else if (result is Result.Error) {
                _errorMessage.value = Event(result.exception.message ?: "Error")
            }
        }
    }

    private suspend fun addHost(host: HostInfo) {
        val result = addHostUseCase(host)
        if (result is Result.Success) _hostConnected.postValue(Event(result.data))
        else if (result is Result.Error) {
            _errorMessage.value = Event(result.exception.message ?: "Error")
        }
    }
}
