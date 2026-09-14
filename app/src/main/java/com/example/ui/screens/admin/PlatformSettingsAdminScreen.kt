package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.PlatformSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformSettingsAdminScreen(
    currentSettings: PlatformSettings,
    onSaveSettings: (PlatformSettings) -> Unit,
    onBack: () -> Unit
) {
    var platformName by remember(currentSettings) { mutableStateOf(currentSettings.platformName) }
    var tagline by remember(currentSettings) { mutableStateOf(currentSettings.tagline) }
    var registrationPlaceholder by remember(currentSettings) { mutableStateOf(currentSettings.registrationNumberPlaceholder) }
    var addressPlaceholder by remember(currentSettings) { mutableStateOf(currentSettings.registeredAddressPlaceholder) }
    var supportEmail by remember(currentSettings) { mutableStateOf(currentSettings.supportEmail) }
    var supportPhone by remember(currentSettings) { mutableStateOf(currentSettings.supportPhone) }
    var infoOfficerEmail by remember(currentSettings) { mutableStateOf(currentSettings.infoOfficerEmail) }
    var announcementActive by remember(currentSettings) { mutableStateOf(currentSettings.announcementActive) }
    var announcementText by remember(currentSettings) { mutableStateOf(currentSettings.announcementText) }
    var maintenanceMode by remember(currentSettings) { mutableStateOf(currentSettings.maintenanceMode) }
    var enableInstantBooking by remember(currentSettings) { mutableStateOf(currentSettings.enableInstantBooking) }
    var enableTotpMfa by remember(currentSettings) { mutableStateOf(currentSettings.enableTotpMfa) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Platform Settings & Audit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Marketplace Configuration & Production Readiness",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_settings_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val updated = currentSettings.copy(
                                platformName = platformName,
                                tagline = tagline,
                                registrationNumberPlaceholder = registrationPlaceholder,
                                registeredAddressPlaceholder = addressPlaceholder,
                                supportEmail = supportEmail,
                                supportPhone = supportPhone,
                                infoOfficerEmail = infoOfficerEmail,
                                announcementActive = announcementActive,
                                announcementText = announcementText,
                                maintenanceMode = maintenanceMode,
                                enableInstantBooking = enableInstantBooking,
                                enableTotpMfa = enableTotpMfa
                            )
                            onSaveSettings(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        modifier = Modifier.testTag("save_platform_settings_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Config", fontWeight = FontWeight.Bold)
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
                .padding(16.dp)
                .testTag("platform_settings_admin_screen"),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Section 1: Commercial & Identity Branding
            Text(
                text = "1. Commercial Branding & Disclosures",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = platformName,
                onValueChange = { platformName = it },
                label = { Text("Platform Brand Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("Official Brand Tagline") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = registrationPlaceholder,
                onValueChange = { registrationPlaceholder = it },
                label = { Text("Corporate Registration Disclosure") },
                supportingText = { Text("Clearly marks placeholder company details for South African CIPC filing.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = addressPlaceholder,
                onValueChange = { addressPlaceholder = it },
                label = { Text("Registered Business Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            // Section 2: Support & Compliance Endpoints
            Text(
                text = "2. Support & Compliance Endpoints",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = supportEmail,
                onValueChange = { supportEmail = it },
                label = { Text("Customer & Partner Support Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = supportPhone,
                onValueChange = { supportPhone = it },
                label = { Text("Helpline Phone Number (+27)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = infoOfficerEmail,
                onValueChange = { infoOfficerEmail = it },
                label = { Text("POPIA Information Officer Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            // Section 3: Announcements & Banners
            Text(
                text = "3. Platform Announcements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Show Announcement Banner on Landing", fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = announcementActive,
                            onCheckedChange = { announcementActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = Color(0xFFC5A059).copy(alpha = 0.4f))
                        )
                    }

                    if (announcementActive) {
                        OutlinedTextField(
                            value = announcementText,
                            onValueChange = { announcementText = it },
                            label = { Text("Banner Message") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // Section 4: System Operational Modes
            Text(
                text = "4. System Operational Flags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Maintenance Mode", fontWeight = FontWeight.SemiBold)
                            Text("Displays notice on landing page for scheduled updates", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = maintenanceMode,
                            onCheckedChange = { maintenanceMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE65100))
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Instant Appointment Confirmation", fontWeight = FontWeight.SemiBold)
                            Text("Auto-confirms booking requests without owner manual approval step", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = enableInstantBooking,
                            onCheckedChange = { enableInstantBooking = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enforce 2FA TOTP for Staff/Admins", fontWeight = FontWeight.SemiBold)
                            Text("Prompts all merchant & admin users to activate Google Authenticator", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = enableTotpMfa,
                            onCheckedChange = { enableTotpMfa = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                        )
                    }
                }
            }
        }
    }
}
