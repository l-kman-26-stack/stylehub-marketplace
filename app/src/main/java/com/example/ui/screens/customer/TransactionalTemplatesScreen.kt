package com.example.ui.screens.customer

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

enum class TemplateType(val title: String, val category: String) {
    WELCOME("Welcome & Verification", "Account"),
    SECURITY_2FA("2FA Security Alert", "Security"),
    PASSWORD_RESET("Password Reset", "Security"),
    BOOKING_REQUEST("Booking Requested", "Appointment"),
    BOOKING_CONFIRMED("Booking Confirmed", "Appointment"),
    BOOKING_CANCELLED("Booking Cancelled", "Appointment"),
    BUSINESS_APPROVED("Listing Approved", "Merchant"),
    REVIEW_RECEIVED("Review Received", "Merchant")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionalTemplatesScreen(
    onBack: () -> Unit
) {
    var selectedTemplate by remember { mutableStateOf(TemplateType.WELCOME) }
    var previewMode by remember { mutableStateOf("Email (HTML)") } // "Email (HTML)" vs "SMS / Push"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Transactional Communications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automated Email & SMS Notification Templates",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("templates_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Channel Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = previewMode == "Email (HTML)",
                    onClick = { previewMode = "Email (HTML)" },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Email (HTML)")
                }
                SegmentedButton(
                    selected = previewMode == "SMS / Push",
                    onClick = { previewMode = "SMS / Push" },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SMS / Push")
                }
            }

            // Template Selector Chips
            Text(
                text = "Select Transactional Event Template",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTemplate.ordinal,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                TemplateType.values().forEach { template ->
                    Tab(
                        selected = selectedTemplate == template,
                        onClick = { selectedTemplate = template },
                        text = {
                            Text(
                                text = template.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTemplate == template) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Live Template Card Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (previewMode == "Email (HTML)") MaterialTheme.colorScheme.surface else Color(0xFF1E2430)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (previewMode == "Email (HTML)") {
                        // Email Header Simulation
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("From: StyleHub Notifications <notifications@stylehub.co.za>", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                Text("To: leletukamana9@gmail.com", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                Text("Subject: ${getEmailSubject(selectedTemplate)}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Email Brand Header Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CharcoalDark)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("STYLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("HUB", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                            }
                        }

                        // Body
                        Text(
                            text = getEmailBody(selectedTemplate),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )

                        // Action Button Simulation
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(getButtonLabel(selectedTemplate), fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "StyleHub Technologies (Pty) Ltd • 138 West St, Sandton, Johannesburg\nThis is an automated operational notification. POPIA Compliant.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // SMS / Push Notification Simulation
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF283244)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.ChatBubble, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Text("SMS • StyleHub-SA (+27)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = getSmsBody(selectedTemplate),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Technical Dispatcher Configuration Note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = GoldPrimary)
                    Text(
                        text = "Configurable Delivery Engine: Integrates with Twilio, SendGrid, Amazon SES, or Africa's Talking via environment variables (SMTP_HOST / SMS_API_KEY).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun getEmailSubject(type: TemplateType): String = when (type) {
    TemplateType.WELCOME -> "Welcome to StyleHub! Confirm your email address"
    TemplateType.SECURITY_2FA -> "🔐 Security Alert: New Sign-in with 2FA Challenge"
    TemplateType.PASSWORD_RESET -> "🔑 Reset your StyleHub account password"
    TemplateType.BOOKING_REQUEST -> "📅 Appointment Requested: Signature Fade & Beard Trim"
    TemplateType.BOOKING_CONFIRMED -> "✅ Booking Confirmed at Legends Barbershop Sandton"
    TemplateType.BOOKING_CANCELLED -> "❌ Appointment Cancelled: Signature Fade & Beard Trim"
    TemplateType.BUSINESS_APPROVED -> "🎉 Congratulations! Your StyleHub Salon is Now Live"
    TemplateType.REVIEW_RECEIVED -> "⭐ New 5-Star Review Received on StyleHub"
}

private fun getEmailBody(type: TemplateType): String = when (type) {
    TemplateType.WELCOME -> "Hi Leletu,\n\nWelcome to StyleHub, South Africa's premier hair and grooming marketplace! To ensure account security and receive instant appointment updates, please verify your email address by clicking the button below."
    TemplateType.SECURITY_2FA -> "Hi Leletu,\n\nA new login attempt was detected from Chrome on Android in Johannesburg, South Africa. Enter your 6-digit TOTP authenticator code to proceed. If this wasn't you, revoke your sessions immediately."
    TemplateType.PASSWORD_RESET -> "Hi Leletu,\n\nWe received a request to reset your StyleHub password. Click the secure link below to set a new password. If you did not make this request, you can safely ignore this email."
    TemplateType.BOOKING_REQUEST -> "Hi Leletu,\n\nYour appointment request for 'Signature Fade & Beard Trim' at Legends Barbershop Sandton has been received for Wednesday, 26 Aug at 11:00. Total: R220. The salon will confirm shortly."
    TemplateType.BOOKING_CONFIRMED -> "Great news! Your booking at Legends Barbershop Sandton has been CONFIRMED by Master Barber Sipho.\n\nDate: Wednesday, 26 Aug at 11:00\nTotal: R220 (Pay at salon via Card/Cash)\nAddress: Shop 14, Sandton City, Johannesburg"
    TemplateType.BOOKING_CANCELLED -> "Hi Leletu,\n\nYour appointment for 'Signature Fade & Beard Trim' at Legends Barbershop on 26 Aug has been cancelled. If you wish to reschedule, browse available slots on the app."
    TemplateType.BUSINESS_APPROVED -> "Congratulations Sipho!\n\nYour salon 'Legends Barbershop Sandton' has been verified by the StyleHub South Africa team and is now live to clients across Johannesburg. View your dashboard to manage bookings."
    TemplateType.REVIEW_RECEIVED -> "You received a new 5-star review from Leletu Kamana: 'Best taper fade and beard lineup in Sandton! Clean shop and on time.' Keep up the outstanding craftsmanship!"
}

private fun getButtonLabel(type: TemplateType): String = when (type) {
    TemplateType.WELCOME -> "Verify Email Address"
    TemplateType.SECURITY_2FA -> "Open Security Dashboard"
    TemplateType.PASSWORD_RESET -> "Reset Password"
    TemplateType.BOOKING_REQUEST -> "View Booking Status"
    TemplateType.BOOKING_CONFIRMED -> "Get Directions & Details"
    TemplateType.BOOKING_CANCELLED -> "Book Another Salon"
    TemplateType.BUSINESS_APPROVED -> "Open Owner Dashboard"
    TemplateType.REVIEW_RECEIVED -> "Respond to Review"
}

private fun getSmsBody(type: TemplateType): String = when (type) {
    TemplateType.WELCOME -> "StyleHub: Welcome Leletu! Use code 849201 to verify your South African mobile number. Valid for 10 mins."
    TemplateType.SECURITY_2FA -> "StyleHub Security: New sign-in detected in Johannesburg. If unauthorized, visit stylehub.co.za/security."
    TemplateType.PASSWORD_RESET -> "StyleHub: Password reset link sent to your email. Do not share your temporary OTP with anyone."
    TemplateType.BOOKING_REQUEST -> "StyleHub: Booking requested at Legends Barbershop Sandton for 26 Aug 11:00 (R220). Ref: BK-9821."
    TemplateType.BOOKING_CONFIRMED -> "StyleHub: Booking CONFIRMED! Legends Barbershop Sandton on 26 Aug 11:00. Pay R220 at salon. View: stylehub.co.za/app"
    TemplateType.BOOKING_CANCELLED -> "StyleHub: Your booking on 26 Aug at Legends Barbershop has been cancelled. Ref: BK-9821."
    TemplateType.BUSINESS_APPROVED -> "StyleHub Merchant: Congratulations! Your salon listing is now active and accepting client bookings."
    TemplateType.REVIEW_RECEIVED -> "StyleHub: You have a new 5-star review from Leletu! Check your merchant dashboard."
}
