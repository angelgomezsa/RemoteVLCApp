package com.example.android.core.data

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.android.core.result.Result
import com.example.android.core.service.NotificationService
import com.example.android.model.FileInfo
import com.example.android.model.HostInfo
import com.example.android.model.Status
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class MediaServer @Inject constructor(
    private val context: Context,
    private val db: AppDatabase,
    private val preferenceStorage: PreferenceStorage,
    private val vlcConnectionService: VlcConnectionService,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    companion object {
        private const val STATUS_CHECK_INTERVAL = 2000L
    }

    private var _currentHost = MutableLiveData<HostInfo>()
    val currentHost: LiveData<HostInfo> = _currentHost

    private var isNotificationServiceRunning = false

    val playerStatus: LiveData<Result<Status>> = liveData {
        // note that `while(true)` is fine because the `delay()` below will cooperate in
        // cancellation if LiveData is not actively observed anymore
        while (true) {
            try {
                val status = vlcConnectionService.getStatus()
                emit(Result.Success(status))
                if (!isNotificationServiceRunning && preferenceStorage.isNotificationsEnabled) {
                    startNotificationService()
                }
            } catch (e: Exception) {
                Timber.d(e)
                emit(Result.Error(e))
            }
            delay(STATUS_CHECK_INTERVAL)
        }
    }

    init {
        CoroutineScope(ioDispatcher).launch {
            val hostId = preferenceStorage.currentHostId
            val host = db.hostDao().getHostById(hostId) ?: return@launch
            switchHost(host)
        }
    }

    fun switchHost(host: HostInfo) {
        vlcConnectionService.switchHost(host)
        preferenceStorage.currentHostId = host.id
        _currentHost.postValue(host)
    }

    suspend fun browse(uri: String): List<FileInfo> {
        return vlcConnectionService.browse(uri)
    }

    suspend fun openFile(uri: String) {
        vlcConnectionService.openFile(uri)
    }

    suspend fun sendCommand(command: String, value: String? = null) {
        vlcConnectionService.sendCommand(command, value)
    }

    private fun startNotificationService() {
        context.startService(Intent(context, NotificationService::class.java))
    }

    fun setNotificationServerRunning(value: Boolean) {
        isNotificationServiceRunning = value
    }
}