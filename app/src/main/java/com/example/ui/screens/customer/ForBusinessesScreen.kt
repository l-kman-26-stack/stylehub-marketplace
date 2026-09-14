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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForBusinessesScreen(
    onBack: () -> Unit,
    onStartListing: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "For Barbers & Salons",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Partner Growth & Merchant Solutions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("for_businesses_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onStartListing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = CharcoalDark
                        ),
                        modifier = Modifier.testTag("nav_list_biz_btn")
                    ) {
                        Text("List Business", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            // Hero Merchant Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CharcoalDark,
                                Color(0xFF1E2533)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "🇿🇦 SOUTH AFRICA MERCHANT NETWORK",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "Turn Foot Traffic into Loyal Regulars with StyleHub",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )

                    Text(
                        text = "Join over 250+ premier barbershops, afro hair salons, and beauty studios across Gauteng, Western Cape, KwaZulu-Natal and beyond.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = onStartListing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = CharcoalDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hero_start_listing_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Your Business Profile (Free)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            // Key Merchant Benefits
            Text(
                text = "Why Partner with StyleHub?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            MerchantBenefitCard(
                icon = Icons.Default.TrendingUp,
                title = "Expand Your Customer Reach",
                description = "Get discovered by clients actively searching for haircuts, braids, locs, and beauty treatments in your specific suburb and city."
            )

            MerchantBenefitCard(
                icon = Icons.Default.CalendarToday,
                title = "Effortless Appointment Management",
                description = "Receive instant booking inquiries with preferred dates and times. Confirm or manage appointments from your dedicated Business Owner dashboard."
            )

            MerchantBenefitCard(
                icon = Icons.Default.Verified,
                title = "Verified Partner Credibility",
                description = "Stand out with an official gold verification badge, authentic client reviews, and transparent pricing in South African Rand (ZAR)."
            )

            MerchantBenefitCard(
                icon = Icons.Default.Collections,
                title = "Digital Portfolio & Team Showcase",
                description = "Showcase your barbers and stylists, photo gallery, specialized services, and operating hours."
            )

            // Transparent Pricing / Tiers (Honest MVP vs Future Pro)
            Text(
                text = "Transparent Partner Pricing",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Free MVP Tier
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "COMMUNITY MVP",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text("Free Forever", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("R0 / month", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("0% commission on direct bookings", style = MaterialTheme.typography.bodySmall)

                        HorizontalDivider()

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            BenefitBullet("Full Business Listing")
                            BenefitBullet("Service Menu & Pricing")
                            BenefitBullet("Appointment Inquiries")
                            BenefitBullet("Client Reviews & Ratings")
                            BenefitBullet("Owner Dashboard Access")
                        }
                    }
                }

                // Future Pro Tier (Roadmap)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "UPCOMING ROADMAP",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text("StyleHub Pro", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Coming Soon", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Advanced automated tools", style = MaterialTheme.typography.bodySmall)

                        HorizontalDivider()

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            BenefitBullet("Automated SMS Reminders")
                            BenefitBullet("Online Deposit Settlement")
                            BenefitBullet("Featured Top Search Placement")
                            BenefitBullet("Multi-branch Analytics")
                        }
                    }
                }
            }

            // 3-Step Quick Guide
            Text(
                text = "How to Get Started in 3 Minutes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            StepGuideItem(
                number = "1",
                title = "Fill Out Studio Profile",
                description = "Enter your business name, address in South Africa, contact details, and categories."
            )

            StepGuideItem(
                number = "2",
                title = "Add Services & Team",
                description = "Set service names, duration (mins), and transparent South African Rand (ZAR) prices."
            )

            StepGuideItem(
                number = "3",
                title = "Start Receiving Clients",
                description = "Get featured on StyleHub discovery immediately upon submitting your listing."
            )

            // Final Action Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Ready to Grow Your Salon?",
                        color = CharcoalDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Join South Africa's premier hair & beauty digital marketplace today.",
                        color = CharcoalDark.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onStartListing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalDark,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("bottom_start_listing_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("List Your Business Now", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MerchantBenefitCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun BenefitBullet(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StepGuideItem(number: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CharcoalDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = GoldPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
