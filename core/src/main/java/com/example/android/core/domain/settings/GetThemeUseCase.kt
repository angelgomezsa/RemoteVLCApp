package com.example.android.core.domain.settings

import com.example.android.core.data.PreferenceStorage
import com.example.android.model.Theme
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(): String {
        preferenceStorage.selectedTheme?.let { return it }
        // Theme is not set, return dark theme as the default theme
        return Theme.DARK
    }
}