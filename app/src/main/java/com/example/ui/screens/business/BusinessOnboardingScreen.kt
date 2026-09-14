package com.example.ui.screens.business

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.locations.SouthAfricaLocations
import com.example.data.local.entities.BusinessCategory
import com.example.data.local.entities.ServiceEntity
import com.example.data.local.entities.TeamMemberEntity
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessOnboardingScreen(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        category: String,
        description: String,
        phone: String,
        email: String,
        whatsapp: String,
        address: String,
        suburb: String,
        city: String,
        province: String,
        postalCode: String,
        services: List<ServiceEntity>,
        teamMembers: List<TeamMemberEntity>
    ) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Form states
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BusinessCategory.BARBER.displayName) }
    var description by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+27 82 ") }
    var email by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("+27 82 ") }

    var address by remember { mutableStateOf("") }
    var suburb by remember { mutableStateOf("Sandton") }
    var city by remember { mutableStateOf("Johannesburg") }
    var province by remember { mutableStateOf("Gauteng") }
    var postalCode by remember { mutableStateOf("2196") }

    // Service 1
    var service1Name by remember { mutableStateOf("Standard Cut & Style") }
    var service1Price by remember { mutableStateOf("180") }
    var service1Duration by remember { mutableStateOf("30") }

    // Team 1
    var team1Name by remember { mutableStateOf("") }
    var team1Role by remember { mutableStateOf("Lead Stylist") }

    val provinces = listOf("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape")
    val cities = listOf("Johannesburg", "Cape Town", "Durban", "Pretoria", "Gqeberha", "Bloemfontein")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("List Your Business", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Step $step of 3 • South Africa Marketplace", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LinearProgressIndicator(
                progress = step / 3f,
                color = GoldPrimary,
                modifier = Modifier.fillMaxWidth().height(6.dp)
            )

            when (step) {
                1 -> {
                    // Step 1: Business Details
                    Text(
                        text = "1. Business Details",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Business Name *") },
                        placeholder = { Text("e.g. Crown Barbers Sandton") },
                        modifier = Modifier.fillMaxWidth().testTag("onboard_name_input")
                    )

                    Text("Category", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(BusinessCategory.BARBER, BusinessCategory.SALON, BusinessCategory.BRAIDER).forEach { cat ->
                            FilterChip(
                                selected = category == cat.displayName,
                                onClick = { category = cat.displayName },
                                label = { Text(cat.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = CharcoalDark
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Brand Story *") },
                        placeholder = { Text("Tell customers about your expertise and atmosphere...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Business Phone Number *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number (for direct chat) *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Business Email *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { if (name.isNotBlank()) step = 2 },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                    ) {
                        Text("Next: Location in South Africa", fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    // Step 2: Location
                    Text(
                        text = "2. Location in South Africa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address / Shopping Centre *") },
                        placeholder = { Text("e.g. 150 Rivonia Road, Shop 12") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Province Selection
                    Text("Province (All 9 Provinces) *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    var provinceDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = provinceDropdownExpanded,
                        onExpandedChange = { provinceDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = province,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = provinceDropdownExpanded,
                            onDismissRequest = { provinceDropdownExpanded = false }
                        ) {
                            SouthAfricaLocations.getAllProvinces().forEach { prov ->
                                DropdownMenuItem(
                                    text = { Text(prov) },
                                    onClick = {
                                        province = prov
                                        val citiesInProv = SouthAfricaLocations.getCitiesForProvince(prov)
                                        if (citiesInProv.isNotEmpty()) {
                                            city = citiesInProv.first()
                                            val suburbsInCity = SouthAfricaLocations.getSuburbsForCity(prov, city)
                                            if (suburbsInCity.isNotEmpty()) {
                                                suburb = suburbsInCity.first()
                                            }
                                        }
                                        provinceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // City Selection
                    Text("City / District *", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    var cityDropdownExpanded by remember { mutableStateOf(false) }
                    val currentCities = SouthAfricaLocations.getCitiesForProvince(province)
                    ExposedDropdownMenuBox(
                        expanded = cityDropdownExpanded,
                        onExpandedChange = { cityDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = cityDropdownExpanded,
                            onDismissRequest = { cityDropdownExpanded = false }
                        ) {
                            currentCities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        city = c
                                        val suburbsInCity = SouthAfricaLocations.getSuburbsForCity(province, c)
                                        if (suburbsInCity.isNotEmpty()) {
                                            suburb = suburbsInCity.first()
                                        }
                                        cityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = suburb,
                        onValueChange = { suburb = it },
                        label = { Text("Suburb / Area *") },
                        placeholder = { Text("e.g. Sandton, Rosebank, Menlyn, Umhlanga") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Postal Code") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = { step = 3 },
                            enabled = address.isNotBlank() && suburb.isNotBlank() && city.isNotBlank(),
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                        ) {
                            Text("Next: Services & Team", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // Step 3: Initial Services & Team Preview
                    Text(
                        text = "3. Initial Service & Specialist",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Primary Service", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            OutlinedTextField(
                                value = service1Name,
                                onValueChange = { service1Name = it },
                                label = { Text("Service Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = service1Price,
                                    onValueChange = { service1Price = it },
                                    label = { Text("Price (ZAR)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = service1Duration,
                                    onValueChange = { service1Duration = it },
                                    label = { Text("Mins") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Primary Specialist / Barber", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            OutlinedTextField(
                                value = team1Name,
                                onValueChange = { team1Name = it },
                                label = { Text("Specialist Name") },
                                placeholder = { Text("e.g. Thabo Ndlovu") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 2 },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                val srvList = listOf(
                                    ServiceEntity(
                                        businessId = 0,
                                        name = service1Name.ifBlank { "Standard Cut & Style" },
                                        category = "General",
                                        priceZar = service1Price.toDoubleOrNull() ?: 180.0,
                                        durationMinutes = service1Duration.toIntOrNull() ?: 30
                                    )
                                )
                                val teamList = if (team1Name.isNotBlank()) {
                                    listOf(
                                        TeamMemberEntity(
                                            businessId = 0,
                                            name = team1Name,
                                            role = team1Role,
                                            specialties = "All Styles",
                                            avatarInitials = team1Name.take(2).uppercase()
                                        )
                                    )
                                } else emptyList()

                                onSubmit(
                                    name,
                                    category,
                                    description.ifBlank { "Top-tier grooming studio." },
                                    phone,
                                    email,
                                    whatsapp,
                                    address.ifBlank { "Main Street" },
                                    suburb,
                                    city,
                                    province,
                                    postalCode,
                                    srvList,
                                    teamList
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("submit_listing_approval_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                        ) {
                            Text("Submit for Approval", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
