package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BusinessStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    SUSPENDED
}

enum class BusinessCategory(val displayName: String) {
    BARBER("Barbershop"),
    SALON("Hair Salon"),
    HAIRSTYLIST("Hairstylist"),
    BRAIDER("Braids & Locs"),
    GROOMING("Grooming Studio"),
    MOBILE_BARBER("Mobile Barber")
}

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerId: Long = 0,
    val name: String,
    val category: String, // from BusinessCategory
    val description: String,
    val phone: String,
    val email: String,
    val whatsapp: String,
    val website: String = "",
    val instagram: String = "",
    // Location in South Africa
    val streetAddress: String,
    val suburb: String,
    val city: String,
    val province: String,
    val postalCode: String,
    // Media & Visuals
    val coverDrawable: String = "hero_barber",
    val logoDrawable: String = "ic_app_logo",
    // Metrics & Badges
    val rating: Float = 5.0f,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val isFeatured: Boolean = false,
    val status: String = BusinessStatus.APPROVED.name,
    val profileViews: Int = 0,
    val minPriceZar: Double = 150.0,
    val createdAt: Long = System.currentTimeMillis()
)
