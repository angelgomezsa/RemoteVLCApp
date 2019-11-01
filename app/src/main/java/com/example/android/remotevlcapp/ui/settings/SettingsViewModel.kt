package com.example.android.remotevlcapp.ui.settings

import androidx.lifecycle.ViewModel
import com.example.android.remotevlcapp.domain.settings.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val setThemeUseCase: SetThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val getNotificationsEnabledUseCase: GetNotificationsEnabledUseCase,
    private val setEnableNotificationsUseCase: SetNotificationsEnabledUseCase,
    private val getKeepScreenOnUseCase: GetKeepScreenOnUseCase,
    private val setKeepScreenOnUseCase: SetKeepScreenOnUseCase
) : ViewModel() {

    val isNotificationsEnabled: Boolean
        get() = getNotificationsEnabledUseCase.execute()

    val keepScreenOn: Boolean
        get() = getKeepScreenOnUseCase.execute()

    val theme: String
        get() = getThemeUseCase.execute()

    fun setTheme(theme: String) {
        setThemeUseCase.execute(theme)
    }

    fun setNotificationEnabled(isEnabled: Boolean) {
        setEnableNotificationsUseCase.execute(isEnabled)
        // Cancel current notifications and stop the NotificationSerice
        // If notifications are disabled
//        if (!isEnabled) {
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.cancelAll()
//            context.stopService(Intent(context, NotificationService::class.java))
//        }
    }

    fun setKeepScreenOn(isEnabled: Boolean) {
        setKeepScreenOnUseCase.execute(isEnabled)
    }
}
