package com.example.android.remotevlcapp.domain.settings

import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(isNotificationsEnabled: Boolean) {
        preferenceStorage.isNotificationsEnabled = isNotificationsEnabled
    }
}