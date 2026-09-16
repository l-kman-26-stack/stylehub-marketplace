package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.AccountStatus
import com.example.data.local.entities.UserEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    currentUser: UserEntity?,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onDeactivate: (password: String) -> Unit,
    onReactivate: (password: String) -> Unit,
    onRequestDeletion: (password: String) -> Unit,
    onCancelDeletion: () -> Unit,
    onPurgePermanently: () -> Unit,
    onClearError: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Dialog & Flow States
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showReactivateDialog by remember { mutableStateOf(false) }
    var showDeleteFlowDialog by remember { mutableStateOf(false) }
    var showCancelDeletionDialog by remember { mutableStateOf(false) }

    // Input fields for password verification
    var deactivatePassword by remember { mutableStateOf("") }
    var reactivatePassword by remember { mutableStateOf("") }

    val rawStatus = currentUser?.accountStatus ?: AccountStatus.ACTIVE.name
    val currentStatus = try {
        AccountStatus.valueOf(rawStatus)
    } catch (e: Exception) {
        AccountStatus.ACTIVE
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Account Status & Management",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Settings → Account → Account Status",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("account_management_back_button")
                    ) {
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
                .verticalScroll(scrollState)
                .padding(16.dp)
                .testTag("account_management_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Banner
            if (!errorMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearError, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 1. Current Account Status Card
            AccountStatusHeaderCard(
                user = currentUser,
                status = currentStatus,
                dateFormatter = dateFormatter,
                onCancelDeletionClick = { showCancelDeletionDialog = true },
                onReactivateClick = { showReactivateDialog = true }
            )

            // 2. Important Difference Explanatory Card (Deactivate vs Delete)
            DifferenceExplainerCard()

            // 3. Temporary Option: Deactivate / Activate Account
            TemporaryDeactivateCard(
                status = currentStatus,
                onDeactivateClick = {
                    deactivatePassword = ""
                    showDeactivateDialog = true
                },
                onActivateClick = {
                    reactivatePassword = ""
                    showReactivateDialog = true
                }
            )

            // 4. Permanent Option: Delete Account Permanently (Visually Separated)
            PermanentDeleteCard(
                status = currentStatus,
                onStartDeleteFlow = { showDeleteFlowDialog = true },
                onCancelDeletionClick = { showCancelDeletionDialog = true }
            )

            // 5. Data Protection, POPIA & Retention Policy Info
            DataProtectionNoticeCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // --- DIALOG: Deactivate Confirmation ---
    if (showDeactivateDialog) {
        DeactivateConfirmDialog(
            isLoading = isLoading,
            password = deactivatePassword,
            onPasswordChange = { deactivatePassword = it },
            onConfirm = {
                onDeactivate(deactivatePassword)
                showDeactivateDialog = false
            },
            onDismiss = { showDeactivateDialog = false }
        )
    }

    // --- DIALOG: Reactivate / Activate Confirmation ---
    if (showReactivateDialog) {
        ReactivateConfirmDialog(
            isLoading = isLoading,
            password = reactivatePassword,
            onPasswordChange = { reactivatePassword = it },
            onConfirm = {
                onReactivate(reactivatePassword)
                showReactivateDialog = false
            },
            onDismiss = { showReactivateDialog = false }
        )
    }

    // --- MULTI-STEP DIALOG: Delete Account Permanently Flow ---
    if (showDeleteFlowDialog) {
        DeleteAccountMultiStepFlowDialog(
            user = currentUser,
            isLoading = isLoading,
            onCompleteDeletion = { password ->
                onRequestDeletion(password)
                showDeleteFlowDialog = false
            },
            onDismiss = { showDeleteFlowDialog = false },
            onSwitchToDeactivate = {
                showDeleteFlowDialog = false
                deactivatePassword = ""
                showDeactivateDialog = true
            }
        )
    }

    // --- DIALOG: Cancel Deletion Request Confirmation ---
    if (showCancelDeletionDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDeletionDialog = false },
            icon = {
                Icon(
                    Icons.Outlined.Restore,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Cancel Deletion Request?") },
            text = {
                Text("Changed your mind? Your account deletion request will be cancelled immediately and your account will remain active. You will retain all your saved bookings, reviews, and profile data.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelDeletion()
                        showCancelDeletionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = CharcoalDark
                    ),
                    modifier = Modifier.testTag("confirm_cancel_deletion_btn")
                ) {
                    Text("Yes, Keep Account Active", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDeletionDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------------------
// SECTION 1: Current Account Status Card
// -----------------------------------------------------------------------------------------
@Composable
private fun AccountStatusHeaderCard(
    user: UserEntity?,
    status: AccountStatus,
    dateFormatter: SimpleDateFormat,
    onCancelDeletionClick: () -> Unit,
    onReactivateClick: () -> Unit
) {
    val statusColor = when (status) {
        AccountStatus.ACTIVE -> Color(0xFF22C55E)
        AccountStatus.DEACTIVATED -> Color(0xFFF59E0B)
        AccountStatus.PENDING_DELETION -> Color(0xFFEF4444)
        AccountStatus.DELETED -> Color(0xFF6B7280)
    }

    val statusIcon = when (status) {
        AccountStatus.ACTIVE -> Icons.Default.CheckCircle
        AccountStatus.DEACTIVATED -> Icons.Default.PauseCircle
        AccountStatus.PENDING_DELETION -> Icons.Default.Schedule
        AccountStatus.DELETED -> Icons.Default.Cancel
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("account_status_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor)
                    }
                    Column {
                        Text(
                            text = "Account Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                    }
                }

                // Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = status.name,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider()

            // Status Description
            when (status) {
                AccountStatus.ACTIVE -> {
                    Text(
                        text = "Your account is currently active and all app features are available. You can discover barbers & salons, make instant appointments, submit reviews, and manage your preferences.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AccountStatus.DEACTIVATED -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "The account has temporarily been disabled by the user. Your account information and associated data are safely retained according to StyleHub's data-retention policy. You can reactivate your account at any time to restore full access.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (user?.deactivatedAt != null) {
                            Text(
                                text = "Deactivated on: ${dateFormatter.format(Date(user.deactivatedAt))}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = onReactivateClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = CharcoalDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("header_activate_account_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Activate Account Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                AccountStatus.PENDING_DELETION -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "The user has requested permanent deletion and the account is within the 30-day deletion waiting period.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (user?.deletionRequestedAt != null) {
                                    Text(
                                        text = "Deletion Requested: ${dateFormatter.format(Date(user.deletionRequestedAt))}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                val scheduledTime = user?.scheduledDeletionAt ?: (System.currentTimeMillis() + (30L * 24 * 3600 * 1000L))
                                Text(
                                    text = "Scheduled Permanent Purge: ${dateFormatter.format(Date(scheduledTime))}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "During this 30-day grace period, all your data remains recoverable.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }

                        Button(
                            onClick = onCancelDeletionClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22C55E),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("header_cancel_deletion_btn")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Cancel Deletion & Keep Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                AccountStatus.DELETED -> {
                    Text(
                        text = "This account has been permanently removed according to our account deletion policy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SECTION 2: Explanatory Difference Card (Deactivate vs Delete)
// -----------------------------------------------------------------------------------------
@Composable
private fun DifferenceExplainerCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = GoldPrimary)
                Text(
                    text = "Understanding the Difference",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Text(
                text = "Please review the distinct differences below so you do not accidentally delete your account when you only wish to take a temporary break.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Deactivate Column
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Pause, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                            Text("Deactivate", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFF59E0B))
                        }
                        Text("• Temporary pause", fontSize = 11.sp)
                        Text("• Profile hidden publicly", fontSize = 11.sp)
                        Text("• Data & bookings retained", fontSize = 11.sp)
                        Text("• Reactivate anytime with 1 click", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Delete Column
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Text("Permanently Delete", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                        }
                        Text("• Permanent removal", fontSize = 11.sp)
                        Text("• 30-day grace period", fontSize = 11.sp)
                        Text("• Multi-step verification", fontSize = 11.sp)
                        Text("• Irreversible after 30 days", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SECTION 3: Temporary Option (Deactivate / Activate)
// -----------------------------------------------------------------------------------------
@Composable
private fun TemporaryDeactivateCard(
    status: AccountStatus,
    onDeactivateClick: () -> Unit,
    onActivateClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("temporary_deactivate_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (status == AccountStatus.DEACTIVATED) Icons.Default.PlayCircle else Icons.Default.PauseCircle,
                    contentDescription = null,
                    tint = if (status == AccountStatus.DEACTIVATED) Color(0xFF22C55E) else Color(0xFFF59E0B)
                )
                Text(
                    text = if (status == AccountStatus.DEACTIVATED) "Activate Account" else "Temporary Option: Deactivate Account",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (status == AccountStatus.DEACTIVATED) {
                Text(
                    text = "Your account is currently deactivated. Activate your account to restore full access to the app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onActivateClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = CharcoalDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("activate_account_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Activate Account", fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "Deactivating your account temporarily disables your account. Your account information and associated data will be retained according to the app's data-retention policy. You can reactivate your account later.\n\nDeactivation is not the same as deletion. You will not lose your account or your history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onDeactivateClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF59E0B)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("deactivate_account_button")
                ) {
                    Icon(Icons.Default.PauseCircleOutline, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Deactivate Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SECTION 4: Permanent Option (Delete Account Permanently)
// -----------------------------------------------------------------------------------------
@Composable
private fun PermanentDeleteCard(
    status: AccountStatus,
    onStartDeleteFlow: () -> Unit,
    onCancelDeletionClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("permanent_delete_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Permanent Option: Delete Account Permanently",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }

            Text(
                text = "Permanently deleting your account will remove your account and associated personal information according to the app's data-retention and legal requirements. This action may not be reversible.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Transparency Breakdown of Deletion Consequences
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Pre-Deletion Disclosure:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• What will be deleted: Profile data, direct messages, preferences & active logins.", fontSize = 11.sp)
                    Text("• What may be retained: Invoiced booking receipts and security logs (required for South African tax & POPIA compliance).", fontSize = 11.sp)
                    Text("• Recovery Period: A 30-day grace period is provided. You can cancel deletion anytime before the expiration date.", fontSize = 11.sp)
                    Text("• Active Sessions: All device sessions will be invalidated immediately.", fontSize = 11.sp)
                    Text("• Subscriptions & Appointments: Pending appointments will be automatically cancelled.", fontSize = 11.sp)
                }
            }

            if (status == AccountStatus.PENDING_DELETION) {
                Button(
                    onClick = onCancelDeletionClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("cancel_deletion_button")
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cancel Deletion (30-Day Grace Active)", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onStartDeleteFlow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("delete_account_button")
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SECTION 5: Data Protection & Legal Notice Card
// -----------------------------------------------------------------------------------------
@Composable
private fun DataProtectionNoticeCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Policy, contentDescription = null, tint = GoldPrimary)
                Text("POPIA & Data-Retention Standards", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Text(
                text = "StyleHub complies with South Africa's Protection of Personal Information Act 4 of 2013 (POPIA). When an account enters the deletion process, personal identifying information is scheduled for permanent purge after 30 days. To contact the Information Officer, email privacy@stylehub.co.za.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------------------
// DIALOG: Deactivation Confirmation Dialog
// -----------------------------------------------------------------------------------------
@Composable
private fun DeactivateConfirmDialog(
    isLoading: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Deactivate your account?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Your account will be temporarily disabled. You will not be able to access normal account features until you reactivate it.\n\nYour account information and associated data will be retained safely.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Please enter your password to confirm:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deactivate_password_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF59E0B),
                    contentColor = CharcoalDark
                ),
                modifier = Modifier.testTag("confirm_deactivate_account_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = CharcoalDark
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text("Deactivate Account", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                modifier = Modifier.testTag("cancel_deactivate_dialog_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

// -----------------------------------------------------------------------------------------
// DIALOG: Reactivation Confirmation Dialog
// -----------------------------------------------------------------------------------------
@Composable
private fun ReactivateConfirmDialog(
    isLoading: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Activate your account?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Your account will be reactivated and you will regain access to the app.\n\nApplicable notifications and normal booking features will be fully restored.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Please enter your password to confirm:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reactivate_password_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = CharcoalDark
                ),
                modifier = Modifier.testTag("confirm_reactivate_account_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = CharcoalDark
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text("Activate Account", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                modifier = Modifier.testTag("cancel_reactivate_dialog_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}

// -----------------------------------------------------------------------------------------
// MULTI-STEP DIALOG: Permanent Deletion Flow (Step 1 -> 2 -> 3 -> 4)
// -----------------------------------------------------------------------------------------
@Composable
private fun DeleteAccountMultiStepFlowDialog(
    user: UserEntity?,
    isLoading: Boolean,
    onCompleteDeletion: (password: String) -> Unit,
    onDismiss: () -> Unit,
    onSwitchToDeactivate: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var acknowledgedWarning by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var typedDeleteConfirm by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("delete_multi_step_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..4).forEach { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == step) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i == step) MaterialTheme.colorScheme.error
                                        else if (i < step) Color(0xFF22C55E)
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    Text(
                        text = "Step $step of 4",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                when (step) {
                    // ----------------------------------------------------
                    // STEP 1: Warning & Option to Deactivate Instead
                    // ----------------------------------------------------
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text("Delete your account?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            }

                            Text(
                                text = "This action is intended for permanent account removal. Your profile and associated account information may be permanently removed.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B))
                                    Text(
                                        text = "If you only want to take a break from the app, consider deactivating your account instead. Deactivating keeps all your history safe.",
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            OutlinedButton(
                                onClick = onSwitchToDeactivate,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("step1_switch_to_deactivate_btn")
                            ) {
                                Icon(Icons.Default.PauseCircle, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(Modifier.width(6.dp))
                                Text("Deactivate Instead (Recommended)")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("step1_keep_account_btn")
                                ) {
                                    Text("Keep My Account")
                                }

                                Button(
                                    onClick = { step = 2 },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("step1_continue_btn")
                                ) {
                                    Text("Continue to Delete")
                                }
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // STEP 2: Explain Consequences & Mandatory Acknowledgement
                    // ----------------------------------------------------
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Before continuing, please make sure you understand what will happen to your account:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ConsequenceItem("Your account access will be disabled immediately.")
                                    ConsequenceItem("Your profile and public listings will no longer be visible.")
                                    ConsequenceItem("Account-related data scheduled for deletion will be removed according to the deletion policy.")
                                    ConsequenceItem("Some records may need to be retained for legal, security, fraud-prevention, or transaction purposes.")
                                    ConsequenceItem("Deletion may not be reversible after the 30-day deletion period has expired.")
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("step2_checkbox_row")
                            ) {
                                Checkbox(
                                    checked = acknowledgedWarning,
                                    onCheckedChange = { acknowledgedWarning = it },
                                    modifier = Modifier.testTag("step2_acknowledge_checkbox")
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "I understand that permanently deleting my account may not be reversible.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = { step = 1 }, modifier = Modifier.weight(1f)) {
                                    Text("Back")
                                }
                                Button(
                                    onClick = { step = 3 },
                                    enabled = acknowledgedWarning,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("step2_continue_btn")
                                ) {
                                    Text("Continue")
                                }
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // STEP 3: Identity Verification (Re-Authentication)
                    // ----------------------------------------------------
                    3 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Verify your identity before deletion:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                            Text(
                                text = "To ensure security and prevent unauthorized destruction, enter your account password.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Account Password") },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("step3_password_field")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = { step = 2 }, modifier = Modifier.weight(1f)) {
                                    Text("Back")
                                }
                                Button(
                                    onClick = { step = 4 },
                                    enabled = password.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("step3_verify_btn")
                                ) {
                                    Text("Verify & Continue")
                                }
                            }
                        }
                    }

                    // ----------------------------------------------------
                    // STEP 4: Final Confirmation (Type "DELETE")
                    // ----------------------------------------------------
                    4 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Final confirmation",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            )

                            Text(
                                text = "You are about to request permanent deletion of your account. This action cannot be easily undone after the 30-day deletion period has expired.\n\nTo confirm, type DELETE below:",
                                style = MaterialTheme.typography.bodySmall
                            )

                            OutlinedTextField(
                                value = typedDeleteConfirm,
                                onValueChange = { typedDeleteConfirm = it },
                                label = { Text("Type DELETE in capital letters") },
                                singleLine = true,
                                isError = typedDeleteConfirm.isNotBlank() && typedDeleteConfirm != "DELETE",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("step4_type_delete_field")
                            )

                            if (typedDeleteConfirm.isNotBlank() && typedDeleteConfirm != "DELETE") {
                                Text(
                                    text = "Must match exact word: DELETE",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { step = 3 },
                                    enabled = !isLoading,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back")
                                }

                                Button(
                                    onClick = { onCompleteDeletion(password) },
                                    enabled = !isLoading && typedDeleteConfirm == "DELETE",
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .testTag("step4_final_confirm_btn")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text("Permanently Delete My Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsequenceItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("•", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        Text(text, fontSize = 11.sp, lineHeight = 15.sp)
    }
}
