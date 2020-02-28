package com.example.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Status constructor(
    val fullscreen: Boolean,
    val time: Int,
    val volume: Int,
    val length: Int,
    val random: Boolean,
    val state: String,
    val loop: Boolean,
    val position: Float,
    val repeat: Boolean,
    val filename: String?
)