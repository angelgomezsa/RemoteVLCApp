package com.example.android.remotevlcapp.di.module

import androidx.lifecycle.ViewModel
import com.example.android.remotevlcapp.di.ViewModelKey
import com.example.android.remotevlcapp.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.remote.RemoteFragment
import com.example.android.remotevlcapp.ui.remote.RemoteViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [RemoteFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class RemoteModule {

    /**
     * Generates an [AndroidInjector] for the [RemoteFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeRemoteFragment(): RemoteFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [RemoteViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(RemoteViewModel::class)
    abstract fun bindRemoteFragmentViewModel(viewModel: RemoteViewModel): ViewModel
}