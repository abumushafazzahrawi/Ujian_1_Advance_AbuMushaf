package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: List<EventEntity>)

    @Query("SELECT * FROM event")
    suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT * FROM event WHERE type = :type")
    suspend fun getEventsByType(type: String): List<EventEntity>

    @Query("SELECT * FROM event WHERE isFavorite = 1")
    suspend fun getFavoriteEvents(): List<EventEntity>

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun getEventById(id: Int): EventEntity?

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE event SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
}
