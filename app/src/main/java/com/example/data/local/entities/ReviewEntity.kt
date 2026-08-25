package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appointmentId: Long = 0,
    val businessId: Long,
    val customerId: Long,
    val customerName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val serviceName: String = "General Grooming",
    val isFlagged: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
