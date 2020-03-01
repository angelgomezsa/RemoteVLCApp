package com.example.android.remotevlcapp.util

import android.content.Context
import android.util.TypedValue
import java.math.RoundingMode
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun dpToPx(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
        .roundToInt()
}

fun formatTime(seconds: Int): String {
    return formatTime(seconds / 3600, seconds % 3600 / 60, seconds % 3600 % 60)
}

fun formatTime(hours: Int, minutes: Int, seconds: Int): String {
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%1d:%02d", minutes, seconds)
    }
}

fun formatFileSize(b: Long): String {
    var bytes = b.toFloat()
    val sizes = arrayOf("bytes", "KB", "MB", "GB")
    var index = 3
    for (i in sizes.indices) {
        val n = bytes / 1024
        if (n.toInt() == 0) {
            index = i
            break
        }
        bytes = n
    }
    bytes = bytes.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toFloat()
    return "$bytes ${sizes[index]}"
}

fun formatFileTime(seconds: Int): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = seconds * 1000L
    return formatter.format(calendar.time)
}

fun formatUri(uri: String): String {
    return URLDecoder.decode(uri, "UTF-8")
}

fun formatPathFromUri(uri: String): String {
    var formattedUri = uri.replace("file:///", "")
        .replace("//", "/")
    if (formattedUri.isBlank()) formattedUri = "/"
    return formattedUri
}

