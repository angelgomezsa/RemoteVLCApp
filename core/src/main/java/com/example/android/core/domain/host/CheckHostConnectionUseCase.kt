package com.example.android.core.domain.host

import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException
import java.net.HttpURLConnection
import javax.inject.Inject

class CheckHostConnectionUseCase @Inject constructor() : UseCase<HostInfo, HostInfo>() {

    override suspend fun execute(parameters: HostInfo): HostInfo {
        return withContext(Dispatchers.IO) {
            val interceptor = Interceptor { chain ->
                var request = chain.request()
                request = request.newBuilder()
                    .header("Authorization", Credentials.basic("", parameters.password))
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val request = Request.Builder()
                .url("http://${parameters.address}:${parameters.port}/requests/status.json")
                .build()

            val response = client.newCall(request).execute()

            return@withContext when (response.code()) {
                HttpURLConnection.HTTP_OK -> parameters
                HttpURLConnection.HTTP_UNAUTHORIZED -> throw AuthenticationException(
                    "Wrong password"
                )
                else -> throw ConnectException("Couldn't connect to host")
            }
        }
    }
}

class AuthenticationException(message: String) : Exception(message)