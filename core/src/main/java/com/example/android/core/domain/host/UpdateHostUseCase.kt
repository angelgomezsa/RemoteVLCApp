package com.example.android.core.domain.host

import com.example.android.core.data.AppDatabase
import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import javax.inject.Inject

class UpdateHostUseCase @Inject constructor(private val db: AppDatabase) :
    UseCase<HostInfo, Boolean>() {
    override suspend fun execute(parameters: HostInfo): Boolean {
        db.hostDao().updateHosts(parameters)
        return true
    }
}