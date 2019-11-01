package com.example.android.remotevlcapp.ui.settings

import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class ThemeSettingDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


}