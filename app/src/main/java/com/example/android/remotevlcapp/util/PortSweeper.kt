package com.example.android.remotevlcapp.util

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class PortSweeper(private val context: Context) {
    private val port = "8080"
    private val addressList = mutableListOf<ByteArray>()
    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .build()

    private val reachableHosts = mutableListOf<HostInfo>()

    private fun toByteArray(i: Int): ByteArray {
        val i4 = i shr 24 and 0xFF
        val i3 = i shr 16 and 0xFF
        val i2 = i shr 8 and 0xFF
        val i1 = i and 0xFF
        return byteArrayOf(i1.toByte(), i2.toByte(), i3.toByte(), i4.toByte())
    }

    private fun getConnectionInfo(): WifiInfo? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo

        return when (info.supplicantState) {
            SupplicantState.COMPLETED -> info
            else -> null
        }
    }

    private fun getIpAddress(): ByteArray? {
        val info = getConnectionInfo() ?: return null
        return toByteArray(info.ipAddress)
    }

    suspend fun sweep(): Result<List<HostInfo>> {
        addressList.clear()
        reachableHosts.clear()
        val interfaceAddress = getIpAddress()
            ?: return Result.Error(ConnectException("Please enable a network connection before searching for media centers."))
        // Scan outwards from the interface IP address for best results
        // with DHCP servers that allocate addresses sequentially.
        val start = interfaceAddress[interfaceAddress.size - 1]
        for (delta in 1..127) {
            var sign = -1
            while (sign <= 1) {
                val b = (256 + start.toInt() + sign * delta) % 256
                if (b != 0) {
                    val ipAddress = interfaceAddress.clone()
                    ipAddress[ipAddress.size - 1] = b.toByte()
                    addressList.add(ipAddress)
                }
                sign += 2
            }
        }
        return withContext(Dispatchers.IO) {
            val jobs = (0 until addressList.size).map { index ->
                val ipAddress = addressList[index]
                async {
                    val hostAddress = InetAddress.getByAddress(ipAddress).hostAddress
                    checkHost(hostAddress)
                }
            }

            jobs.awaitAll()
            if (reachableHosts.isNotEmpty()) {
                return@withContext Result.Success(reachableHosts.toList())
            } else {
                return@withContext Result.Error(ConnectException("Couldn't find any media centers on your local network."))
            }
        }
    }

    private fun checkHost(hostAddress: String) {
        val request = Request.Builder()
            .url("http://$hostAddress:$port/requests/status.json")
            .build()
        try {
            val response = client.newCall(request).execute()
            when (response.code()) {
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_FORBIDDEN,
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    Timber.d("Host reachable: $hostAddress")
                    val host = HostInfo(0, hostAddress, port, "", "Media Center")
                    reachableHosts.add(host)
                }
            }
        } catch (e: IOException) {
            Timber.d("$hostAddress is unreachable")
        }
    }
}