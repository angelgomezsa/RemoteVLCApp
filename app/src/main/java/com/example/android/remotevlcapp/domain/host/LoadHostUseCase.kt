package com.example.android.remotevlcapp.domain.host

import com.example.android.remotevlcapp.data.AppDatabase
import com.example.android.remotevlcapp.domain.UseCase
import com.example.android.remotevlcapp.model.HostInfo
import javax.inject.Inject

class LoadHostUseCase @Inject constructor(
    private val db: AppDatabase
) : UseCase<Int, HostInfo?>() {

    override suspend fun execute(parameters: Int): HostInfo? {
        return db.hostDao().getHostById(parameters)
    }
}