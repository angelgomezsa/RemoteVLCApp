package com.example.android.remotevlcapp.di

import com.example.android.core.di.scope.ServiceScoped
import com.example.android.core.service.IntentActionService
import com.example.android.core.service.NotificationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("UNUSED")
abstract class ServiceBindingModule {

    @ServiceScoped
    @ContributesAndroidInjector
    abstract fun provideNotificationService(): NotificationService

    @ServiceScoped
    @ContributesAndroidInjector
    abstract fun provideIntentActionService(): IntentActionService

}