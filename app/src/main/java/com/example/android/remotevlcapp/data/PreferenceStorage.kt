package com.example.android.remotevlcapp.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.remotevlcapp.model.Theme
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Singleton
class PreferenceStorage @Inject constructor(context: Context) {

    companion object {
        const val PREFS_NAME = "vlcremote"
        const val PREF_CURRENT_HOST_ID = "pref_current_host_id"
        const val PREF_THEME = "pref_theme"
        const val PREF_NOTIFICATIONS_ENABLED = "pref_notifications_enabled"
        const val PREF_KEEP_SCREEN_ON = "pref_keep_screen_on"
    }

    private val changeListener = OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_THEME -> _observableSelectedTheme.value = selectedTheme
        }
    }

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME, MODE_PRIVATE
    ).apply {
        registerOnSharedPreferenceChangeListener(changeListener)
    }


    private val _observableSelectedTheme = MutableLiveData<String>()
    val observableSelectedTheme: LiveData<String> = _observableSelectedTheme

    var currentHostId by IntPreference(prefs, PREF_CURRENT_HOST_ID, -1)
    var selectedTheme by StringPreference(prefs, PREF_THEME, Theme.LIGHT)
    var isNotificationsEnabled by BooleanPreference(prefs, PREF_NOTIFICATIONS_ENABLED, false)
    var keepScreenOn by BooleanPreference(prefs, PREF_KEEP_SCREEN_ON, false)
}

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit { putString(name, value) }
    }
}

class IntPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit { putInt(name, value) }
    }
}

class BooleanPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit { putBoolean(name, value) }
    }
}