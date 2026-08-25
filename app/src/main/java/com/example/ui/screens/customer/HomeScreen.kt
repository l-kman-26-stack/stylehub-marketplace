package com.example.ui.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.BusinessCategory
import com.example.data.local.entities.BusinessEntity
import com.example.data.local.entities.UserRole
import com.example.ui.components.BusinessCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.StyleHubViewModel

@Composable
fun HomeScreen(
    viewModel: StyleHubViewModel,
    approvedBusinesses: List<BusinessEntity>,
    onViewBusiness: (Long) -> Unit,
    onBookBusiness: (BusinessEntity) -> Unit,
    onNavigateTab: (CustomerTab) -> Unit,
    onSwitchToOwner: () -> Unit
) {
    val featuredBusinesses = approvedBusinesses.filter { it.isFeatured }
    val topRatedBusinesses = approvedBusinesses.sortedByDescending { it.rating }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Hero Discovery Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_barber),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark atmospheric gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    CharcoalDark.copy(alpha = 0.4f),
                                    CharcoalDark.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GoldPrimary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "SOUTH AFRICA'S GROOMING MARKETPLACE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Find a barber or salon near you.",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = "Discover top barbers, braiders & hairstylists in JHB, CPT, DBN & PTA.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }

                    // Search Trigger Bar
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateTab(CustomerTab.EXPLORE) }
                            .testTag("home_search_trigger")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GoldPrimary
                            )
                            Text(
                                text = "Search barbers, braids, fade, salon...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. Categories Horizontal Carousel
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Explore Categories",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val categories = listOf(
                        Triple(BusinessCategory.BARBER.displayName, Icons.Default.ContentCut, "Barbers"),
                        Triple(BusinessCategory.BRAIDER.displayName, Icons.Default.Face, "Braids & Locs"),
                        Triple(BusinessCategory.SALON.displayName, Icons.Default.Spa, "Hair Salons"),
                        Triple(BusinessCategory.GROOMING.displayName, Icons.Default.SelfImprovement, "Grooming"),
                        Triple(BusinessCategory.HAIRSTYLIST.displayName, Icons.Default.Brush, "Stylists"),
                        Triple(BusinessCategory.MOBILE_BARBER.displayName, Icons.Default.DirectionsCar, "Mobile Barbers")
                    )

                    items(categories) { (categoryName, icon, label) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectCategory(categoryName)
                                    onNavigateTab(CustomerTab.EXPLORE)
                                }
                                .testTag("category_pill_${categoryName}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Featured Businesses
        if (featuredBusinesses.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Featured Businesses",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { onNavigateTab(CustomerTab.EXPLORE) }) {
                            Text("See All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(featuredBusinesses) { biz ->
                            BusinessCard(
                                business = biz,
                                onClick = { onViewBusiness(biz.id) },
                                onBookClick = { onBookBusiness(biz) },
                                modifier = Modifier.width(300.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Popular Services Grid / Quick Select
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Popular Services in Demand",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                val popularServices = listOf(
                    Triple("Executive Skin Fade", "Precision fades & styling", "From R180"),
                    Triple("Knotless Box Braids", "Neat, pain-free mid-back braids", "From R550"),
                    Triple("Beard Sculpt & Razor Line", "Hot towel beard shaping", "From R150"),
                    Triple("Luxury Silk Press", "Deep wash, press & trim", "From R420")
                )

                popularServices.forEach { (title, subtitle, price) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateSearchQuery(title.split(" ").first())
                                onNavigateTab(CustomerTab.EXPLORE)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(text = price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GoldPrimary)
                        }
                    }
                }
            }
        }

        // 5. Highly Rated Grooming Studios in South Africa
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Rated in South Africa",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { onNavigateTab(CustomerTab.EXPLORE) }) {
                        Text("View All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                topRatedBusinesses.take(3).forEach { biz ->
                    BusinessCard(
                        business = biz,
                        onClick = { onViewBusiness(biz.id) },
                        onBookClick = { onBookBusiness(biz) }
                    )
                }
            }
        }

        // 6. Business Owner Onboarding Call To Action Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CharcoalSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = GoldPrimary)
                        Text(
                            text = "Are you a Barber or Salon Owner?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "List your business on StyleHub, showcase your services and portfolio, and start receiving appointment bookings directly.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )

                    Button(
                        onClick = onSwitchToOwner,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = CharcoalDark
                        ),
                        modifier = Modifier.testTag("cta_list_business_button")
                    ) {
                        Text("List Your Business (Free MVP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
