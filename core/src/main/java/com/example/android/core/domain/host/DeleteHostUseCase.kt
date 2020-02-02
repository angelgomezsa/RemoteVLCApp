package com.example.android.core.domain.host

import com.example.android.core.data.AppDatabase
import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteHostUseCase @Inject constructor(private val db: AppDatabase) :
    UseCase<HostInfo, Boolean>() {

    override suspend fun execute(parameters: HostInfo): Boolean {
        withContext(Dispatchers.IO) {
            db.hostDao().deleteHosts(parameters)
        }
        return true
    }
}