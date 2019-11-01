package com.example.android.remotevlcapp.domain.settings

import com.example.android.remotevlcapp.data.PreferenceStorage
import com.example.android.remotevlcapp.model.Theme
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