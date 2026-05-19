package com.transflow.adas.data

class EventRepository(private val eventDao: EventDao) {

    suspend fun recordEvent(eventType: String, speedKmh: Float, description: String) {
        val event = EventEntity(
            timestamp = System.currentTimeMillis(),
            eventType = eventType,
            speedKmh = speedKmh,
            description = description
        )
        eventDao.insert(event)
    }

    suspend fun getRecentEvents() = eventDao.getLastEvents()
}
