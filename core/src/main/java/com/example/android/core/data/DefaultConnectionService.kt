package com.example.android.core.data

import com.example.android.model.FileInfo
import com.example.android.model.HostInfo
import com.example.android.model.Status
import com.example.android.model.adapter.StatusAdapter
import com.squareup.moshi.Moshi
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

interface VlcConnectionService {
    fun switchHost(host: HostInfo)
    suspend fun getStatus(): Status
    suspend fun browse(uri: String): List<FileInfo>
    suspend fun openFile(uri: String)
    suspend fun sendCommand(command: String, value: String? = null)
}

class DefaultConnectionService : VlcConnectionService {

    private lateinit var vlcService: VLCService

    override fun switchHost(host: HostInfo) {
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

        val moshi = Moshi.Builder().add(StatusAdapter()).build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .baseUrl("http://${host.address}:${host.port}/requests/")
            .build()

        vlcService = retrofit.create(VLCService::class.java)
    }

    override suspend fun getStatus(): Status {
        return vlcService.getStatus()
    }

    override suspend fun browse(uri: String): List<FileInfo> {
        return vlcService.browse(uri).files
    }

    override suspend fun openFile(uri: String) {
        vlcService.openFile(uri)
    }

    override suspend fun sendCommand(command: String, value: String?) {
        try {
            if (value == null) vlcService.sendCommand(command)
            else vlcService.sendCommand(command, value)
        } catch (e: Exception) {
            Timber.d("SendCommand exception: ${e.message}")
        }
    }
}