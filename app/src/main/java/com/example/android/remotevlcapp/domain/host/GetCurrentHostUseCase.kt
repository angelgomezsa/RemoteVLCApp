package com.example.android.remotevlcapp.domain.host

import com.example.android.remotevlcapp.data.PreferenceStorage
import javax.inject.Inject

class GetCurrentHostUseCase @Inject constructor(private val preferenceStorage: PreferenceStorage) {
    fun execute(): Int = preferenceStorage.currentHostId
}