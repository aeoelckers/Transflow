package com.transflow.adas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY timestamp DESC LIMIT 50")
    suspend fun getLastEvents(): List<EventEntity>
    
    @Query("DELETE FROM events")
    suspend fun clearAll()
}
