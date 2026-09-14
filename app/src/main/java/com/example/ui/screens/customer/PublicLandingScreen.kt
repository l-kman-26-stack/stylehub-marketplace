package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BusinessEntity
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.PlatformSettings
import com.example.ui.viewmodel.PublicViewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicLandingScreen(
    featuredBusinesses: List<BusinessEntity>,
    platformSettings: PlatformSettings,
    onNavigatePublic: (PublicViewMode) -> Unit,
    onEnterApp: () -> Unit,
    onOpenAuthModal: (tab: Int) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectBusiness: (Long) -> Unit,
    onOpenLocationFilter: () -> Unit
) {
    var searchInput by remember { mutableStateOf("") }
    var newsletterEmail by remember { mutableStateOf("") }
    var isNewsletterSubscribed by remember { mutableStateOf(false) }
    var howItWorksTab by remember { mutableStateOf(0) } // 0 = Clients, 1 = Salons

    Scaffold(
        topBar = {
            PublicCorporateNavBar(
                onNavigatePublic = onNavigatePublic,
                onEnterApp = onEnterApp,
                onOpenAuthModal = onOpenAuthModal
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("public_landing_screen")
        ) {
            // Maintenance Mode Notice (if active)
            if (platformSettings.maintenanceMode) {
                Surface(
                    color = Color(0xFFE65100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scheduled Platform Optimization in progress. All bookings remain securely processed.",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 1. Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CharcoalDark,
                                Color(0xFF161B26),
                                Color(0xFF10141D)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldPrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🇿🇦", fontSize = 13.sp)
                            Text(
                                text = "SOUTH AFRICA'S PREMIER GROOMING MARKETPLACE",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Text(
                        text = "Find Your Style.\nFind Your Professional.",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 38.sp
                    )

                    Text(
                        text = "Connect with top-rated barbers, braiders, loc stylists, and salons across all 9 provinces with transparent pricing in South African Rand (ZAR).",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Hero Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onEnterApp,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = CharcoalDark
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_find_barber_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Find a Salon", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onNavigatePublic(PublicViewMode.FOR_BUSINESSES) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.4f))
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_list_business_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("List Business", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Fast Discovery Search Box in Hero
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = searchInput,
                                onValueChange = { searchInput = it },
                                placeholder = { Text("What service or salon are you looking for?") },
                                leadingIcon = { Icon(Icons.Default.ContentCut, contentDescription = null, tint = GoldPrimary) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onOpenLocationFilter,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("9 Provinces", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = onEnterApp,
                                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalDark, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Search Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Live Platform Metrics & Trust Bar
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricItem("250+", "Vetted Studios")
                    VerticalDivider(modifier = Modifier.height(30.dp))
                    MetricItem("9", "SA Provinces")
                    VerticalDivider(modifier = Modifier.height(30.dp))
                    MetricItem("R0", "Booking Fees")
                    VerticalDivider(modifier = Modifier.height(30.dp))
                    MetricItem("100%", "POPIA Safe")
                }
            }

            // 3. Popular Categories Section
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
                    Column {
                        Text(
                            text = "Popular Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Specialized hair, beard & beauty treatments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onEnterApp) {
                        Text("View All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryPillCard("Barbershop", Icons.Default.ContentCut, "Fades, Tapers & Beard Lines") {
                        onSelectCategory("Barbershop")
                        onEnterApp()
                    }
                    CategoryPillCard("Hair Salon", Icons.Default.Face, "Knotless Braids, Weaves & Blowouts") {
                        onSelectCategory("Hair Salon")
                        onEnterApp()
                    }
                    CategoryPillCard("Loc Studio", Icons.Default.Spa, "Retwist, Styling & Detox") {
                        onSelectCategory("Loc Studio")
                        onEnterApp()
                    }
                    CategoryPillCard("Nail & Beauty", Icons.Default.AutoAwesome, "Acrylics, Gel & Manicures") {
                        onSelectCategory("Nail & Beauty")
                        onEnterApp()
                    }
                }
            }

            // 4. Featured Studios Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Featured Verified Salons",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hand-picked studios with top client reviews",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onEnterApp) {
                        Text("Explore All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                featuredBusinesses.take(3).forEach { biz ->
                    FeaturedStudioCard(biz = biz, onClick = { onSelectBusiness(biz.id); onEnterApp() })
                }
            }

            // 5. How StyleHub Works (Dual Client / Salon Switcher)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "How StyleHub Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = howItWorksTab == 0,
                        onClick = { howItWorksTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("For Clients & Customers")
                    }
                    SegmentedButton(
                        selected = howItWorksTab == 1,
                        onClick = { howItWorksTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("For Salons & Barbers")
                    }
                }

                if (howItWorksTab == 0) {
                    HowItWorksStep("1", "Discover Nearby Pros", "Browse vetted barbers and salons across Sandton, Cape Town, Durban and your local area.")
                    HowItWorksStep("2", "Select Service & Slot", "Pick your treatment with 100% transparent pricing in ZAR, choose your preferred stylist and time.")
                    HowItWorksStep("3", "Show Up & Pay at Salon", "Get instant booking confirmation. Settle your payment seamlessly at the studio via Card or Cash.")
                } else {
                    HowItWorksStep("1", "List Your Business (Free)", "Create your business profile, service menu, pricing, and operating hours in under 3 minutes.")
                    HowItWorksStep("2", "Get Verified by StyleHub", "Receive the official gold verified partner badge after physical studio check.")
                    HowItWorksStep("3", "Grow Your Appointments", "Receive direct booking inquiries from thousands of local South African customers.")
                }
            }

            // 6. Testimonials Section (Transparently labelled)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "What South Africans Are Saying",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real feedback from verified clients and salon partners",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TestimonialCard(
                    quote = "Finding a barber in Sandton who understands a clean taper fade and beard sculpting used to be trial and error. StyleHub made booking seamless.",
                    author = "Leletu K.",
                    location = "Sandton, Gauteng • Verified Client",
                    rating = 5
                )

                TestimonialCard(
                    quote = "StyleHub has brought our salon over 40 new monthly clients for knotless braids and wash days without paying hefty commissions.",
                    author = "Thandiwe N.",
                    location = "Rosebank, JHB • Salon Owner Partner",
                    rating = 5
                )
            }

            // 7. Interactive FAQs Accordion
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigatePublic(PublicViewMode.HELP_CENTRE) }) {
                        Text("Help Centre", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                FaqAccordionItem("Is StyleHub free for customers?", "Yes, StyleHub is 100% free for clients. You only pay for the haircut or styling service directly to your stylist at the salon.")
                FaqAccordionItem("How do salons get verified?", "Our South African operations team reviews physical salon premises, verified phone numbers, and customer reviews before granting the official gold verified badge.")
                FaqAccordionItem("Which areas in South Africa are supported?", "StyleHub supports all 9 provinces including Gauteng, Western Cape, KwaZulu-Natal, Eastern Cape, Free State, Limpopo, Mpumalanga, North West, and Northern Cape.")
            }

            // 8. Newsletter Signup Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Stay Updated on South African Hair Trends",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Subscribe for exclusive stylist showcases, hair care guides, and partner promotions.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )

                    if (isNewsletterSubscribed) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎉 Thank you for subscribing! Check your inbox soon.",
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = newsletterEmail,
                            onValueChange = { newsletterEmail = it },
                            placeholder = { Text("Enter your email address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = {
                                if (newsletterEmail.contains("@")) {
                                    isNewsletterSubscribed = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Subscribe (No Spam)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 9. Comprehensive Corporate Footer
            PublicCorporateFooter(
                platformSettings = platformSettings,
                onNavigatePublic = onNavigatePublic,
                onEnterApp = onEnterApp,
                onOpenAuthModal = onOpenAuthModal
            )
        }
    }
}

@Composable
private fun PublicCorporateNavBar(
    onNavigatePublic: (PublicViewMode) -> Unit,
    onEnterApp: () -> Unit,
    onOpenAuthModal: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // StyleHub Monogram / Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onNavigatePublic(PublicViewMode.LANDING) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CharcoalDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "StyleHub Logo",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row {
                        Text("STYLE", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("HUB", fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldPrimary)
                    }
                }

                // Header Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onOpenAuthModal(0) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("nav_signin_btn")
                    ) {
                        Text("Sign In", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onEnterApp,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = CharcoalDark
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("nav_explore_btn")
                    ) {
                        Text("Explore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Secondary Quick Links Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTextLink("Explore", onClick = onEnterApp)
                NavTextLink("For Businesses", onClick = { onNavigatePublic(PublicViewMode.FOR_BUSINESSES) })
                NavTextLink("About StyleHub", onClick = { onNavigatePublic(PublicViewMode.ABOUT_US) })
                NavTextLink("Help Centre", onClick = { onNavigatePublic(PublicViewMode.HELP_CENTRE) })
                NavTextLink("POPIA & Legal", onClick = { onNavigatePublic(PublicViewMode.LEGAL_TERMS) })
                NavTextLink("Notification Sandbox", onClick = { onNavigatePublic(PublicViewMode.TRANSACTIONAL_TEMPLATES) })
            }
        }
    }
}

@Composable
private fun NavTextLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun MetricItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldPrimary)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CategoryPillCard(title: String, icon: ImageVector, description: String, onClick: () -> Unit) {
    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
            }
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun FeaturedStudioCard(biz: BusinessEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CharcoalDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ContentCut, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = biz.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    if (biz.isVerified) {
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Text(text = "${biz.category} • ${biz.suburb}, ${biz.city}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    Text(text = "${biz.rating} (${biz.reviewCount})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "• From R${biz.minPriceZar.toInt()}", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HowItWorksStep(step: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CharcoalDark),
            contentAlignment = Alignment.Center
        ) {
            Text(step, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TestimonialCard(quote: String, author: String, location: String, rating: Int) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row {
                repeat(rating) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Text(text = "\"$quote\"", style = MaterialTheme.typography.bodyMedium, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            Column {
                Text(text = author, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(text = location, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FaqAccordionItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = question, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = GoldPrimary)
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = answer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun PublicCorporateFooter(
    platformSettings: PlatformSettings,
    onNavigatePublic: (PublicViewMode) -> Unit,
    onEnterApp: () -> Unit,
    onOpenAuthModal: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharcoalDark)
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Brand & Tagline
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, tint = CharcoalDark, modifier = Modifier.size(18.dp))
                }
                Text("STYLE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("HUB", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }

            Text(
                text = "South Africa's premier marketplace connecting clients with vetted barbers, afro salons, braiders, and beauty professionals.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Navigation Links Grouping
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    Text("MARKETPLACE", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    FooterLink("Explore Salons", onEnterApp)
                    FooterLink("List Your Business", { onNavigatePublic(PublicViewMode.FOR_BUSINESSES) })
                    FooterLink("For Barbers", { onNavigatePublic(PublicViewMode.FOR_BUSINESSES) })
                    FooterLink("Sign In / Register", { onOpenAuthModal(0) })
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    Text("COMPANY", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    FooterLink("About StyleHub", { onNavigatePublic(PublicViewMode.ABOUT_US) })
                    FooterLink("Help Centre", { onNavigatePublic(PublicViewMode.HELP_CENTRE) })
                    FooterLink("Contact Desk", { onNavigatePublic(PublicViewMode.HELP_CENTRE) })
                    FooterLink("Email Sandbox", { onNavigatePublic(PublicViewMode.TRANSACTIONAL_TEMPLATES) })
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    Text("LEGAL & POPIA", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    FooterLink("Terms of Service", { onNavigatePublic(PublicViewMode.LEGAL_TERMS) })
                    FooterLink("POPIA Privacy", { onNavigatePublic(PublicViewMode.LEGAL_TERMS) })
                    FooterLink("Partner Terms", { onNavigatePublic(PublicViewMode.LEGAL_TERMS) })
                    FooterLink("Cookie Policy", { onNavigatePublic(PublicViewMode.LEGAL_TERMS) })
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Official Legal / Corporate Registration Disclosure
            Text(
                text = "Corporate Entity: ${platformSettings.registrationNumberPlaceholder}\nRegistered Address: ${platformSettings.registeredAddressPlaceholder}\nPOPIA Act 4 of 2013 Compliant. © 2026 StyleHub Technologies. All rights reserved.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 12.sp,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
