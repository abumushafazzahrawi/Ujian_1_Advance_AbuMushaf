package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey
    val id: Int = 0,
    val name: String,
    val summary: String,
    val description: String,
    val imageLogo: String,
    val mediaCover: String,
    val category: String,
    val ownerName: String,
    val cityName: String,
    val quota: Int,
    val registrants: Int,
    val beginTime: String,
    val endTime: String,
    val link: String,
    val type: String,
    var isFavorite: Boolean = false
)
