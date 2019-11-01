package com.example.android.remotevlcapp.util

import android.os.Parcel
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.os.ParcelCompat
import com.example.android.remotevlcapp.model.Theme

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel. */
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)

/** Update theme */
fun AppCompatActivity.updateForTheme(theme: String) = when (theme) {
    Theme.DARK -> delegate.localNightMode = MODE_NIGHT_YES
    Theme.LIGHT -> delegate.localNightMode = MODE_NIGHT_NO
    else -> throw IllegalArgumentException("Unknown theme: $theme")
}


