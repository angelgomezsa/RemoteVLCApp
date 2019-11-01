package com.example.android.remotevlcapp.di.module

import android.content.Context
import androidx.room.Room
import com.example.android.remotevlcapp.MainApplication
import com.example.android.remotevlcapp.data.AppDatabase
import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.data.PreferenceStorage
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
        return application
    }

    @Singleton
    @Provides
    fun providePreferenceStorage(context: Context): PreferenceStorage {
        return PreferenceStorage(context)
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "remote-vlc-database"
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideMediaServer(
        context: Context,
        db: AppDatabase,
        preferenceStorage: PreferenceStorage
    ): MediaServer {
        return MediaServer(context, db, preferenceStorage)
    }
}