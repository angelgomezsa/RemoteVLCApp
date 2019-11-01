package com.example.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileInfo(

    val type: String,
    val path: String,
    val name: String,
    val uri: String,
    val size: Long?,
    val modification_time: Int?

)