package com.example.android.remotevlcapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.android.core.data.PreferenceStorage.Companion.PREFS_NAME
import com.example.android.core.data.PreferenceStorage.Companion.PREF_THEME
import com.example.android.model.Theme
import com.example.android.remotevlcapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

/**
 * Initialization of libraries.
 */
class MainApplication : DaggerApplication() {

    override fun onCreate() {
        setDayNightTheme()
        // Timber for logging
        Timber.plant(Timber.DebugTree())
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    // Sets the DayNight theme here before MainActivity gets created
    // to avoid recreating the activity
    private fun setDayNightTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(PREF_THEME, Theme.DARK)?.let {
            return when (it) {
                Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> throw IllegalArgumentException("Unkown theme: $it")
            }
        }
        return AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}