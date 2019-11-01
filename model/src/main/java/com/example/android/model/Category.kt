package com.example.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    val meta: Meta?
)