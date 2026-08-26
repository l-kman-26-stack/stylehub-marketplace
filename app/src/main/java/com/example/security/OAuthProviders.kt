package com.example.security

/**
 * Enterprise OAuth Provider Adapter Architecture.
 *
 * Supported Providers:
 * 1. Google OAuth (Google Identity Services / CredentialManager & Firebase Auth)
 * 2. Apple Sign-In (Apple Identity Token with cryptographic ES256 verification)
 * 3. Facebook / Meta Login (Meta Graph API User Access Token exchange)
 *
 * Architectural Note on Instagram Authentication:
 * Direct Instagram web login APIs have been deprecated by Meta in favor of the Meta Graph API /
 * Facebook Login with Instagram Graph API permissions. Applications requiring Meta ecosystem identity
 * must use the standard Facebook/Meta Login SDK flow to ensure compliant token lifecycle management.
 */
sealed class OAuthProvider(val id: String, val displayName: String, val iconRes: String) {
    object Google : OAuthProvider("google.com", "Google", "ic_google")
    object Apple : OAuthProvider("apple.com", "Apple", "ic_apple")
    object Facebook : OAuthProvider("facebook.com", "Facebook (Meta)", "ic_facebook")
}

data class OAuthUserPayload(
    val provider: OAuthProvider,
    val providerUid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val idToken: String? = null,
    val isEmailVerified: Boolean = true
)

sealed class AuthResult {
    data class Success(val user: com.example.data.local.entities.UserEntity, val sessionToken: String) : AuthResult()
    data class RequiresMfa(val userId: Long, val email: String, val maskedPhone: String?) : AuthResult()
    data class AccountLocked(val remainingMinutes: Long) : AuthResult()
    data class Error(val message: String, val code: String? = null) : AuthResult()
}

data class AuthSessionToken(
    val sessionId: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long = 3600L,
    val issuedAt: Long = System.currentTimeMillis()
)
