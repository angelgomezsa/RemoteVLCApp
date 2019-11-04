package com.example.android.core.domain.host

import com.example.android.core.data.AppDatabase
import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import javax.inject.Inject

class LoadHostUseCase @Inject constructor(
    private val db: AppDatabase
) : UseCase<Int, HostInfo?>() {

    override suspend fun execute(parameters: Int): HostInfo? {
        return db.hostDao().getHostById(parameters)
    }
}