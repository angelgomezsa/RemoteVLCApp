package com.example.android.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.model.HostInfo

@Database(entities = [HostInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostDao(): HostDao

    companion object {
        private const val databaseName = "remote-vlc-database"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
        }
    }
}