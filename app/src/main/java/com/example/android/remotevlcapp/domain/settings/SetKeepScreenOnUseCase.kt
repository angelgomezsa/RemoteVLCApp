package com.example.android.remotevlcapp.domain.settings

import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class SetKeepScreenOnUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {

    fun execute(keepScreenOn: Boolean) {
        preferenceStorage.keepScreenOn = keepScreenOn
    }
}