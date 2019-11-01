package com.example.android.remotevlcapp.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

/**
 * Module used to define the connection between the framework's [ViewModelProvider.Factory] and
 * our own implementation: [ViewModelFactory].
 */
@Module
@Suppress("UNUSED")
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory):
            ViewModelProvider.Factory

}