package com.example.android.core.domain.settings

import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(theme: String) {
        preferenceStorage.selectedTheme = theme
    }
}