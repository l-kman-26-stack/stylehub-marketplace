package com.example.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.entities.AppointmentEntity
import com.example.data.local.entities.AppointmentStatus
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AppointmentsScreen(
    appointments: List<AppointmentEntity>,
    onCancelAppointment: (AppointmentEntity) -> Unit,
    onOpenReview: (AppointmentEntity) -> Unit,
    onExploreServices: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past & History")

    var appointmentToCancel by remember { mutableStateOf<AppointmentEntity?>(null) }

    val upcomingAppointments = appointments.filter {
        it.status == AppointmentStatus.CONFIRMED.name || it.status == AppointmentStatus.PENDING.name
    }
    val pastAppointments = appointments.filter {
        it.status == AppointmentStatus.COMPLETED.name || it.status == AppointmentStatus.CANCELLED.name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("appointments_screen")
    ) {
        // Tab Header
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = GoldPrimary,
            divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) }
        ) {
            tabs.forEachIndexed { index, title ->
                val count = if (index == 0) upcomingAppointments.size else pastAppointments.size
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = "$title ($count)",
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        val displayList = if (selectedTab == 0) upcomingAppointments else pastAppointments

        if (displayList.isEmpty()) {
            StyleHubEmptyState(
                icon = if (selectedTab == 0) Icons.Outlined.CalendarMonth else Icons.Outlined.History,
                title = if (selectedTab == 0) "No Upcoming Appointments" else "No Appointment History",
                description = if (selectedTab == 0)
                    "Book appointments with top-rated barbers, braiders, stylists, and aesthetic clinics across South Africa."
                else
                    "Your completed and past bookings will be documented here for easy re-booking and leaving reviews.",
                actionButtonText = if (selectedTab == 0) "Explore Grooming Studios" else null,
                onActionClick = if (selectedTab == 0) onExploreServices else null,
                modifier = Modifier.fillMaxSize().testTag("appointments_empty_state")
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayList) { appt ->
                    StyleHubCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appointment_card_${appt.id}")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Header Row: Business Name & Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = appt.businessName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = appt.serviceName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = GoldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                StyleHubStatusBadge(status = appt.status)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                            // Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(15.dp), tint = GoldPrimary)
                                        Text(
                                            text = "${appt.appointmentDate} • ${appt.appointmentTime}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp), tint = GoldPrimary)
                                        Text(
                                            text = "Specialist: ${appt.teamMemberName}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Total (ZAR)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "R${appt.priceZar.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    )
                                }
                            }

                            if (appt.notes.isNotBlank()) {
                                Surface(
                                    shape = StyleHubDesignTokens.RadiusSmall,
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Notes: ${appt.notes}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            // Actions Row
                            if (appt.status == AppointmentStatus.CONFIRMED.name || appt.status == AppointmentStatus.PENDING.name) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    StyleHubSecondaryButton(
                                        text = "Cancel Booking",
                                        icon = Icons.Default.Cancel,
                                        onClick = { appointmentToCancel = appt },
                                        modifier = Modifier.testTag("cancel_appointment_${appt.id}")
                                    )
                                }
                            } else if (appt.status == AppointmentStatus.COMPLETED.name && !appt.hasReviewed) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    StyleHubPrimaryButton(
                                        text = "Leave Review",
                                        icon = Icons.Default.Star,
                                        onClick = { onOpenReview(appt) },
                                        modifier = Modifier.testTag("leave_review_button_${appt.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancellation Confirmation Dialog
    appointmentToCancel?.let { appt ->
        StyleHubConfirmationDialog(
            isOpen = true,
            title = "Cancel Appointment",
            message = "Are you sure you want to cancel your booking with ${appt.businessName} on ${appt.appointmentDate} at ${appt.appointmentTime}?",
            consequenceNote = "This action will release your slot back to the studio. If you need a different time, you can book a new slot anytime.",
            confirmButtonText = "Confirm Cancellation",
            cancelButtonText = "Keep Booking",
            isDestructive = true,
            onConfirm = {
                onCancelAppointment(appt)
                appointmentToCancel = null
            },
            onDismiss = { appointmentToCancel = null }
        )
    }
}
