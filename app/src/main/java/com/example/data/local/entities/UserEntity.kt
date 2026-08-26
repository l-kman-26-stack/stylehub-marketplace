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
    val createdAt: Long = System.currentTimeMillis(),

    // --- Enterprise Authentication & Security Fields ---
    val passwordHash: String = "",
    val salt: String = "",
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val isMfaEnabled: Boolean = false,
    val mfaSecret: String? = null,
    val mfaRecoveryCodes: String = "", // Comma-separated recovery codes
    val twoFactorMethod: String = "AUTHENTICATOR_APP", // AUTHENTICATOR_APP, SMS_OTP
    val authProvider: String = "PASSWORD", // PASSWORD, GOOGLE, APPLE, FACEBOOK
    val providerUid: String? = null,
    val failedLoginAttempts: Int = 0,
    val lockoutUntil: Long? = null,
    val forceMfa: Boolean = false,
    val lastLoginAt: Long? = null,
    val lastPasswordChangeAt: Long? = null
)
