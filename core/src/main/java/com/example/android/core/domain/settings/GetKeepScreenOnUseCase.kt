package com.example.android.core.domain.settings

import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class GetKeepScreenOnUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {
    fun execute(): Boolean = preferenceStorage.keepScreenOn
}