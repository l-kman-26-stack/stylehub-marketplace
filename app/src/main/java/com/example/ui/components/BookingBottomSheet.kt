package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BusinessEntity
import com.example.data.local.entities.ServiceEntity
import com.example.data.local.entities.TeamMemberEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingBottomSheet(
    business: BusinessEntity,
    services: List<ServiceEntity>,
    teamMembers: List<TeamMemberEntity>,
    selectedService: ServiceEntity?,
    selectedTeamMember: TeamMemberEntity?,
    selectedDate: String,
    selectedTime: String,
    notes: String,
    onServiceSelected: (ServiceEntity) -> Unit,
    onTeamMemberSelected: (TeamMemberEntity?) -> Unit,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onConfirmBooking: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateOptions = listOf(
        "Today, 26 Aug",
        "Tomorrow, 27 Aug",
        "Fri, 28 Aug",
        "Sat, 29 Aug",
        "Mon, 31 Aug"
    )

    val timeSlots = listOf(
        "09:00", "10:00", "11:30", "13:00", "14:00", "15:30", "16:30", "17:30"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("booking_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Book Appointment",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider()

            // 1. Service Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Select Service",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                services.forEach { service ->
                    val isSelected = selectedService?.id == service.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onServiceSelected(service) }
                            .testTag("booking_service_${service.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${service.durationMinutes} mins • ${service.category}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "R${service.priceZar.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }

            // 2. Professional / Team Member Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. Select Professional",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        val isSelected = selectedTeamMember == null
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary) else null,
                            modifier = Modifier.clickable { onTeamMemberSelected(null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Any Professional", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    items(teamMembers) { member ->
                        val isSelected = selectedTeamMember?.id == member.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary) else null,
                            modifier = Modifier
                                .clickable { onTeamMemberSelected(member) }
                                .testTag("booking_team_${member.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.avatarInitials.ifBlank { member.name.take(2).uppercase() },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    )
                                }
                                Column {
                                    Text(member.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(member.role, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Date Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. Select Date",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateOptions) { date ->
                        val isSelected = selectedDate == date
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { onDateSelected(date) }
                        ) {
                            Text(
                                text = date,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CharcoalDark else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Time Slot Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "4. Available Time Slot",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.take(4).forEach { time ->
                        val isSelected = selectedTime == time
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTimeSelected(time) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CharcoalDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeSlots.drop(4).take(4).forEach { time ->
                        val isSelected = selectedTime == time
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTimeSelected(time) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = time,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CharcoalDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 5. Special Notes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "5. Notes or Special Requests (Optional)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChanged,
                    placeholder = { Text("e.g. Please bring extra clipper guards, skin fade with razor finish...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_notes_input"),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }

            // Summary & Confirm Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Total (ZAR):",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "R${selectedService?.priceZar?.toInt() ?: 0}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        )
                        Text(
                            text = "Pay at venue on appointment",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onConfirmBooking,
                        enabled = selectedService != null,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = CharcoalDark
                        ),
                        modifier = Modifier.testTag("confirm_booking_button")
                    ) {
                        Text(
                            text = "Confirm Booking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
