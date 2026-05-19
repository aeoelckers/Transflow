package com.transflow.adas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val eventType: String, // ej. "COLLISION_WARNING", "LANE_DEPARTURE"
    val speedKmh: Float,
    val description: String
)
