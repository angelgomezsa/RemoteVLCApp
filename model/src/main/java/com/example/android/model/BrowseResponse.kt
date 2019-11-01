package com.example.android.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrowseResponse(

    @Json(name = "element")
    val files: List<FileInfo>
)