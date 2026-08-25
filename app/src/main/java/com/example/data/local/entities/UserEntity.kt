package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    BUSINESS_OWNER,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = UserRole.CUSTOMER.name,
    val avatarUrl: String = "",
    val city: String = "Johannesburg",
    val province: String = "Gauteng",
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
