package com.example.android.remotevlcapp.data

import com.example.android.remotevlcapp.model.BrowseResponse
import com.example.android.remotevlcapp.model.Status
import retrofit2.http.GET
import retrofit2.http.Query

interface VLCService {

    @GET("status.json")
    suspend fun getStatus(): Status

    @GET("browse.json")
    suspend fun browse(@Query("uri") uri: String): BrowseResponse

    @GET("status.json?command=in_play")
    suspend fun openFile(@Query("input") mrl: String): Status

    @GET("status.json")
    suspend fun sendCommand(@Query("command") command: String): Status

    @GET("status.json")
    suspend fun sendCommand(
        @Query("command") command: String,
        @Query("val") value: String
    ): Status

}