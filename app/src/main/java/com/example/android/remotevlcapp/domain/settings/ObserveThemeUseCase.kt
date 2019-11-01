package com.example.android.remotevlcapp.domain.settings

import androidx.lifecycle.LiveData
import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(): LiveData<String> {
        return preferenceStorage.observableSelectedTheme
    }
}