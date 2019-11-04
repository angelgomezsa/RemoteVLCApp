package com.example.android.remotevlcapp.di

import androidx.lifecycle.ViewModel
import com.example.android.core.di.ViewModelKey
import com.example.android.remotevlcapp.ui.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
abstract class MainActivityModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [MainActivityViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindViewModel(viewModel: MainActivityViewModel): ViewModel
}