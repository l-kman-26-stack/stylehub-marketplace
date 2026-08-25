package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val customerPhone: String,
    val businessId: Long,
    val businessName: String,
    val serviceId: Long,
    val serviceName: String,
    val teamMemberId: Long = 0,
    val teamMemberName: String = "Any Available Professional",
    val appointmentDate: String, // e.g. "2026-08-26" or "Wed, 26 Aug"
    val appointmentTime: String, // e.g. "10:30"
    val notes: String = "",
    val priceZar: Double,
    val status: String = AppointmentStatus.CONFIRMED.name,
    val hasReviewed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
