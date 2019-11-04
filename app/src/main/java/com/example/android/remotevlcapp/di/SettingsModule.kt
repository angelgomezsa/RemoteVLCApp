package com.example.android.remotevlcapp.di

import androidx.lifecycle.ViewModel
import com.example.android.core.di.ViewModelKey
import com.example.android.core.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.settings.SettingsFragment
import com.example.android.remotevlcapp.ui.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [SettingsFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class SettingsModule {

    /**
     * Generates an [AndroidInjector] for the [SettingsFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SettingsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel
}