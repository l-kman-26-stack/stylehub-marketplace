package com.example.ui.screens.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AppointmentEntity
import com.example.data.local.entities.AppointmentStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.CustomerTab

@Composable
fun AppointmentsScreen(
    appointments: List<AppointmentEntity>,
    onCancelAppointment: (AppointmentEntity) -> Unit,
    onOpenReview: (AppointmentEntity) -> Unit,
    onExploreServices: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past & Completed")

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
            contentColor = GoldPrimary
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
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (selectedTab == 0) "No upcoming bookings" else "No past appointment history",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Find top-rated barbers, braiders and stylists in South Africa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onExploreServices,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        modifier = Modifier.testTag("book_first_appointment_button")
                    ) {
                        Text("Explore Grooming Studios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayList) { appt ->
                    AppointmentCard(
                        appointment = appt,
                        onCancel = { onCancelAppointment(appt) },
                        onReview = { onOpenReview(appt) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: AppointmentEntity,
    onCancel: () -> Unit,
    onReview: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("appointment_card_${appointment.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Business Name & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.businessName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = appointment.serviceName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Status Badge
                val (badgeColor, textColor) = when (appointment.status) {
                    AppointmentStatus.CONFIRMED.name -> EmeraldVerified to Color.White
                    AppointmentStatus.PENDING.name -> GoldPrimary to CharcoalDark
                    AppointmentStatus.COMPLETED.name -> Color(0xFF3B82F6) to Color.White
                    else -> CrimsonCancel to Color.White
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = appointment.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Details Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(15.dp), tint = GoldPrimary)
                        Text(
                            text = "${appointment.appointmentDate} • ${appointment.appointmentTime}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(15.dp), tint = GoldPrimary)
                        Text(
                            text = "Specialist: ${appointment.teamMemberName}",
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
                        text = "R${appointment.priceZar.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    )
                }
            }

            if (appointment.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${appointment.notes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Actions Row
            if (appointment.status == AppointmentStatus.CONFIRMED.name || appointment.status == AppointmentStatus.PENDING.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonCancel),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("cancel_appointment_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Booking", fontSize = 12.sp)
                    }
                }
            } else if (appointment.status == AppointmentStatus.COMPLETED.name && !appointment.hasReviewed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onReview,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("leave_review_button_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Leave Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
