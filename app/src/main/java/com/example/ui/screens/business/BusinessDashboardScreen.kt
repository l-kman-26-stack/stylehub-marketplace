package com.example.ui.screens.business

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StyleHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDashboardScreen(
    viewModel: StyleHubViewModel,
    business: BusinessEntity?,
    appointments: List<AppointmentEntity>,
    services: List<ServiceEntity>,
    team: List<TeamMemberEntity>,
    onOpenOnboarding: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Bookings", "Services", "Team", "Profile & Hours")

    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddTeamDialog by remember { mutableStateOf(false) }

    if (business == null) {
        // Empty state when owner has no listing yet
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = GoldPrimary
                )
                Text(
                    text = "Welcome to StyleHub Business",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Create your professional salon or barbershop listing to start receiving online bookings from customers in South Africa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onOpenOnboarding,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                    modifier = Modifier.testTag("create_business_listing_button")
                ) {
                    Text("Create Business Listing", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("business_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Business Header & Status
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = business.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${business.category} • ${business.suburb}, ${business.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Status Badge
                        val (statusColor, textColor) = when (business.status) {
                            BusinessStatus.APPROVED.name -> EmeraldVerified to Color.White
                            BusinessStatus.PENDING_APPROVAL.name -> GoldPrimary to CharcoalDark
                            else -> CrimsonCancel to Color.White
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor
                        ) {
                            Text(
                                text = when (business.status) {
                                    BusinessStatus.APPROVED.name -> if (business.isVerified) "VERIFIED LIVE" else "LIVE"
                                    BusinessStatus.PENDING_APPROVAL.name -> "PENDING APPROVAL"
                                    else -> business.status
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 4 Metric Stat Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(title = "Views", value = business.profileViews.toString(), icon = Icons.Default.Visibility, modifier = Modifier.weight(1f))
                        MetricCard(title = "Bookings", value = appointments.size.toString(), icon = Icons.Default.CalendarMonth, modifier = Modifier.weight(1f))
                        MetricCard(title = "Completed", value = appointments.count { it.status == AppointmentStatus.COMPLETED.name }.toString(), icon = Icons.Default.CheckCircle, modifier = Modifier.weight(1f))
                        MetricCard(title = "Rating", value = String.format("%.1f ★", business.rating), icon = Icons.Default.Star, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 2. Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = GoldPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // 3. Tab Contents
        when (selectedTab) {
            0 -> {
                // Bookings Inbox
                if (appointments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No appointments booked yet.")
                        }
                    }
                } else {
                    items(appointments) { appt ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .testTag("owner_appointment_card_${appt.id}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = appt.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = "${appt.serviceName} • R${appt.priceZar.toInt()}", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (appt.status == AppointmentStatus.CONFIRMED.name) EmeraldVerified else GoldPrimary
                                    ) {
                                        Text(
                                            text = appt.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(text = "📅 ${appt.appointmentDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "⏰ ${appt.appointmentTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "👤 ${appt.teamMemberName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (appt.notes.isNotBlank()) {
                                    Text(text = "Customer Note: ${appt.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                                // Status Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (appt.status == AppointmentStatus.PENDING.name) {
                                        Button(
                                            onClick = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.CONFIRMED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerified, contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Accept Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (appt.status == AppointmentStatus.CONFIRMED.name) {
                                        Button(
                                            onClick = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.COMPLETED) },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Mark Completed", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (appt.status != AppointmentStatus.CANCELLED.name && appt.status != AppointmentStatus.COMPLETED.name) {
                                        OutlinedButton(
                                            onClick = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.CANCELLED) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonCancel),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Decline / Cancel", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Services & Pricing Tab
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Manage Services (${services.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Button(
                            onClick = { showAddServiceDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.testTag("add_service_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Service", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(services) { srv ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = srv.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${srv.durationMinutes} mins • ${srv.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (srv.description.isNotBlank()) {
                                    Text(text = srv.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "R${srv.priceZar.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldPrimary)
                                IconButton(onClick = { viewModel.deleteServiceFromActiveBusiness(srv) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonCancel, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Team Tab
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Team & Specialists (${team.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Button(
                            onClick = { showAddTeamDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.testTag("add_team_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Specialist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(team) { member ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.avatarInitials.ifBlank { member.name.take(2).uppercase() },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = member.role, fontSize = 12.sp, color = GoldPrimary)
                                if (member.specialties.isNotBlank()) {
                                    Text(text = "Specialties: ${member.specialties}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            IconButton(onClick = { viewModel.deleteTeamMemberFromActiveBusiness(member) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonCancel, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            3 -> {
                // Profile Details & Onboarding CTA
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Business Contact Info", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("📞 Phone: ${business.phone}", fontSize = 13.sp)
                                Text("💬 WhatsApp: ${business.whatsapp}", fontSize = 13.sp)
                                Text("📧 Email: ${business.email}", fontSize = 13.sp)
                                Text("📍 Address: ${business.streetAddress}, ${business.suburb}, ${business.city}, ${business.province}", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onOpenOnboarding,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = GoldPrimary)
                        ) {
                            Text("Create / Add Another Business Listing", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Service Dialog
    if (showAddServiceDialog) {
        var srvName by remember { mutableStateOf("") }
        var srvCategory by remember { mutableStateOf("Haircut") }
        var srvDesc by remember { mutableStateOf("") }
        var srvPrice by remember { mutableStateOf("180") }
        var srvDuration by remember { mutableStateOf("30") }

        Dialog(onDismissRequest = { showAddServiceDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("add_service_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add New Service", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                    OutlinedTextField(
                        value = srvName,
                        onValueChange = { srvName = it },
                        label = { Text("Service Name (e.g. Skin Fade)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = srvPrice,
                        onValueChange = { srvPrice = it },
                        label = { Text("Price in ZAR (R)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = srvDuration,
                        onValueChange = { srvDuration = it },
                        label = { Text("Duration in Minutes (e.g. 45)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = srvDesc,
                        onValueChange = { srvDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddServiceDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val price = srvPrice.toDoubleOrNull() ?: 150.0
                                val duration = srvDuration.toIntOrNull() ?: 30
                                if (srvName.isNotBlank()) {
                                    viewModel.addServiceToActiveBusiness(srvName, srvCategory, srvDesc, price, duration)
                                    showAddServiceDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Service", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Team Member Dialog
    if (showAddTeamDialog) {
        var tmName by remember { mutableStateOf("") }
        var tmRole by remember { mutableStateOf("Stylist") }
        var tmBio by remember { mutableStateOf("") }
        var tmSpecialties by remember { mutableStateOf("Fades, Razor lines") }

        Dialog(onDismissRequest = { showAddTeamDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("add_team_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add Specialist / Team Member", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                    OutlinedTextField(
                        value = tmName,
                        onValueChange = { tmName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tmRole,
                        onValueChange = { tmRole = it },
                        label = { Text("Role (e.g. Master Barber, Senior Braider)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tmSpecialties,
                        onValueChange = { tmSpecialties = it },
                        label = { Text("Specialties (e.g. Knotless, Fades, Color)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tmBio,
                        onValueChange = { tmBio = it },
                        label = { Text("Short Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddTeamDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (tmName.isNotBlank()) {
                                    viewModel.addTeamMemberToActiveBusiness(tmName, tmRole, tmBio, tmSpecialties)
                                    showAddTeamDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Member", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
