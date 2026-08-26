package com.example.ui.screens.customer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.SecurityAuditLogEntity
import com.example.data.local.entities.SessionEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.security.OAuthProvider
import com.example.security.SecurityValidator
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityScreen(
    currentUser: UserEntity?,
    activeSessions: List<SessionEntity>,
    auditLogs: List<SecurityAuditLogEntity>,
    mfaSetupStep: Int,
    mfaSetupSecret: String?,
    mfaSetupUri: String?,
    generatedRecoveryCodes: List<String>,
    onBack: () -> Unit,
    onStartMfaSetup: () -> Unit,
    onProceedToMfaVerify: () -> Unit,
    onConfirmMfaSetup: (testCode: String) -> Unit,
    onDisableMfa: (code: String) -> Unit,
    onRegenerateRecoveryCodes: (code: String) -> Unit,
    onCloseMfaWizard: () -> Unit,
    onChangePassword: (currentPass: String, newPass: String) -> Unit,
    onRevokeSession: (sessionId: String) -> Unit,
    onRevokeAllOtherSessions: () -> Unit,
    onVerifyEmail: () -> Unit,
    onOpenPhoneVerify: () -> Unit,
    onLinkOAuth: (OAuthProvider) -> Unit
) {
    val context = LocalContext.current
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDisableMfaDialog by remember { mutableStateOf(false) }
    var showRegenerateCodesDialog by remember { mutableStateOf(false) }
    var showLogoutAllDialog by remember { mutableStateOf(false) }
    var mfaInputCode by remember { mutableStateOf("") }

    val isMfaActive = currentUser?.isMfaEnabled == true || currentUser?.forceMfa == true
    val isAdmin = currentUser?.role == UserRole.ADMIN.name

    // Calculate dynamic security score (0-100%)
    val securityScore = remember(currentUser, isMfaActive) {
        var score = 30 // base account
        if (currentUser?.isEmailVerified == true) score += 25
        if (currentUser?.isPhoneVerified == true) score += 20
        if (isMfaActive) score += 25
        score.coerceIn(0, 100)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Account Security & Identity", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Enterprise protection & access management", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("security_screen_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Security Health Score Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (securityScore >= 80) Color(0xFF22C55E).copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (securityScore >= 80) Color(0xFF22C55E) else GoldPrimary
                                )
                            }
                            Column {
                                Text(
                                    "Security Health Score",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    if (securityScore >= 80) "Optimal Protection Active" else "Action Recommended",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (securityScore >= 80) Color(0xFF22C55E) else GoldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            "$securityScore%",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (securityScore >= 80) Color(0xFF22C55E) else GoldPrimary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { securityScore / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (securityScore >= 80) Color(0xFF22C55E) else GoldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Checklist chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SecurityStatusChip("Email", currentUser?.isEmailVerified == true)
                        SecurityStatusChip("Phone", currentUser?.isPhoneVerified == true)
                        SecurityStatusChip("2FA TOTP", isMfaActive)
                        SecurityStatusChip("Audit Logs", true)
                    }
                }
            }

            // 2. Multi-Factor Authentication (2FA) Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, tint = GoldPrimary)
                            Column {
                                Text("Two-Factor Authentication (2FA)", fontWeight = FontWeight.Bold)
                                Text(
                                    if (isAdmin) "Mandatory Policy (RFC 6238 TOTP)" else "Recommended for all accounts",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isMfaActive) Color(0xFF22C55E).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = if (isMfaActive) "ENABLED" else "DISABLED",
                                color = if (isMfaActive) Color(0xFF22C55E) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        "Protects your StyleHub account with an extra verification code from Google Authenticator, Microsoft Authenticator, or 1Password when signing in.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    if (!isMfaActive) {
                        Button(
                            onClick = onStartMfaSetup,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("enable_2fa_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set Up Authenticator App (2FA)", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRegenerateCodesDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("regenerate_recovery_codes_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Outlined.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Backup Codes", fontSize = 12.sp)
                            }

                            if (!isAdmin) {
                                OutlinedButton(
                                    onClick = { showDisableMfaDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("disable_2fa_button"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Disable 2FA", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Credentials & Verifications Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaddingTextHeader("Credentials & Verification")

                    // Password
                    ListItem(
                        headlineContent = { Text("Password", fontWeight = FontWeight.SemiBold) },
                        supportingContent = {
                            val lastChanged = currentUser?.lastPasswordChangeAt
                            val dateStr = if (lastChanged != null) {
                                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(lastChanged))
                            } else "Active"
                            Text("Last updated: $dateStr • Hashed with Salt (SHA-256 PBKDF2)")
                        },
                        leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = {
                            TextButton(
                                onClick = { showChangePasswordDialog = true },
                                modifier = Modifier.testTag("change_password_button")
                            ) {
                                Text("Change", color = GoldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    HorizontalDivider()

                    // Email Verification
                    ListItem(
                        headlineContent = { Text("Email Address", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text(currentUser?.email ?: "No email registered") },
                        leadingContent = { Icon(Icons.Outlined.Email, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = {
                            if (currentUser?.isEmailVerified == true) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF22C55E).copy(alpha = 0.15f)) {
                                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verified", fontSize = 11.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onVerifyEmail,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Verify Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    )

                    HorizontalDivider()

                    // Phone Verification
                    ListItem(
                        headlineContent = { Text("Mobile Phone (South Africa)", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text(currentUser?.phone ?: "No phone registered") },
                        leadingContent = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = {
                            if (currentUser?.isPhoneVerified == true) {
                                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF22C55E).copy(alpha = 0.15f)) {
                                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verified", fontSize = 11.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = onOpenPhoneVerify,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Verify SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    )
                }
            }

            // 4. Connected OAuth Providers Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PaddingTextHeader("Connected Social Providers")

                    // Google OAuth
                    OAuthProviderRow(
                        title = "Google Account",
                        subtitle = "Google Identity Services & CredentialManager",
                        icon = Icons.Default.AccountCircle,
                        iconTint = Color(0xFFEA4335),
                        isConnected = currentUser?.authProvider == "GOOGLE",
                        onConnect = { onLinkOAuth(OAuthProvider.Google) }
                    )

                    HorizontalDivider()

                    // Apple ID
                    OAuthProviderRow(
                        title = "Apple ID",
                        subtitle = "Apple Identity Token with ES256 verification",
                        icon = Icons.Default.PhoneIphone,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                        isConnected = currentUser?.authProvider == "APPLE",
                        onConnect = { onLinkOAuth(OAuthProvider.Apple) }
                    )

                    HorizontalDivider()

                    // Meta / Facebook Login
                    OAuthProviderRow(
                        title = "Meta / Facebook Login",
                        subtitle = "Meta Graph API (Direct Instagram login deprecated by Meta)",
                        icon = Icons.Default.Groups,
                        iconTint = Color(0xFF1877F2),
                        isConnected = currentUser?.authProvider == "FACEBOOK",
                        onConnect = { onLinkOAuth(OAuthProvider.Facebook) }
                    )
                }
            }

            // 5. Active Sessions & Device Management Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Active Device Sessions", fontWeight = FontWeight.Bold)
                            Text("${activeSessions.size} connected devices", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (activeSessions.size > 1) {
                            TextButton(
                                onClick = { showLogoutAllDialog = true },
                                modifier = Modifier.testTag("logout_all_devices_button")
                            ) {
                                Text("Log out all other devices", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        activeSessions.forEach { session ->
                            SessionItem(
                                session = session,
                                onRevoke = { onRevokeSession(session.id) }
                            )
                        }
                    }
                }
            }

            // 6. Security Audit Log Timeline
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Security Audit Trail", fontWeight = FontWeight.Bold)
                        Text("Live Log", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    if (auditLogs.isEmpty()) {
                        Text("No audit events recorded yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            auditLogs.take(10).forEach { log ->
                                AuditLogItem(log = log)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MFA SETUP WIZARD DIALOG ---
    if (mfaSetupStep > 0) {
        MfaSetupWizardDialog(
            step = mfaSetupStep,
            secret = mfaSetupSecret ?: "JBSWY3DPEHPK3PXP",
            uri = mfaSetupUri ?: "",
            recoveryCodes = generatedRecoveryCodes,
            onProceedToVerify = onProceedToMfaVerify,
            onConfirmCode = onConfirmMfaSetup,
            onClose = onCloseMfaWizard
        )
    }

    // --- CHANGE PASSWORD DIALOG ---
    if (showChangePasswordDialog) {
        ChangePasswordModal(
            onDismiss = { showChangePasswordDialog = false },
            onSubmit = { cur, new ->
                onChangePassword(cur, new)
                showChangePasswordDialog = false
            }
        )
    }

    // --- REGENERATE RECOVERY CODES DIALOG ---
    if (showRegenerateCodesDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateCodesDialog = false },
            title = { Text("Regenerate Backup Codes") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter your current 6-digit authenticator app code to invalidate current recovery codes and generate 8 new codes:")
                    OutlinedTextField(
                        value = mfaInputCode,
                        onValueChange = { mfaInputCode = it.take(6).filter { c -> c.isDigit() } },
                        label = { Text("6-Digit Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRegenerateRecoveryCodes(mfaInputCode)
                        showRegenerateCodesDialog = false
                        mfaInputCode = ""
                    },
                    enabled = mfaInputCode.length == 6,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                ) {
                    Text("Regenerate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegenerateCodesDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- DISABLE MFA DIALOG ---
    if (showDisableMfaDialog) {
        AlertDialog(
            onDismissRequest = { showDisableMfaDialog = false },
            title = { Text("Disable Two-Factor Authentication") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter a 6-digit TOTP code or an 8-character recovery code to confirm disabling 2FA:")
                    OutlinedTextField(
                        value = mfaInputCode,
                        onValueChange = { mfaInputCode = it },
                        label = { Text("Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDisableMfa(mfaInputCode)
                        showDisableMfaDialog = false
                        mfaInputCode = ""
                    },
                    enabled = mfaInputCode.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Disable 2FA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisableMfaDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- LOGOUT ALL SESSIONS DIALOG ---
    if (showLogoutAllDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutAllDialog = false },
            title = { Text("Revoke All Other Sessions") },
            text = { Text("This will terminate all active sign-ins on other phones, laptops, and tablets. Your current session will remain active.") },
            confirmButton = {
                Button(
                    onClick = {
                        onRevokeAllOtherSessions()
                        showLogoutAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Revoke All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutAllDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SecurityStatusChip(label: String, isOk: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isOk) Color(0xFF22C55E).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                if (isOk) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (isOk) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isOk) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PaddingTextHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
    )
}

@Composable
private fun OAuthProviderRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isConnected: Boolean,
    onConnect: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingContent = { Icon(icon, contentDescription = null, tint = iconTint) },
        trailingContent = {
            if (isConnected) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF22C55E).copy(alpha = 0.15f)) {
                    Text(
                        "Connected",
                        fontSize = 11.sp,
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onConnect,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Link", fontSize = 11.sp)
                }
            }
        }
    )
}

@Composable
private fun SessionItem(session: SessionEntity, onRevoke: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(session.lastActiveAt))

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = if (session.isCurrent) androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                if (session.deviceType == "DESKTOP") Icons.Default.Computer else Icons.Default.Smartphone,
                contentDescription = null,
                tint = if (session.isCurrent) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(session.deviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (session.isCurrent) {
                        Surface(shape = RoundedCornerShape(4.dp), color = GoldPrimary) {
                            Text("THIS DEVICE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = CharcoalDark, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }
                Text("${session.location} • IP: ${session.ipAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Active: $dateStr • ${session.osVersion}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }

            if (!session.isCurrent) {
                IconButton(onClick = onRevoke) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Revoke session", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AuditLogItem(log: SecurityAuditLogEntity) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
    val sevColor = when (log.severity) {
        "CRITICAL" -> Color(0xFFEF4444)
        "WARNING" -> Color(0xFFF97316)
        else -> Color(0xFF22C55E)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(sevColor)
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.eventType.replace("_", " "), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${log.device} • ${log.ipAddress}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun MfaSetupWizardDialog(
    step: Int,
    secret: String,
    uri: String,
    recoveryCodes: List<String>,
    onProceedToVerify: () -> Unit,
    onConfirmCode: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var testCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("mfa_setup_wizard_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "2FA Setup Wizard (Step $step of 3)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                when (step) {
                    1 -> {
                        // STEP 1: Display Secret Key & Instructions
                        Text(
                            "1. Open your authenticator app (Google Authenticator, Microsoft Authenticator, or 1Password).",
                            fontSize = 13.sp
                        )
                        Text(
                            "2. Choose 'Enter a setup key' and paste the secret key below:",
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = secret,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 2.sp,
                                    color = GoldPrimary
                                )

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("TOTP Secret", secret))
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy secret key")
                                }
                            }
                        }

                        Button(
                            onClick = onProceedToVerify,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next: Enter Verification Code", fontWeight = FontWeight.Bold)
                        }
                    }

                    2 -> {
                        // STEP 2: Verify First Code
                        Text(
                            "Enter the 6-digit verification code currently shown in your authenticator app to activate 2FA:",
                            fontSize = 13.sp
                        )

                        OutlinedTextField(
                            value = testCode,
                            onValueChange = { testCode = it.take(6).filter { c -> c.isDigit() } },
                            label = { Text("6-Digit Code") },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 6.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { onConfirmCode(testCode) },
                            enabled = testCode.length == 6,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Verify & Activate", fontWeight = FontWeight.Bold)
                        }
                    }

                    3 -> {
                        // STEP 3: Display 8 Emergency Backup Recovery Codes
                        Text(
                            "🛡️ Save Your Emergency Recovery Codes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
                        )
                        Text(
                            "If you ever lose access to your authenticator phone, each of these 8 codes can be used once to log in. Store them in a safe password manager.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                recoveryCodes.chunked(2).forEach { rowCodes ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        rowCodes.forEach { code ->
                                            Text(
                                                code,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("StyleHub Recovery Codes", recoveryCodes.joinToString("\n")))
                                onClose()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Codes & Finish", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangePasswordModal(
    onDismiss: () -> Unit,
    onSubmit: (currentPass: String, newPass: String) -> Unit
) {
    var curPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    val strength = remember(newPass) { SecurityValidator.evaluatePassword(newPass) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Change Account Password", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                OutlinedTextField(
                    value = curPass,
                    onValueChange = { curPass = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password (Min 8 characters)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = confirmPass.isNotBlank() && confirmPass != newPass,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(curPass, newPass) },
                        enabled = curPass.isNotBlank() && strength.isValid && newPass == confirmPass,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                    ) {
                        Text("Update Password")
                    }
                }
            }
        }
    }
}
