package com.example.plantmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Query("SELECT * FROM session WHERE id = 1 LIMIT 1")
    suspend fun getSessionNow(): Session?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: Session)

    @Query("DELETE FROM session WHERE id = 1")
    suspend fun clear()
}

