package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val id: String, // UUID
    val userId: Long,
    val deviceName: String,
    val deviceType: String, // "MOBILE", "DESKTOP", "TABLET"
    val osVersion: String,
    val ipAddress: String,
    val location: String, // e.g. "Sandton, Johannesburg"
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L), // 30 days
    val isCurrent: Boolean = false,
    val isRevoked: Boolean = false
)
