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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    companyRegPlaceholder: String = "StyleHub Technologies (Pty) Ltd [Reg: 2026/012948/07 (Pending Final Verification)]",
    supportEmail: String = "support@stylehub.co.za",
    supportPhone: String = "+27 10 824 9000",
    onBack: () -> Unit,
    onExplore: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "About StyleHub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Our Story, Mission & South African Vision",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_btn")) {
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Brand Mission Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CharcoalDark,
                                Color(0xFF19202E)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "🇿🇦 CELEBRATING SOUTH AFRICAN STYLE",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "Find Your Style. Find Your Professional.",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )

                    Text(
                        text = "StyleHub is South Africa's premier digital marketplace connecting clients with vetted barbers, afro hair stylists, braiders, nail technicians, and beauty studios.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            // The Problem & Our Solution
            Text(
                text = "Bridging the Gap in South Africa's Hair & Beauty Sector",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "For decades, booking a reliable haircut, fresh taper fade, knotless braids, or locs maintenance in South Africa meant relying on word-of-mouth, guessing prices, or waiting in long Saturday queues. Meanwhile, talented barbers and braiders lacked digital booking tools to showcase their craftsmanship and manage their client schedules efficiently.\n\nStyleHub transforms this by giving every stylist a professional digital storefront with transparent pricing in South African Rand (ZAR), verified customer reviews, instant appointment booking, and enterprise-level account security.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )

            // Our Core Pillars
            Text(
                text = "Our Guiding Pillars",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            PillarCard(
                icon = Icons.Default.Visibility,
                title = "100% Transparent Pricing",
                description = "No hidden salon fees or surprise rate increases. Every listed service displays exact South African Rand (ZAR) prices before you confirm."
            )

            PillarCard(
                icon = Icons.Default.VerifiedUser,
                title = "Verified Quality & Trust",
                description = "Our local operations team reviews business authenticity and physical studio premises, giving users peace of mind."
            )

            PillarCard(
                icon = Icons.Default.Handshake,
                title = "Empowering Local Entrepreneurs",
                description = "From township salons in Soweto and Umlazi to luxury studios in Sandton and Camps Bay, we empower local business owners with world-class digital tools."
            )

            PillarCard(
                icon = Icons.Default.Security,
                title = "POPIA & Account Privacy",
                description = "Strict adherence to South Africa's Protection of Personal Information Act with optional TOTP Two-Factor Authentication and audit logs."
            )

            // Corporate Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Company & Legal Identification",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    HorizontalDivider()

                    Text(
                        text = "Corporate Entity: $companyRegPlaceholder",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Registered Address: 138 West Street, Sandton Central, Johannesburg, 2196, South Africa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Support: $supportEmail • Phone: $supportPhone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Information Officer (POPIA): privacy@stylehub.co.za",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onExplore,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = CharcoalDark
                ),
                modifier = Modifier.fillMaxWidth().testTag("about_explore_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Explore Salons & Barbers", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PillarCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
