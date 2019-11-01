package com.example.android.remotevlcapp.domain.host

import com.example.android.remotevlcapp.data.AppDatabase
import com.example.android.remotevlcapp.domain.UseCase
import com.example.android.remotevlcapp.model.HostInfo
import javax.inject.Inject

class AddHostUseCase @Inject constructor(
    private val db: AppDatabase
) : UseCase<HostInfo, Boolean>() {

    override suspend fun execute(parameters: HostInfo): Boolean {
        db.hostDao().insertHosts(parameters)
        return true
    }
}