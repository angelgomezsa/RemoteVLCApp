package com.example.android.remotevlcapp.ui.hosts.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.remotevlcapp.domain.host.DeleteHostUseCase
import com.example.android.remotevlcapp.domain.host.GetCurrentHostUseCase
import com.example.android.remotevlcapp.domain.host.LoadAllHostsUseCase
import com.example.android.remotevlcapp.domain.host.SwitchHostUseCase
import com.example.android.remotevlcapp.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HostListViewModel @Inject constructor(
    loadAllHostsUseCase: LoadAllHostsUseCase,
    private val getCurrentHostUseCase: GetCurrentHostUseCase,
    private val deleteHostUseCase: DeleteHostUseCase,
    private val switchHostUseCase: SwitchHostUseCase
) : ViewModel() {

    val hosts: LiveData<List<HostInfo>> = loadAllHostsUseCase.execute()

    val currentHostId: Int
        get() = getCurrentHostUseCase.execute()

    fun deleteHost(host: HostInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteHostUseCase(host)
        }
    }

    fun switchHost(host: HostInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            switchHostUseCase(host)
        }
    }
}
