package com.example.android.core.domain.settings

import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class SetKeepScreenOnUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {

    fun execute(keepScreenOn: Boolean) {
        preferenceStorage.keepScreenOn = keepScreenOn
    }
}