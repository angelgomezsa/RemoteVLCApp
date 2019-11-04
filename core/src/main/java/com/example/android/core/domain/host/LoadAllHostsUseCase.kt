package com.example.android.core.domain.host

import androidx.lifecycle.LiveData
import com.example.android.core.data.AppDatabase
import com.example.android.model.HostInfo
import javax.inject.Inject

class LoadAllHostsUseCase @Inject constructor(private val db: AppDatabase) {
    fun execute(): LiveData<List<HostInfo>> = db.hostDao().loadAllHosts()
}