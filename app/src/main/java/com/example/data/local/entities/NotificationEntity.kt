package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String = "APPOINTMENT", // APPOINTMENT, SYSTEM, APPROVAL
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
