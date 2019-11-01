package com.example.android.remotevlcapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.remotevlcapp.model.HostInfo

@Dao
interface HostDao {

    @Query("SELECT * FROM host")
    fun loadAllHosts(): LiveData<List<HostInfo>>

    @Query("SELECT * FROM host WHERE id = :id")
    fun getHostById(id: Int): HostInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHosts(vararg hosts: HostInfo)

    @Update
    fun updateHosts(vararg hosts: HostInfo)

    @Delete
    fun deleteHosts(vararg hosts: HostInfo)
}