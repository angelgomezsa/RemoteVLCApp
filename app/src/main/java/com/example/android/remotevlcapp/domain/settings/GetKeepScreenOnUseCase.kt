package com.example.android.remotevlcapp.domain.settings

import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class GetKeepScreenOnUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {
    fun execute(): Boolean = preferenceStorage.keepScreenOn
}