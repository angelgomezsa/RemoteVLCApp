package com.example.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    val filename: String
)