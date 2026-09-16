package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.local.entities.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StyleHubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: StyleHubViewModel,
    allBusinesses: List<BusinessEntity>,
    allUsers: List<UserEntity>,
    allAppointments: List<AppointmentEntity>,
    allReviews: List<ReviewEntity>
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProvinceFilter by remember { mutableStateOf("ALL") }

    val pendingList = allBusinesses.filter { it.status == BusinessStatus.PENDING_APPROVAL.name }

    // Confirmation dialog states
    var businessToReject by remember { mutableStateOf<BusinessEntity?>(null) }
    var userToToggleSuspension by remember { mutableStateOf<UserEntity?>(null) }
    var reviewToDelete by remember { mutableStateOf<ReviewEntity?>(null) }

    val tabs = listOf(
        "Approvals (${pendingList.size})",
        "Businesses (${allBusinesses.size})",
        "Users (${allUsers.size})",
        "Reviews (${allReviews.size})",
        "Audit Trail"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Admin Header & Platform Overview
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(StyleHubDesignTokens.RadiusMedium)
                                    .background(StyleHubDesignTokens.StatusError.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = StyleHubDesignTokens.StatusError,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Platform Administration",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp
                                    )
                                )
                                Text(
                                    text = "StyleHub South Africa Super Admin Console",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = StyleHubDesignTokens.RadiusSmall,
                            color = StyleHubDesignTokens.StatusError.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, StyleHubDesignTokens.StatusError.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "ADMIN CONSOLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StyleHubDesignTokens.StatusError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Platform Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricCard(
                            label = "Businesses",
                            value = "${allBusinesses.size}",
                            color = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "Pending",
                            value = "${pendingList.size}",
                            color = if (pendingList.isNotEmpty()) CrimsonCancel else GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "Bookings",
                            value = "${allAppointments.size}",
                            color = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricCard(
                            label = "Reviews",
                            value = "${allReviews.size}",
                            color = GoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Global Search & Quick Filter
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, email, city, category...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        shape = StyleHubDesignTokens.RadiusMedium,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Navigation Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = GoldPrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) }
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
                // Pending Approvals Tab
                val filteredPending = pendingList.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
                            it.city.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }

                if (filteredPending.isEmpty()) {
                    item {
                        StyleHubEmptyState(
                            icon = Icons.Default.CheckCircle,
                            title = "All Partner Applications Reviewed",
                            description = "There are no pending business listings requiring verification at this time."
                        )
                    }
                } else {
                    items(filteredPending) { biz ->
                        StyleHubCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .testTag("admin_pending_card_${biz.id}")
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = biz.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(
                                            text = "${biz.category} • Submitted for verification",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    StyleHubStatusBadge(status = "PENDING_APPROVAL")
                                }

                                Text(
                                    text = "📍 ${biz.streetAddress}, ${biz.suburb}, ${biz.city} (${biz.province})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "📞 Phone: ${biz.phone} • WhatsApp: ${biz.whatsapp} • Email: ${biz.email}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (biz.description.isNotBlank()) {
                                    Text(text = biz.description, fontSize = 13.sp)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    StyleHubSecondaryButton(
                                        text = "Reject",
                                        icon = Icons.Default.Close,
                                        onClick = { businessToReject = biz },
                                        modifier = Modifier.weight(1f).testTag("reject_business_btn_${biz.id}")
                                    )

                                    StyleHubPrimaryButton(
                                        text = "Approve & Publish",
                                        icon = Icons.Default.Check,
                                        onClick = { viewModel.approveBusiness(biz.id) },
                                        modifier = Modifier.weight(1f).testTag("approve_business_btn_${biz.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // All Businesses Directory & Moderation
                val filteredBusinesses = allBusinesses.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
                            it.city.contains(searchQuery, ignoreCase = true) ||
                            it.suburb.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }

                items(filteredBusinesses) { biz ->
                    StyleHubCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = biz.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        if (biz.isVerified) {
                                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = EmeraldVerified, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text(
                                        text = "${biz.category} • ${biz.suburb}, ${biz.city} (${biz.province})",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                StyleHubStatusBadge(status = biz.status)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(text = "👀 ${biz.profileViews} Views", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "⭐ ${String.format("%.1f", biz.rating)} (${biz.reviewCount} reviews)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                            // Verification and Featured Toggles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = biz.isVerified,
                                    onClick = { viewModel.toggleVerification(biz.id) },
                                    label = { Text(if (biz.isVerified) "Verified 🛡️" else "Unverified") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                )

                                FilterChip(
                                    selected = biz.isFeatured,
                                    onClick = { viewModel.toggleFeatured(biz.id) },
                                    label = { Text(if (biz.isFeatured) "Featured ⭐" else "Standard") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Users Directory Tab
                val filteredUsers = allUsers.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) ||
                            it.email.contains(searchQuery, ignoreCase = true) ||
                            it.phone.contains(searchQuery, ignoreCase = true)
                }

                items(filteredUsers) { user ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        fontSize = 15.sp
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (user.isMfaEnabled || user.forceMfa) {
                                            Surface(
                                                shape = StyleHubDesignTokens.RadiusSmall,
                                                color = EmeraldVerified.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    "2FA",
                                                    fontSize = 9.sp,
                                                    color = EmeraldVerified,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        if (user.isSuspended) {
                                            StyleHubStatusBadge(status = "SUSPENDED")
                                        }
                                    }
                                    Text(text = "${user.email} • ${user.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Role: ${user.role}", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.SemiBold)
                                        Text(text = "• Provider: ${user.authProvider}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            IconButton(
                                onClick = { userToToggleSuspension = user },
                                modifier = Modifier.size(40.dp)
                            ) {
                                val isUserActive = !user.isSuspended
                                Icon(
                                    imageVector = if (isUserActive) Icons.Default.CheckCircle else Icons.Default.Block,
                                    contentDescription = if (isUserActive) "Active User" else "Suspended User",
                                    tint = if (isUserActive) EmeraldVerified else CrimsonCancel
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                // Reviews Moderation Tab
                val filteredReviews = allReviews.filter {
                    searchQuery.isBlank() || it.customerName.contains(searchQuery, ignoreCase = true) ||
                            it.serviceName.contains(searchQuery, ignoreCase = true) ||
                            it.comment.contains(searchQuery, ignoreCase = true)
                }

                items(filteredReviews) { rev ->
                    StyleHubCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${rev.customerName} → ${rev.serviceName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(
                                    onClick = { reviewToDelete = rev },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Review", tint = CrimsonCancel, modifier = Modifier.size(18.dp))
                                }
                            }

                            Row {
                                repeat(rev.rating) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(14.dp))
                                }
                            }

                            Text(text = "\"${rev.comment}\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            val revDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(rev.createdAt))
                            Text(text = revDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            4 -> {
                // Security & Audit Logs Tab
                item {
                    val auditLogs by viewModel.allAuditLogsAdmin.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StyleHubPrimaryButton(
                            text = "Open Admin Security & Identity Center",
                            icon = Icons.Default.Shield,
                            onClick = { viewModel.openSecurityDashboard() },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Platform Audit Trail (${auditLogs.size} events logged)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        auditLogs.forEach { log ->
                            val sevColor = when (log.severity) {
                                "CRITICAL" -> StyleHubDesignTokens.StatusError
                                "WARNING" -> StyleHubDesignTokens.StatusWarning
                                else -> StyleHubDesignTokens.StatusSuccess
                            }
                            val dateStr = java.text.SimpleDateFormat("dd MMM, HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))

                            Surface(
                                shape = StyleHubDesignTokens.RadiusSmall,
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = StyleHubDesignTokens.ElevationLow,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(sevColor)
                                    )
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(log.eventType.replace("_", " "), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text("User: ${log.userEmail ?: "System"} • ${log.device} • ${log.ipAddress}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation: Reject Business
    businessToReject?.let { biz ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Reject Business Listing",
            message = "Are you sure you want to reject the application for \"${biz.name}\"?",
            consequenceNote = "The business owner will receive notice that their application does not meet StyleHub South Africa marketplace standards. You can reverse this later from the Admin directory.",
            confirmButtonText = "Reject Listing",
            isDestructive = true,
            onConfirm = {
                viewModel.rejectBusiness(biz.id)
                businessToReject = null
            },
            onDismiss = { businessToReject = null }
        )
    }

    // Confirmation: Suspend / Reactivate User
    userToToggleSuspension?.let { user ->
        val willSuspend = !user.isSuspended
        StyleHubConfirmationDialog(
            isOpen = true,
            title = if (willSuspend) "Suspend User Account" else "Reactivate User Account",
            message = if (willSuspend)
                "Are you sure you want to suspend ${user.name} (${user.email})?"
            else
                "Are you sure you want to restore active status for ${user.name}?",
            consequenceNote = if (willSuspend)
                "The user will immediately be blocked from logging in, booking appointments, or managing listings until reactivated by an admin."
            else
                "The user will regain full access to their account and existing appointments.",
            confirmButtonText = if (willSuspend) "Suspend User" else "Reactivate",
            isDestructive = willSuspend,
            onConfirm = {
                viewModel.toggleUserSuspension(user.id)
                userToToggleSuspension = null
            },
            onDismiss = { userToToggleSuspension = null }
        )
    }

    // Confirmation: Delete Review
    reviewToDelete?.let { rev ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Delete Customer Review",
            message = "Are you sure you want to remove the review by ${rev.customerName} for ${rev.serviceName}?",
            consequenceNote = "This action is permanent and cannot be undone. The business's aggregate rating will be automatically recalculated.",
            confirmButtonText = "Delete Review",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteReviewByAdmin(rev)
                reviewToDelete = null
            },
            onDismiss = { reviewToDelete = null }
        )
    }
}

@Composable
private fun AdminMetricCard(
    label: String,
    value: String,
    color: Color,
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
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
