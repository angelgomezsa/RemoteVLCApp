package com.example.android.core.domain.settings

import androidx.lifecycle.LiveData
import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) {

    fun execute(): LiveData<String> {
        return preferenceStorage.observableSelectedTheme
    }
}