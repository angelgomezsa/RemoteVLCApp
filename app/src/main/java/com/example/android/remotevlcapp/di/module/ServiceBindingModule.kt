package com.example.android.remotevlcapp.di.module

import com.example.android.remotevlcapp.di.scope.ServiceScoped
import com.example.android.remotevlcapp.service.IntentActionService
import com.example.android.remotevlcapp.service.NotificationService
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