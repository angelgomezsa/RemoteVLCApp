package com.example.android.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusJson(
    val fullscreen: Any,
    val audiodelay: Int,
    val currentplid: Int,
    val time: Int,
    val volume: Int,
    val length: Int,
    val random: Boolean,
    val rate: Int,
    val state: String,
    val loop: Boolean,
    val position: Float,
    val repeat: Boolean,
    val subtitledelay: Int,
    val information: Information?
)

@JsonClass(generateAdapter = true)
data class Category(
    val meta: Meta?
)

@JsonClass(generateAdapter = true)
data class Information(
    val category: Category?
)

@JsonClass(generateAdapter = true)
data class Meta(
    val filename: String
)