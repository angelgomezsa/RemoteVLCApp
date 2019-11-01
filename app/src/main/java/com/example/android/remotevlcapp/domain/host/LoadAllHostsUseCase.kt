package com.example.android.remotevlcapp.domain.host

import androidx.lifecycle.LiveData
import com.example.android.remotevlcapp.data.AppDatabase
import com.example.android.remotevlcapp.model.HostInfo
import javax.inject.Inject

class LoadAllHostsUseCase @Inject constructor(private val db: AppDatabase) {
    fun execute(): LiveData<List<HostInfo>> = db.hostDao().loadAllHosts()
}