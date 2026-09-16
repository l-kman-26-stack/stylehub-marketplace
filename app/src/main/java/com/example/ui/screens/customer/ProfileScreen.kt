package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit = {},
    totalBookings: Int,
    totalSaved: Int,
    onOpenNotifications: () -> Unit,
    onOpenSecurity: () -> Unit = {},
    onOpenAccountManagement: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onOpenBusinessDashboard: () -> Unit = {},
    onOpenAdminConsole: () -> Unit = {},
    onOpenPartnerOnboarding: () -> Unit = {},
    onOpenHelpCentre: () -> Unit = {},
    onOpenAboutUs: () -> Unit = {},
    onOpenLegal: () -> Unit = {},
    onOpenLandingPage: () -> Unit = {},
    onOpenTemplates: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Prominent Alert Banner if Account is Deactivated or Pending Deletion
        val accountStatus = currentUser?.accountStatus ?: "ACTIVE"
        if (currentUser != null && accountStatus != "ACTIVE") {
            val isDeactivated = accountStatus == "DEACTIVATED"
            val bannerColor = if (isDeactivated) Color(0xFFF59E0B) else Color(0xFFEF4444)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = bannerColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, bannerColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_account_status_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (isDeactivated) Icons.Default.PauseCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = bannerColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDeactivated) "Account is Deactivated" else "Account Pending Deletion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = bannerColor
                        )
                        Text(
                            text = if (isDeactivated)
                                "Your account is temporarily disabled. Tap to activate and restore full access."
                            else
                                "30-day grace period is active. You can cancel deletion anytime.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onOpenAccountManagement,
                        colors = ButtonDefaults.buttonColors(containerColor = bannerColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("banner_manage_account_btn")
                    ) {
                        Text(
                            text = if (isDeactivated) "Activate" else "Manage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (currentUser != null) {
            // Authenticated User Info Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val initials = currentUser.name.split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .uppercase()
                            .ifBlank { "SH" }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = currentUser.email.ifBlank { currentUser.phone },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (currentUser.city.isNotBlank()) {
                                Text(
                                    text = "${currentUser.city}, ${currentUser.province}",
                                    fontSize = 12.sp,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Role Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (currentRole) {
                                UserRole.ADMIN -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                UserRole.BUSINESS_OWNER -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                UserRole.CUSTOMER -> GoldPrimary.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = when (currentRole) {
                                    UserRole.ADMIN -> "Admin"
                                    UserRole.BUSINESS_OWNER -> "Business Partner"
                                    UserRole.CUSTOMER -> "Client"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (currentRole) {
                                    UserRole.ADMIN -> Color(0xFFEF4444)
                                    UserRole.BUSINESS_OWNER -> Color(0xFF3B82F6)
                                    UserRole.CUSTOMER -> CharcoalDark
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider()

                    // Quick metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = totalBookings.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text("Bookings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = totalSaved.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text("Saved", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🇿🇦 ZAR",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                            )
                            Text("Currency", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else {
            // Unauthenticated Guest Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(32.dp))
                    }

                    Text(
                        text = "Welcome to StyleHub",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = "Sign in to manage appointments, save favourite studios, and access personalized grooming recommendations across South Africa.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = onOpenAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("profile_signin_button")
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In or Register", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Role-Specific Action Card
        when (currentRole) {
            UserRole.BUSINESS_OWNER -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E3A8A).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Business Owner Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Manage services, working hours, appointments and staff", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = onOpenBusinessDashboard,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            UserRole.ADMIN -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF7F1D1D).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Platform Administration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Approve listings, review audit logs and manage platform settings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = onOpenAdminConsole,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Admin Console", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            UserRole.CUSTOMER -> {
                // Partner Call to action for Customer
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Own a Salon or Barbershop?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Partner with StyleHub to reach new clients across South Africa.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = onOpenPartnerOnboarding,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("List Business", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Account & Security Settings Options
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentUser != null) {
                    // Account Status & Management (Activate, Deactivate, Delete)
                    ListItem(
                        headlineContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Account Status & Lifecycle", fontWeight = FontWeight.SemiBold)
                                val statusStr = currentUser.accountStatus
                                val statusColor = when (statusStr) {
                                    "ACTIVE" -> Color(0xFF22C55E)
                                    "DEACTIVATED" -> Color(0xFFF59E0B)
                                    "PENDING_DELETION" -> Color(0xFFEF4444)
                                    else -> Color(0xFF6B7280)
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = statusColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = statusStr.replace("_", " "),
                                        fontSize = 9.sp,
                                        color = statusColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        supportingContent = { Text("Deactivate account or submit deletion request") },
                        leadingContent = { Icon(Icons.Outlined.ManageAccounts, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier
                            .clickable { onOpenAccountManagement() }
                            .testTag("profile_account_status_management_item")
                    )
                    HorizontalDivider()

                    // Security & 2FA
                    ListItem(
                        headlineContent = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Account Security & Credentials", fontWeight = FontWeight.SemiBold)
                                if (currentUser.isMfaEnabled || currentUser.forceMfa) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF22C55E).copy(alpha = 0.15f)) {
                                        Text("2FA ACTIVE", fontSize = 9.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        },
                        supportingContent = { Text("Password updates, 2FA TOTP authentication & device sessions") },
                        leadingContent = { Icon(Icons.Outlined.Shield, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier
                            .clickable { onOpenSecurity() }
                            .testTag("profile_account_security_item")
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Notifications Center") },
                        leadingContent = { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = GoldPrimary) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onOpenNotifications() }
                    )
                    HorizontalDivider()
                }

                ListItem(
                    headlineContent = { Text("Payment & Currency") },
                    supportingContent = { Text("South African Rand (ZAR) • Direct payment on appointment") },
                    leadingContent = { Icon(Icons.Outlined.Payments, contentDescription = null, tint = GoldPrimary) }
                )
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Help & Support Desk") },
                    supportingContent = { Text("Submit tickets, view FAQs & customer support") },
                    leadingContent = { Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = GoldPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpenHelpCentre() }
                )
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("About StyleHub") },
                    supportingContent = { Text("South Africa's premier salon & barber marketplace") },
                    leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null, tint = GoldPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpenAboutUs() }
                )
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Legal, Privacy & POPIA Compliance") },
                    supportingContent = { Text("Data protection policies, Terms of Service & cancellation rules") },
                    leadingContent = { Icon(Icons.Outlined.Policy, contentDescription = null, tint = GoldPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpenLegal() }
                )
                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Communication Notices") },
                    supportingContent = { Text("SMS & email booking notifications") },
                    leadingContent = { Icon(Icons.Outlined.Email, contentDescription = null, tint = GoldPrimary) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onOpenTemplates() }
                )
            }
        }

        // Authentication Actions (Sign In / Register / Log Out)
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentUser == null) {
                    Button(
                        onClick = onOpenAuth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_open_auth_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In or Register", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_sign_out_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
