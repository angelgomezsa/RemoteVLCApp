package com.example.android.model.adapter

import com.example.android.model.Status
import com.example.android.model.StatusJson
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException

class StatusAdapter {

    @FromJson
    fun fromJson(statusJson: StatusJson): Status {
        // Json can show the value as a number (0 or 1) or as a boolean
        val fullscreen = when (statusJson.fullscreen) {
            is Boolean -> statusJson.fullscreen
            is Number -> statusJson.fullscreen == 1
            else -> throw JsonDataException("Unknown value for fullscreen: ${statusJson.fullscreen}")
        }
        val filename = statusJson.information?.category?.meta?.filename

        return Status(
            fullscreen, statusJson.time,
            statusJson.volume, statusJson.length,
            statusJson.random, statusJson.state,
            statusJson.loop, statusJson.position,
            statusJson.repeat, filename
        )
    }
}