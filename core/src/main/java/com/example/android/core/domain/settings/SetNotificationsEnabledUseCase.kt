package com.example.android.core.domain.settings

import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(isNotificationsEnabled: Boolean) {
        preferenceStorage.isNotificationsEnabled = isNotificationsEnabled
    }
}