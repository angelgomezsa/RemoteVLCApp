package com.example.android.remotevlcapp.di.module

import androidx.lifecycle.ViewModel
import com.example.android.remotevlcapp.di.ViewModelKey
import com.example.android.remotevlcapp.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.hosts.configuration.HostConfigurationFragment
import com.example.android.remotevlcapp.ui.hosts.configuration.HostConfigurationViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [HostConfigurationFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class HostConfigurationModule {

    /**
     * Generates an [AndroidInjector] for the [HostConfigurationFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeHostConfigurationFragment(): HostConfigurationFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [HostConfigurationViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(HostConfigurationViewModel::class)
    abstract fun bindHostConfigurationViewModel(viewModel: HostConfigurationViewModel): ViewModel
}
