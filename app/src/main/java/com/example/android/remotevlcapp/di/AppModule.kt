package com.example.android.remotevlcapp.di

import android.content.Context
import com.example.android.core.data.*
import com.example.android.remotevlcapp.MainApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(application: MainApplication): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun providePreferenceStorage(context: Context): PreferenceStorage {
        return PreferenceStorage(context)
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideVLCDataSource(): VLCDataSource = DefaultDataSource()

    @Singleton
    @Provides
    fun provideMediaServer(
        context: Context,
        appDatabase: AppDatabase,
        preferenceStorage: PreferenceStorage,
        vlcDataSource: VLCDataSource
    ): MediaServer {
        return MediaServer(context, appDatabase, preferenceStorage, vlcDataSource)
    }
}