package com.example.android.remotevlcapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Information(

    val category: Category?
)