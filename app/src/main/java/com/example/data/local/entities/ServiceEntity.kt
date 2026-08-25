package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long,
    val name: String,
    val category: String = "General",
    val description: String = "",
    val priceZar: Double, // in South African Rand (ZAR)
    val durationMinutes: Int = 30
)
