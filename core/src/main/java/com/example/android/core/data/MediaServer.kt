package com.example.android.core.data

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.android.core.result.Result
import com.example.android.core.service.NotificationService
import com.example.android.core.service.NotificationService.Companion.EXTRAS_FILENAME
import com.example.android.core.service.NotificationService.Companion.EXTRAS_STATE
import com.example.android.model.FileInfo
import com.example.android.model.HostInfo
import com.example.android.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaServer @Inject constructor(
    private val context: Context,
    private val db: AppDatabase,
    private val preferenceStorage: PreferenceStorage
) {

    companion object {
        private const val STATUS_CHECK_INTERVAL = 2000L
    }

    private var _currentHost = MutableLiveData<HostInfo>()
    val currentHost: LiveData<HostInfo> = _currentHost

    private lateinit var vlcService: VLCService
    var isNotificationServiceRunning = false

    val playerStatus: LiveData<Result<Status>> = liveData {
        // note that `while(true)` is fine because the `delay()` below will cooperate in
        // cancellation if LiveData is not actively observed anymore
        while (true) {
            try {
                val status = vlcService.getStatus()
                emit(Result.Success(status))
//                Timber.d("checking status")

                if (!isNotificationServiceRunning && preferenceStorage.isNotificationsEnabled) {
                    val state = status.state
                    val filename = status.information?.category?.meta?.filename ?: ""
                    startNotificationService(state, filename)
                }
            } catch (e: Exception) {
                Timber.d(e)
                emit(Result.Error(e))
            }
            delay(STATUS_CHECK_INTERVAL)
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val hostId = preferenceStorage.currentHostId
            val host = db.hostDao().getHostById(hostId) ?: return@launch
            switchHost(host)
        }
    }

    fun switchHost(host: HostInfo) {
        Timber.d("Switching host to: ${host.name}")

        val interceptor = Interceptor { chain ->
            var request = chain.request()
            request = request.newBuilder()
                .header("Authorization", Credentials.basic("", host.password))
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()


        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .baseUrl("http://${host.address}:${host.port}/requests/")
            .build()

        vlcService = retrofit.create(VLCService::class.java)
        preferenceStorage.currentHostId = host.id
        _currentHost.postValue(host)
    }

    suspend fun browse(uri: String): List<FileInfo> {
        return vlcService.browse(uri).files
    }

    suspend fun openFile(uri: String) {
        vlcService.openFile(uri)
    }

    suspend fun sendCommand(command: String, value: String? = null) {
        try {
            if (value == null) vlcService.sendCommand(command)
            else vlcService.sendCommand(command, value)
        } catch (e: Exception) {
            Timber.d("SendCommand exception: ${e.message}")
        }
    }

    private fun startNotificationService(state: String, filename: String) {
        if (filename.isBlank()) return
        val intent = Intent(context, NotificationService::class.java).apply {
            putExtra(EXTRAS_STATE, state)
            putExtra(EXTRAS_FILENAME, filename)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }
}