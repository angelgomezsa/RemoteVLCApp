package com.example.android.core.domain.host

import com.example.android.core.data.PreferenceStorage
import javax.inject.Inject

class GetCurrentHostUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {
    fun execute(): Int = preferenceStorage.currentHostId
}