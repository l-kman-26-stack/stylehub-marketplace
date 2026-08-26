package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_audit_logs")
data class SecurityAuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long?,
    val userEmail: String?,
    val eventType: String, // SIGN_IN_SUCCESS, SIGN_IN_FAILED, SIGN_OUT, SIGN_UP, PASSWORD_CHANGED, MFA_ENABLED, MFA_DISABLED, RECOVERY_CODE_USED, SESSION_REVOKED, LOGOUT_ALL_DEVICES, ACCOUNT_LOCKED, ROLE_CHANGED
    val details: String,
    val ipAddress: String = "102.165.34.12",
    val device: String = "Android Client",
    val severity: String = "INFO", // "INFO", "WARNING", "CRITICAL"
    val timestamp: Long = System.currentTimeMillis()
)
