package com.example.android.remotevlcapp.di.module

import androidx.lifecycle.ViewModel
import com.example.android.remotevlcapp.di.ViewModelKey
import com.example.android.remotevlcapp.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.hosts.list.HostListFragment
import com.example.android.remotevlcapp.ui.hosts.list.HostListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [HostListFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class HostListModule {

    /**
     * Generates an [AndroidInjector] for the [HostListFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeHostListFragment(): HostListFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [HostListViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(HostListViewModel::class)
    abstract fun bindHostListViewModel(viewModel: HostListViewModel): ViewModel
}