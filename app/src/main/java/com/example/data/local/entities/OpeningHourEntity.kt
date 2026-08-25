package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opening_hours")
data class OpeningHourEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long,
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val dayName: String,
    val openTime: String = "08:00",
    val closeTime: String = "18:00",
    val isClosed: Boolean = false
)
