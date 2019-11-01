package com.example.android.remotevlcapp.ui.hosts.configuration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.remotevlcapp.domain.host.AddHostUseCase
import com.example.android.remotevlcapp.domain.host.CheckHostConnectionUseCase
import com.example.android.remotevlcapp.domain.host.UpdateHostUseCase
import com.example.android.remotevlcapp.event.Event
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HostConfigurationViewModel @Inject constructor(
    private val checkHostConnectionUseCase: CheckHostConnectionUseCase,
    private val addHostUseCase: AddHostUseCase,
    private val updateHostUseCase: UpdateHostUseCase
) : ViewModel() {

    private val _error = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = _error

    private val _hostEvent = MutableLiveData<Event<Boolean>>()
    val hostEvent: LiveData<Event<Boolean>> = _hostEvent

    fun checkHostConnection(host: HostInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = checkHostConnectionUseCase(host)
            when (result) {
                is Result.Success -> {
                    if (result.data.id == 0) {
                        addHost(result.data)
                    } else {
                        updateHost(result.data)
                    }
                }
                is Result.Error -> _error.postValue(Event(result.exception))
            }
        }
    }

    private suspend fun addHost(host: HostInfo) {
        val result = addHostUseCase(host)
        if (result is Result.Success) {
            _hostEvent.postValue(Event(result.data))
        }
    }

    private suspend fun updateHost(host: HostInfo) {
        val result = updateHostUseCase(host)
        if (result is Result.Success) {
            _hostEvent.postValue(Event(result.data))
        }
    }
}
