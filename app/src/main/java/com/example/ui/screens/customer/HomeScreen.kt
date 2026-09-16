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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entities.BusinessCategory
import com.example.data.local.entities.BusinessEntity
import com.example.ui.components.BusinessCard
import com.example.ui.components.LocationFilterSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.StyleHubViewModel

@Composable
fun HomeScreen(
    viewModel: StyleHubViewModel,
    approvedBusinesses: List<BusinessEntity>,
    filteredBusinesses: List<BusinessEntity> = emptyList(),
    filterState: FilterState = FilterState(),
    onViewBusiness: (Long) -> Unit,
    onBookBusiness: (BusinessEntity) -> Unit,
    onNavigateTab: (CustomerTab) -> Unit,
    onSwitchToOwner: () -> Unit
) {
    val currentFilterState by viewModel.filterState.collectAsStateWithLifecycle()
    val currentFilteredBusinesses by viewModel.filteredBusinesses.collectAsStateWithLifecycle()
    val allServices by viewModel.allServices.collectAsStateWithLifecycle()

    var showLocationFilterSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isSearchActive = currentFilterState.query.isNotBlank() ||
            currentFilterState.province != "All Provinces" ||
            currentFilterState.city != "All Cities" ||
            currentFilterState.suburb != "All Suburbs" ||
            currentFilterState.category != null

    val servicesByBusiness = remember(allServices) {
        allServices.groupBy { it.businessId }
    }

    val featuredBusinesses = approvedBusinesses.filter { it.isFeatured }
    val topRatedBusinesses = approvedBusinesses.sortedByDescending { it.rating }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_home_screen")
    ) {
        // --- 1. TOP SEARCH & LOCATION BAR (STICKY AT TOP OF MAIN SCREEN) ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Search Input Row
                OutlinedTextField(
                    value = currentFilterState.query,
                    onValueChange = { newQuery ->
                        viewModel.updateSearchQuery(newQuery)
                    },
                    placeholder = {
                        Text(
                            text = "Search service (Fade, Braids...) or location...",
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            if (currentFilterState.query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.clearSearch()
                                        focusManager.clearFocus()
                                    },
                                    modifier = Modifier.size(32.dp).testTag("home_search_clear_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showLocationFilterSheet = true },
                                modifier = Modifier.size(34.dp).testTag("home_location_filter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Filter by Location",
                                    tint = if (currentFilterState.city != "All Cities" || currentFilterState.province != "All Provinces") GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar_input")
                )

                // Quick Suggestion Filters: Services & Locations Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    // Location indicator & selector pill
                    item {
                        val activeLocationLabel = when {
                            currentFilterState.suburb != "All Suburbs" -> currentFilterState.suburb
                            currentFilterState.city != "All Cities" -> currentFilterState.city
                            currentFilterState.province != "All Provinces" -> currentFilterState.province
                            else -> "All SA"
                        }
                        val isLocationActive = activeLocationLabel != "All SA"

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isLocationActive) GoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isLocationActive) GoldPrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .clickable { showLocationFilterSheet = true }
                                .testTag("home_location_selector_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isLocationActive) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = activeLocationLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isLocationActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isLocationActive) GoldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isLocationActive) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear location",
                                        tint = GoldPrimary,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable {
                                                viewModel.updateProvinceFilter("All Provinces")
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Popular Services Quick Chips
                    val serviceChips = listOf(
                        "Fade",
                        "Braids",
                        "Haircut",
                        "Beard",
                        "Locs",
                        "Silk Press"
                    )

                    items(serviceChips) { serviceKeyword ->
                        val isSelected = currentFilterState.query.contains(serviceKeyword, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    viewModel.clearSearch()
                                } else {
                                    viewModel.updateSearchQuery(serviceKeyword)
                                }
                            },
                            label = {
                                Text(
                                    text = when (serviceKeyword) {
                                        "Fade" -> "💈 Fade"
                                        "Braids" -> "✨ Braids"
                                        "Haircut" -> "✂️ Haircut"
                                        "Beard" -> "🧔 Beard Trim"
                                        "Locs" -> "💆 Locs"
                                        "Silk Press" -> "💇 Silk Press"
                                        else -> serviceKeyword
                                    },
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = CharcoalDark,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("service_chip_${serviceKeyword}")
                        )
                    }

                    // Popular South Africa Cities Chips
                    val popularCities = listOf("Sandton", "Cape Town", "Durban", "Pretoria", "Rosebank")
                    items(popularCities) { city ->
                        val isCitySelected = currentFilterState.city.equals(city, ignoreCase = true) ||
                                currentFilterState.query.equals(city, ignoreCase = true)

                        FilterChip(
                            selected = isCitySelected,
                            onClick = {
                                if (isCitySelected) {
                                    viewModel.updateCityFilter("All Cities")
                                    if (currentFilterState.query.equals(city, ignoreCase = true)) {
                                        viewModel.clearSearch()
                                    }
                                } else {
                                    viewModel.updateSearchQuery(city)
                                }
                            },
                            label = { Text("📍 $city", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = CharcoalDark,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("city_chip_${city}")
                        )
                    }
                }
            }
        }

        // --- 2. CONTENT AREA (LIVE SEARCH RESULTS OR RICH HOME SECTIONS) ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isSearchActive) {
                // SEARCH & LOCATION RESULTS VIEW
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Search & Filter Results",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = buildString {
                                        append("${currentFilteredBusinesses.size} business${if (currentFilteredBusinesses.size == 1) "" else "es"} found")
                                        if (currentFilterState.query.isNotBlank()) {
                                            append(" for \"${currentFilterState.query}\"")
                                        }
                                        if (currentFilterState.city != "All Cities") {
                                            append(" in ${currentFilterState.city}")
                                        }
                                        if (currentFilterState.category != null) {
                                            append(" (${currentFilterState.category})")
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.clearSearch()
                                    viewModel.resetFilters()
                                },
                                modifier = Modifier.testTag("clear_search_results_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear All", color = GoldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (currentFilteredBusinesses.isEmpty()) {
                    // Empty State for Search
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SearchOff,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = "No Barbers or Salons Found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "No studios matched your search criteria. Try searching for popular services (e.g., Fade, Braids, Locs, Beard) or locations like Sandton, Cape Town, Rosebank, Durban or Pretoria.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    viewModel.clearSearch()
                                    viewModel.resetFilters()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reset Filters & View All", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Render Filtered Businesses
                    items(currentFilteredBusinesses) { biz ->
                        val bizServices = servicesByBusiness[biz.id].orEmpty()
                        val matchedServices = if (currentFilterState.query.isNotBlank()) {
                            bizServices.filter { s ->
                                s.name.contains(currentFilterState.query, ignoreCase = true) ||
                                s.category.contains(currentFilterState.query, ignoreCase = true) ||
                                s.description.contains(currentFilterState.query, ignoreCase = true)
                            }
                        } else {
                            emptyList()
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BusinessCard(
                                business = biz,
                                onClick = { onViewBusiness(biz.id) },
                                onBookClick = { onBookBusiness(biz) }
                            )

                            // Highlight matched services if any
                            if (matchedServices.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                                            text = "Matching service: ${matchedServices.joinToString(", ") { "${it.name} (R${it.priceZar.toInt()})" }}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // NORMAL HOME SCREEN SECTIONS (When search is inactive)

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

                            // Hero Search Prompt (Syncs with top search bar)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateSearchQuery("Fade")
                                    }
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
                                        text = "Tap to search popular fades, braids & salons...",
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

                // 3. AI Stylist & Concierge Highlight Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onNavigateTab(CustomerTab.AI_STYLIST) }
                            .testTag("home_ai_stylist_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(GoldPrimary, GoldSecondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Stylist",
                                    tint = CharcoalDark,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Ask StyleHub AI Concierge",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF1E3A8A).copy(alpha = 0.3f)
                                    ) {
                                        Text(
                                            text = "📍 Maps Data",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF93C5FD),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Get personalized hair advice, pricing in ZAR, and find nearby studios in SA.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open AI Chat",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 4. Featured Businesses
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

                // 5. Popular Services Grid (Filter directly on tap)
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
                            val queryKeyword = when {
                                title.contains("Fade") -> "Fade"
                                title.contains("Braids") -> "Braids"
                                title.contains("Beard") -> "Beard"
                                title.contains("Silk Press") -> "Silk Press"
                                else -> title.split(" ").first()
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateSearchQuery(queryKeyword)
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

                // 6. Highly Rated Grooming Studios in South Africa
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

                // 7. Business Owner Onboarding Call To Action Banner
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
                                Text("List Your Business", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Integrated South Africa Location Filter Bottom Sheet
    LocationFilterSheet(
        isOpen = showLocationFilterSheet,
        currentProvince = currentFilterState.province,
        currentCity = currentFilterState.city,
        currentSuburb = currentFilterState.suburb,
        onDismiss = { showLocationFilterSheet = false },
        onLocationSelected = { prov, city, suburb ->
            viewModel.updateProvinceFilter(prov)
            viewModel.updateCityFilter(city)
            viewModel.updateSuburbFilter(suburb)
            showLocationFilterSheet = false
        }
    )
}
