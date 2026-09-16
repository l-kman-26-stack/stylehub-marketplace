package com.example.ui.screens.business

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.components.*
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
    val ownerBusinesses by viewModel.ownerBusinesses.collectAsState()
    val ownerReviews by viewModel.ownerReviews.collectAsState()
    val openingHours by viewModel.selectedBusinessHours.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Bookings", "Services", "Team", "Reviews", "Workspace")

    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddTeamDialog by remember { mutableStateOf(false) }

    // Confirmation dialog states
    var appointmentToCancel by remember { mutableStateOf<AppointmentEntity?>(null) }
    var serviceToDelete by remember { mutableStateOf<ServiceEntity?>(null) }
    var teamMemberToDelete by remember { mutableStateOf<TeamMemberEntity?>(null) }

    // Booking filter in Bookings tab
    var bookingFilter by remember { mutableStateOf("ALL") }

    if (business == null) {
        // Empty state when owner has no listing yet
        StyleHubEmptyState(
            icon = Icons.Default.Storefront,
            title = "Welcome to StyleHub Partner Workspace",
            description = "Register your salon, barbershop, or beauty studio to start receiving verified bookings from thousands of clients across South Africa.",
            actionButtonText = "Create Business Listing",
            onActionClick = onOpenOnboarding,
            modifier = Modifier.fillMaxSize().testTag("business_empty_state")
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("business_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Workspace Switcher & Partner Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = StyleHubDesignTokens.ElevationCard,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Multi-workspace selector if owner has multiple locations
                    if (ownerBusinesses.size > 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ACTIVE WORKSPACE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(ownerBusinesses) { b ->
                                    val isSelected = b.id == business.id
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectOwnerBusiness(b.id) },
                                        label = { Text(b.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Storefront,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    // Main Business Info & Status Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = business.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                                if (business.isVerified) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Business",
                                        tint = EmeraldVerified,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${business.category} • ${business.suburb}, ${business.city}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Status Badge
                        StyleHubStatusBadge(
                            status = when (business.status) {
                                BusinessStatus.APPROVED.name -> if (business.isVerified) "VERIFIED" else "ACTIVE"
                                else -> business.status
                            }
                        )
                    }

                    // 4 Enterprise Metric Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EnterpriseMetricCard(
                            title = "Views",
                            value = business.profileViews.toString(),
                            icon = Icons.Default.Visibility,
                            modifier = Modifier.weight(1f)
                        )
                        EnterpriseMetricCard(
                            title = "Bookings",
                            value = appointments.size.toString(),
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(1f)
                        )
                        EnterpriseMetricCard(
                            title = "Completed",
                            value = appointments.count { it.status == AppointmentStatus.COMPLETED.name }.toString(),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        EnterpriseMetricCard(
                            title = "Rating",
                            value = String.format("%.1f ★", business.rating),
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Navigation Tabs (Scrollable for professional clean layout)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = GoldPrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    val badgeCount = when (index) {
                        1 -> appointments.count { it.status == AppointmentStatus.PENDING.name }
                        else -> 0
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                                if (badgeCount > 0) {
                                    Surface(
                                        shape = StyleHubDesignTokens.RadiusFull,
                                        color = CrimsonCancel,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = badgeCount.toString(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        // 3. Tab Contents
        when (selectedTab) {
            0 -> {
                // TAB 0: Actionable Dashboard Overview
                item {
                    val pendingBookings = appointments.filter { it.status == AppointmentStatus.PENDING.name }
                    val confirmedBookings = appointments.filter { it.status == AppointmentStatus.CONFIRMED.name }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Quick Action Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StyleHubPrimaryButton(
                                text = "Add Service",
                                icon = Icons.Default.Add,
                                onClick = { showAddServiceDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                            StyleHubSecondaryButton(
                                text = "Add Specialist",
                                icon = Icons.Default.PersonAdd,
                                onClick = { showAddTeamDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Pending Bookings Requiring Action
                        StyleHubCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pending Confirmations (${pendingBookings.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (pendingBookings.isNotEmpty()) {
                                    StyleHubStatusBadge(status = "PENDING")
                                }
                            }

                            if (pendingBookings.isEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "All customer bookings are up to date! New booking requests will appear here for one-tap confirmation.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pendingBookings.take(3).forEach { appt ->
                                        AppointmentActionRow(
                                            appointment = appt,
                                            onConfirm = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.CONFIRMED) },
                                            onCancel = { appointmentToCancel = appt }
                                        )
                                    }
                                }
                            }
                        }

                        // Today's Upcoming Schedule
                        StyleHubCard {
                            Text(
                                text = "Upcoming Confirmed Appointments (${confirmedBookings.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            if (confirmedBookings.isEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No confirmed upcoming appointments scheduled yet.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Column(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    confirmedBookings.take(4).forEach { appt ->
                                        AppointmentConfirmedRow(
                                            appointment = appt,
                                            onComplete = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.COMPLETED) },
                                            onCancel = { appointmentToCancel = appt }
                                        )
                                    }
                                }
                            }
                        }

                        // Business Health & Verification Notice
                        if (!business.isVerified) {
                            Surface(
                                shape = StyleHubDesignTokens.RadiusCard,
                                color = GoldContainer.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Boost Your Business Visibility", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "Submit your CIPC or business registration documents to receive the verified partner badge and rank higher in local searches.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: Filterable Bookings Inbox
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Filter row
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val filters = listOf("ALL", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED")
                            items(filters) { f ->
                                FilterChip(
                                    selected = bookingFilter == f,
                                    onClick = { bookingFilter = f },
                                    label = { Text(f.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }

                val filteredList = appointments.filter {
                    if (bookingFilter == "ALL") true else it.status.uppercase() == bookingFilter
                }

                if (filteredList.isEmpty()) {
                    item {
                        StyleHubEmptyState(
                            icon = Icons.Default.EventBusy,
                            title = "No ${bookingFilter.lowercase().replaceFirstChar { it.uppercase() }} Bookings",
                            description = "There are no bookings matching the selected status filter."
                        )
                    }
                } else {
                    items(filteredList) { appt ->
                        StyleHubCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .testTag("owner_appointment_card_${appt.id}")
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = appt.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(
                                            text = "${appt.serviceName} • R${appt.priceZar.toInt()}",
                                            color = GoldPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    StyleHubStatusBadge(status = appt.status)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text(text = "📅 ${appt.appointmentDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "⏰ ${appt.appointmentTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "👤 ${appt.teamMemberName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (appt.notes.isNotBlank()) {
                                    Text(
                                        text = "Client Note: \"${appt.notes}\"",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                                // Status Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (appt.status == AppointmentStatus.PENDING.name) {
                                        StyleHubPrimaryButton(
                                            text = "Accept Booking",
                                            onClick = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.CONFIRMED) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    if (appt.status == AppointmentStatus.CONFIRMED.name) {
                                        StyleHubPrimaryButton(
                                            text = "Mark Completed",
                                            onClick = { viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.COMPLETED) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    if (appt.status != AppointmentStatus.CANCELLED.name && appt.status != AppointmentStatus.COMPLETED.name) {
                                        StyleHubSecondaryButton(
                                            text = "Decline / Cancel",
                                            onClick = { appointmentToCancel = appt },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: Services & Pricing
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Service Menu (${services.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Configure treatments, durations, and pricing in ZAR",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StyleHubPrimaryButton(
                            text = "Add Service",
                            icon = Icons.Default.Add,
                            onClick = { showAddServiceDialog = true },
                            modifier = Modifier.testTag("add_service_button")
                        )
                    }
                }

                if (services.isEmpty()) {
                    item {
                        StyleHubEmptyState(
                            icon = Icons.Default.ContentCut,
                            title = "No Services Listed Yet",
                            description = "Add haircuts, braiding, spa treatments, or styling services to begin taking bookings.",
                            actionButtonText = "Add First Service",
                            onActionClick = { showAddServiceDialog = true }
                        )
                    }
                } else {
                    items(services) { srv ->
                        StyleHubCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = srv.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        text = "${srv.durationMinutes} mins • ${srv.category}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (srv.description.isNotBlank()) {
                                        Text(
                                            text = srv.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(text = "R${srv.priceZar.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldPrimary)
                                    IconButton(
                                        onClick = { serviceToDelete = srv },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Service", tint = CrimsonCancel, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: Team Specialists
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Specialists & Team (${team.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Manage stylists, barbers, and technicians",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StyleHubPrimaryButton(
                            text = "Add Specialist",
                            icon = Icons.Default.PersonAdd,
                            onClick = { showAddTeamDialog = true },
                            modifier = Modifier.testTag("add_team_button")
                        )
                    }
                }

                if (team.isEmpty()) {
                    item {
                        StyleHubEmptyState(
                            icon = Icons.Default.Group,
                            title = "No Team Members Added",
                            description = "Add your specialists and barbers so clients can choose their preferred professional.",
                            actionButtonText = "Add First Specialist",
                            onActionClick = { showAddTeamDialog = true }
                        )
                    }
                } else {
                    items(team) { member ->
                        StyleHubCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.avatarInitials.ifBlank { member.name.take(2).uppercase() },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = member.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = member.role, fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                                    if (member.specialties.isNotBlank()) {
                                        Text(
                                            text = "Specialties: ${member.specialties}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { teamMemberToDelete = member },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Member", tint = CrimsonCancel, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // TAB 4: Reviews & Ratings
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StyleHubCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Customer Satisfaction", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Based on verified client reviews", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(24.dp))
                                    Text(
                                        text = String.format("%.1f", business.rating),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    )
                                    Text("(${business.reviewCount})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        if (ownerReviews.isEmpty()) {
                            StyleHubEmptyState(
                                icon = Icons.Default.RateReview,
                                title = "No Customer Reviews Yet",
                                description = "Reviews from verified completed appointments will appear here to help build your business reputation."
                            )
                        } else {
                            ownerReviews.forEach { review ->
                                StyleHubCard {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = review.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            repeat(review.rating) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                    if (review.comment.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = review.comment, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val reviewDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(review.createdAt))
                                    Text(text = reviewDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }

            5 -> {
                // TAB 5: Workspace Settings & Operating Hours
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StyleHubCard {
                            Text("Business Contact & Location", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("📞 Phone: ${business.phone}", fontSize = 13.sp)
                            Text("💬 WhatsApp: ${business.whatsapp}", fontSize = 13.sp)
                            Text("📧 Email: ${business.email}", fontSize = 13.sp)
                            Text("📍 Address: ${business.streetAddress}, ${business.suburb}, ${business.city}, ${business.province}", fontSize = 13.sp)
                        }

                        StyleHubCard {
                            Text("Weekly Operating Hours", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (openingHours.isEmpty()) {
                                Text("Standard Hours: Monday - Saturday (08:00 - 18:00)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                openingHours.forEach { h ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = h.dayName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            text = if (h.isClosed) "Closed" else "${h.openTime} - ${h.closeTime}",
                                            fontSize = 13.sp,
                                            color = if (h.isClosed) CrimsonCancel else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        StyleHubPrimaryButton(
                            text = "Register Another Business Location",
                            icon = Icons.Default.AddBusiness,
                            onClick = onOpenOnboarding,
                            modifier = Modifier.fillMaxWidth()
                        )
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
            Surface(
                shape = StyleHubDesignTokens.RadiusCard,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = StyleHubDesignTokens.ElevationModal,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_service_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add New Service", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))

                    OutlinedTextField(
                        value = srvName,
                        onValueChange = { srvName = it },
                        label = { Text("Service Name (e.g. Skin Fade)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = srvPrice,
                        onValueChange = { srvPrice = it },
                        label = { Text("Price in ZAR (R)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = srvDuration,
                        onValueChange = { srvDuration = it },
                        label = { Text("Duration in Minutes (e.g. 45)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                        StyleHubSecondaryButton(
                            text = "Cancel",
                            onClick = { showAddServiceDialog = false },
                            modifier = Modifier.weight(1f)
                        )

                        StyleHubPrimaryButton(
                            text = "Save Service",
                            onClick = {
                                val price = srvPrice.toDoubleOrNull() ?: 150.0
                                val duration = srvDuration.toIntOrNull() ?: 30
                                if (srvName.isNotBlank()) {
                                    viewModel.addServiceToActiveBusiness(srvName, srvCategory, srvDesc, price, duration)
                                    showAddServiceDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
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
            Surface(
                shape = StyleHubDesignTokens.RadiusCard,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = StyleHubDesignTokens.ElevationModal,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_team_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add Specialist / Team Member", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))

                    OutlinedTextField(
                        value = tmName,
                        onValueChange = { tmName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tmRole,
                        onValueChange = { tmRole = it },
                        label = { Text("Role (e.g. Master Barber, Senior Braider)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tmSpecialties,
                        onValueChange = { tmSpecialties = it },
                        label = { Text("Specialties (e.g. Knotless, Fades, Color)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                        StyleHubSecondaryButton(
                            text = "Cancel",
                            onClick = { showAddTeamDialog = false },
                            modifier = Modifier.weight(1f)
                        )

                        StyleHubPrimaryButton(
                            text = "Save Member",
                            onClick = {
                                if (tmName.isNotBlank()) {
                                    viewModel.addTeamMemberToActiveBusiness(tmName, tmRole, tmBio, tmSpecialties)
                                    showAddTeamDialog = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Confirmation: Cancel / Decline Booking
    appointmentToCancel?.let { appt ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Decline Booking Request",
            message = "Are you sure you want to decline or cancel the appointment for ${appt.customerName} on ${appt.appointmentDate} at ${appt.appointmentTime}?",
            consequenceNote = "The client will be notified immediately of the cancellation. This booking cannot be undone.",
            confirmButtonText = "Decline Booking",
            isDestructive = true,
            onConfirm = {
                viewModel.updateAppointmentStatusByOwner(appt, AppointmentStatus.CANCELLED)
                appointmentToCancel = null
            },
            onDismiss = { appointmentToCancel = null }
        )
    }

    // Confirmation: Delete Service
    serviceToDelete?.let { srv ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Delete Service",
            message = "Are you sure you want to remove \"${srv.name}\" from your service menu?",
            consequenceNote = "Clients will no longer be able to book this service online. Existing bookings will remain unaffected.",
            confirmButtonText = "Delete Service",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteServiceFromActiveBusiness(srv)
                serviceToDelete = null
            },
            onDismiss = { serviceToDelete = null }
        )
    }

    // Confirmation: Delete Specialist
    teamMemberToDelete?.let { member ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Remove Specialist",
            message = "Are you sure you want to remove ${member.name} (${member.role}) from your active specialists?",
            consequenceNote = "This specialist will no longer be selectable for new appointments. Ensure pending appointments are assigned.",
            confirmButtonText = "Remove Member",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteTeamMemberFromActiveBusiness(member)
                teamMemberToDelete = null
            },
            onDismiss = { teamMemberToDelete = null }
        )
    }
}

@Composable
private fun EnterpriseMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = StyleHubDesignTokens.RadiusMedium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AppointmentActionRow(
    appointment: AppointmentEntity,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        shape = StyleHubDesignTokens.RadiusSmall,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "${appointment.serviceName} • 📅 ${appointment.appointmentDate} ${appointment.appointmentTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StyleHubSecondaryButton(
                    text = "Decline",
                    onClick = onCancel,
                    modifier = Modifier.height(36.dp)
                )
                StyleHubPrimaryButton(
                    text = "Accept",
                    onClick = onConfirm,
                    modifier = Modifier.height(36.dp)
                )
            }
        }
    }
}

@Composable
private fun AppointmentConfirmedRow(
    appointment: AppointmentEntity,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        shape = StyleHubDesignTokens.RadiusSmall,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "${appointment.serviceName} • ⏰ ${appointment.appointmentTime} with ${appointment.teamMemberName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StyleHubSecondaryButton(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.height(36.dp)
                )
                StyleHubPrimaryButton(
                    text = "Complete",
                    onClick = onComplete,
                    modifier = Modifier.height(36.dp)
                )
            }
        }
    }
}
