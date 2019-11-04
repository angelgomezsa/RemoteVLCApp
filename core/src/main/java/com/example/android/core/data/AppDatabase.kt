package com.example.android.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.model.HostInfo

@Database(entities = [HostInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hostDao(): HostDao
}