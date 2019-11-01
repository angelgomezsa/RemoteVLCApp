package com.example.android.remotevlcapp.domain.settings

import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(theme: String) {
        preferenceStorage.selectedTheme = theme
    }
}