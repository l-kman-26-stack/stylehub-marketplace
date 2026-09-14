package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.BusinessCategory
import com.example.data.local.entities.BusinessEntity
import com.example.ui.components.BusinessCard
import com.example.ui.components.LocationFilterSheet
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.PriceFilter
import com.example.ui.viewmodel.RatingFilter
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.StyleHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: StyleHubViewModel,
    filteredBusinesses: List<BusinessEntity>,
    filterState: FilterState,
    onViewBusiness: (Long) -> Unit,
    onBookBusiness: (BusinessEntity) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var showLocationFilterSheet by remember { mutableStateOf(false) }

    val cities = listOf("All Cities", "Johannesburg", "Cape Town", "Durban", "Pretoria")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen")
    ) {
        // Search & Filter Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search business, service, suburb...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary)
                    },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input"),
                    singleLine = true
                )

                // Quick Filter & Sort Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter Sheet Modal Trigger
                    FilterChip(
                        selected = filterState.category != null || filterState.priceFilter != PriceFilter.ALL || filterState.ratingFilter != RatingFilter.ANY || filterState.city != "All Cities" || filterState.onlyVerified,
                        onClick = { showFilterSheet = true },
                        label = { Text("Filters") },
                        leadingIcon = {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = GoldPrimary
                        ),
                        modifier = Modifier.testTag("open_filters_sheet_button")
                    )

                    // South Africa Location Hierarchical Trigger
                    FilterChip(
                        selected = filterState.province != "All Provinces" || filterState.city != "All Cities",
                        onClick = { showLocationFilterSheet = true },
                        label = {
                            Text(
                                if (filterState.suburb != "All Suburbs") filterState.suburb
                                else if (filterState.city != "All Cities") filterState.city
                                else if (filterState.province != "All Provinces") filterState.province
                                else "🇿🇦 SA Location"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldContainer,
                            selectedLabelColor = GoldPrimary
                        )
                    )

                    // City Filter Chips
                    cities.forEach { city ->
                        FilterChip(
                            selected = filterState.city == city,
                            onClick = { viewModel.updateCityFilter(city) },
                            label = { Text(city) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = CharcoalDark
                            )
                        )
                    }
                }

                // Category Quick Filter Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterState.category == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All Categories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = GoldPrimary
                            )
                        )
                    }
                    items(BusinessCategory.values().toList()) { cat ->
                        FilterChip(
                            selected = filterState.category == cat.displayName,
                            onClick = { viewModel.selectCategory(cat.displayName) },
                            label = { Text(cat.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = GoldPrimary
                            )
                        )
                    }
                }
            }
        }

        // Result Count & Sort Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredBusinesses.size} businesses found",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Sort Dropdown
            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box {
                TextButton(
                    onClick = { sortMenuExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = filterState.sortOption.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortOption.values().forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.label) },
                            onClick = {
                                viewModel.updateSortOption(opt)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Business List or Empty State
        if (filteredBusinesses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No businesses match your search",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Try adjusting your price, category, or city filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { viewModel.resetFilters() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredBusinesses) { biz ->
                    BusinessCard(
                        business = biz,
                        onClick = { onViewBusiness(biz.id) },
                        onBookClick = { onBookBusiness(biz) },
                        isSaved = false,
                        onToggleSave = { viewModel.toggleSave(biz.id) }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Filters
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter & Refine",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Reset All", color = CrimsonCancel)
                    }
                }

                HorizontalDivider()

                // Verified Only Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Verified Businesses Only", fontWeight = FontWeight.SemiBold)
                        Text("Show only vetted and approved South African barbers/salons", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = filterState.onlyVerified,
                        onCheckedChange = { viewModel.updateOnlyVerified(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldContainer)
                    )
                }

                // Price Filters
                Text(
                    text = "Price Range (ZAR)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceFilter.values().forEach { pf ->
                        FilterChip(
                            selected = filterState.priceFilter == pf,
                            onClick = { viewModel.updatePriceFilter(pf) },
                            label = { Text(pf.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = CharcoalDark
                            )
                        )
                    }
                }

                // Rating Filters
                Text(
                    text = "Minimum Rating",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RatingFilter.values().forEach { rf ->
                        FilterChip(
                            selected = filterState.ratingFilter == rf,
                            onClick = { viewModel.updateRatingFilter(rf) },
                            label = { Text(rf.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = CharcoalDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                ) {
                    Text("Apply Filters (${filteredBusinesses.size} Results)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // South Africa Location Filter Bottom Sheet
    LocationFilterSheet(
        isOpen = showLocationFilterSheet,
        currentProvince = filterState.province,
        currentCity = filterState.city,
        currentSuburb = filterState.suburb,
        onDismiss = { showLocationFilterSheet = false },
        onLocationSelected = { prov, city, sub ->
            viewModel.updateProvinceFilter(prov)
            viewModel.updateCityFilter(city)
            viewModel.updateSuburbFilter(sub)
        }
    )
}
