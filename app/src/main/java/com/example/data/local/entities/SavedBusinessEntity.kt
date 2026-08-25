package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_businesses")
data class SavedBusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val businessId: Long,
    val savedAt: Long = System.currentTimeMillis()
)
