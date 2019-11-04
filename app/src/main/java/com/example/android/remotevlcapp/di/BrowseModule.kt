package com.example.android.remotevlcapp.di

import androidx.lifecycle.ViewModel
import com.example.android.core.di.ViewModelKey
import com.example.android.core.di.scope.FragmentScoped
import com.example.android.remotevlcapp.ui.browse.BrowseFragment
import com.example.android.remotevlcapp.ui.browse.BrowseViewModel
import com.example.android.remotevlcapp.widget.NowPlayingFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [BrowseFragment] are defined.
 */

@Module
@Suppress("UNUSED")
abstract class BrowseModule {

    /**
     * Generates an [AndroidInjector] for the [BrowseFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeBrowseFragment(): BrowseFragment

    /**
     * Generates an [AndroidInjector] for the [NowPlayingFragment] as a Dagger subcomponent of the
     * [MainModule].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun contributeNowPlayingFragment(): NowPlayingFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [BrowseViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(BrowseViewModel::class)
    abstract fun bindBrowseFragmentViewModel(viewModel: BrowseViewModel): ViewModel
}