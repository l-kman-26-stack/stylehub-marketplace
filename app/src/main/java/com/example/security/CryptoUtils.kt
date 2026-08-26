package com.example.security

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

object CryptoUtils {

    private val secureRandom = SecureRandom()

    /**
     * Generates a cryptographically secure random salt encoded in Base64.
     */
    fun generateSalt(length: Int = 16): String {
        val saltBytes = ByteArray(length)
        secureRandom.nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    /**
     * Hashes a plain password using SHA-256 with salt and multiple iterations.
     */
    fun hashPassword(password: String, salt: String, iterations: Int = 10_000): String {
        val digest = MessageDigest.getInstance("SHA-256")
        var hash = (password + salt).toByteArray(StandardCharsets.UTF_8)
        for (i in 0 until iterations) {
            digest.reset()
            hash = digest.digest(hash)
        }
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    /**
     * Constant-time comparison to prevent timing attacks.
     */
    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        if (salt.isBlank() || expectedHash.isBlank()) return false
        val computedHash = hashPassword(password, salt)
        val a = computedHash.toByteArray(StandardCharsets.UTF_8)
        val b = expectedHash.toByteArray(StandardCharsets.UTF_8)
        return MessageDigest.isEqual(a, b)
    }
}

/**
 * Standard RFC 6238 Time-Based One-Time Password (TOTP) Implementation.
 */
object TotpManager {

    private const val TIME_STEP_SECONDS = 30L
    private const val DIGITS = 6
    private val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray()

    /**
     * Generates a random Base32 encoded TOTP secret (160 bits = 20 bytes).
     */
    fun generateSecretKey(): String {
        val bytes = ByteArray(20)
        SecureRandom().nextBytes(bytes)
        val sb = StringBuilder()
        var buffer = 0
        var bitsLeft = 0
        for (b in bytes) {
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bitsLeft += 8
            while (bitsLeft >= 5) {
                val index = (buffer shr (bitsLeft - 5)) and 0x1F
                sb.append(BASE32_CHARS[index])
                bitsLeft -= 5
            }
        }
        if (bitsLeft > 0) {
            val index = (buffer shl (5 - bitsLeft)) and 0x1F
            sb.append(BASE32_CHARS[index])
        }
        return sb.toString()
    }

    /**
     * Decodes a Base32 secret into bytes.
     */
    private fun decodeBase32(secret: String): ByteArray {
        val cleanSecret = secret.trim().uppercase().replace("-", "").replace(" ", "")
        val out = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0
        for (c in cleanSecret) {
            val value = BASE32_CHARS.indexOf(c)
            if (value < 0) continue
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            if (bitsLeft >= 8) {
                out.add(((buffer shr (bitsLeft - 8)) and 0xFF).toByte())
                bitsLeft -= 8
            }
        }
        return out.toByteArray()
    }

    /**
     * Generates a 6-digit TOTP code for the given secret and timestamp.
     */
    fun generateTotpCode(secret: String, timestamp: Long = System.currentTimeMillis()): String {
        val secretBytes = decodeBase32(secret)
        if (secretBytes.isEmpty()) return "000000"

        val timeStep = timestamp / 1000L / TIME_STEP_SECONDS
        val msg = ByteBuffer.allocate(8).putLong(timeStep).array()

        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(secretBytes, "HmacSHA1"))
        val hash = mac.doFinal(msg)

        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % (10.0.pow(DIGITS.toDouble()).toInt())
        return String.format("%0${DIGITS}d", otp)
    }

    /**
     * Verifies a user-supplied 6-digit code against the secret key with a 1-step window tolerance.
     */
    fun verifyTotpCode(secret: String, userCode: String, window: Int = 1): Boolean {
        val cleanCode = userCode.trim()
        if (cleanCode.length != DIGITS || !cleanCode.all { it.isDigit() }) return false

        val currentEpoch = System.currentTimeMillis()
        for (i in -window..window) {
            val stepTimestamp = currentEpoch + (i * TIME_STEP_SECONDS * 1000L)
            val expected = generateTotpCode(secret, stepTimestamp)
            if (MessageDigest.isEqual(cleanCode.toByteArray(), expected.toByteArray())) {
                return true
            }
        }
        return false
    }

    /**
     * Builds a standard key URI for QR code scanning in Google Authenticator, Microsoft Authenticator, 1Password, etc.
     */
    fun getOtpAuthUri(accountEmail: String, secret: String, issuer: String = "StyleHub South Africa"): String {
        val encodedIssuer = android.net.Uri.encode(issuer)
        val encodedAccount = android.net.Uri.encode(accountEmail)
        return "otpauth://totp/$encodedIssuer:$encodedAccount?secret=$secret&issuer=$encodedIssuer&algorithm=SHA1&digits=$DIGITS&period=$TIME_STEP_SECONDS"
    }

    /**
     * Generates a set of cryptographically random 8-character backup recovery codes.
     */
    fun generateRecoveryCodes(count: Int = 8): List<String> {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val random = SecureRandom()
        val codes = mutableListOf<String>()
        for (i in 0 until count) {
            val code = (1..8)
                .map { chars[random.nextInt(chars.length)] }
                .joinToString("")
            codes.add("${code.substring(0, 4)}-${code.substring(4, 8)}")
        }
        return codes
    }
}

object SecurityValidator {

    data class PasswordStrength(
        val isLengthValid: Boolean,
        val hasUppercase: Boolean,
        val hasLowercase: Boolean,
        val hasDigit: Boolean,
        val hasSpecialChar: Boolean
    ) {
        val score: Int
            get() {
                var s = 0
                if (isLengthValid) s += 20
                if (hasUppercase) s += 20
                if (hasLowercase) s += 20
                if (hasDigit) s += 20
                if (hasSpecialChar) s += 20
                return s
            }

        val isValid: Boolean
            get() = isLengthValid && hasUppercase && hasLowercase && (hasDigit || hasSpecialChar)
    }

    fun evaluatePassword(password: String): PasswordStrength {
        return PasswordStrength(
            isLengthValid = password.length >= 8,
            hasUppercase = password.any { it.isUpperCase() },
            hasLowercase = password.any { it.isLowerCase() },
            hasDigit = password.any { it.isDigit() },
            hasSpecialChar = password.any { !it.isLetterOrDigit() }
        )
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidSouthAfricanPhone(phone: String): Boolean {
        val clean = phone.replace(" ", "").replace("-", "")
        return clean.startsWith("+27") && clean.length in 11..12 ||
                clean.startsWith("0") && clean.length == 10
    }
}
