package com.example.android.remotevlcapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Status constructor(

    @Json(name = "fullscreen")
    val _fullscreen: Any,
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
) {
    // Json can show the value as a number (0 or 1) or as a boolean
    val fullscreen: Boolean
        get() {
            return when (_fullscreen) {
                is Boolean -> _fullscreen
                is Number -> _fullscreen == 1
                else -> throw IllegalArgumentException("Unknown value for fullscreen: $_fullscreen")
            }
        }

}