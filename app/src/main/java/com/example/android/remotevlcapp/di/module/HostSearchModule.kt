package com.example.android.remotevlcapp.di.module

import androidx.lifecycle.ViewModel
import com.example.android.remotevlcapp.di.ViewModelKey
import com.example.android.remotevlcapp.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.hosts.search.HostSearchFragment
import com.example.android.remotevlcapp.ui.hosts.search.HostSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [HostSearchFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class HostSearchModule {

    /**
     * Generates an [AndroidInjector] for the [HostSearchFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeHostSearchFragment(): HostSearchFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [HostSearchViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(HostSearchViewModel::class)
    abstract fun bindHostSearchViewModel(viewModel: HostSearchViewModel): ViewModel
}
