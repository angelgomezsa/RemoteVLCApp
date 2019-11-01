package com.example.android.remotevlcapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "host")
@JsonClass(generateAdapter = true)
data class HostInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val address: String,
    val port: String,
    var password: String,
    var name: String
)