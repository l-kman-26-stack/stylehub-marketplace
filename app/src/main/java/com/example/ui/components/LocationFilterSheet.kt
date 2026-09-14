package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.locations.SouthAfricaLocations
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFilterSheet(
    isOpen: Boolean,
    currentProvince: String,
    currentCity: String,
    currentSuburb: String,
    onDismiss: () -> Unit,
    onLocationSelected: (province: String, city: String, suburb: String) -> Unit
) {
    if (!isOpen) return

    var selectedProvince by remember(currentProvince) { mutableStateOf(currentProvince) }
    var selectedCity by remember(currentCity) { mutableStateOf(currentCity) }
    var selectedSuburb by remember(currentSuburb) { mutableStateOf(currentSuburb) }
    var searchQuery by remember { mutableStateOf("") }

    val availableProvinces = listOf("All Provinces") + SouthAfricaLocations.getAllProvinces()
    val availableCities = if (selectedProvince == "All Provinces") {
        listOf("All Cities")
    } else {
        listOf("All Cities") + SouthAfricaLocations.getCitiesForProvince(selectedProvince)
    }
    val availableSuburbs = if (selectedCity == "All Cities" || selectedProvince == "All Provinces") {
        listOf("All Suburbs")
    } else {
        listOf("All Suburbs") + SouthAfricaLocations.getSuburbsForCity(selectedProvince, selectedCity)
    }

    val searchResults = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            SouthAfricaLocations.searchLocations(searchQuery)
        } else {
            emptyList()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("location_filter_sheet")
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
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldPrimary
                    )
                    Text(
                        text = "South Africa Locations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Filter verified barbers & salons across all 9 provinces",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_search_input"),
                placeholder = { Text("Search suburb, town or city (e.g. Sandton, Umhlanga, Camps Bay)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                Text(
                    text = "Search Suggestions (${searchResults.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (searchResults.isEmpty()) {
                    Text(
                        text = "No matching South African locations found. Try searching a major city or province.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                    ) {
                        items(searchResults) { (prov, city, suburb) ->
                            ListItem(
                                headlineContent = { Text(suburb, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("$city, $prov") },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = GoldPrimary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLocationSelected(prov, city, suburb)
                                        onDismiss()
                                    }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                // Hierarchical Dropdowns / Selectors
                Text(
                    text = "1. Select Province (All 9 Provinces)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                var provinceDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = provinceDropdownExpanded,
                    onExpandedChange = { provinceDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProvince,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = provinceDropdownExpanded,
                        onDismissRequest = { provinceDropdownExpanded = false }
                    ) {
                        availableProvinces.forEach { prov ->
                            DropdownMenuItem(
                                text = { Text(prov) },
                                onClick = {
                                    selectedProvince = prov
                                    selectedCity = "All Cities"
                                    selectedSuburb = "All Suburbs"
                                    provinceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "2. Select City or District",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                var cityDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = cityDropdownExpanded,
                    onExpandedChange = { if (selectedProvince != "All Provinces") cityDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedProvince != "All Provinces",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false }
                    ) {
                        availableCities.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    selectedCity = c
                                    selectedSuburb = "All Suburbs"
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "3. Select Suburb or Area",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                var suburbDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = suburbDropdownExpanded,
                    onExpandedChange = { if (selectedCity != "All Cities") suburbDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSuburb,
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedCity != "All Cities",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = suburbDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = suburbDropdownExpanded,
                        onDismissRequest = { suburbDropdownExpanded = false }
                    ) {
                        availableSuburbs.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    selectedSuburb = s
                                    suburbDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onLocationSelected("All Provinces", "All Cities", "All Suburbs")
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Location")
                }
                Button(
                    onClick = {
                        onLocationSelected(selectedProvince, selectedCity, selectedSuburb)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_location_filter_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                ) {
                    Text("Apply Filter", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
