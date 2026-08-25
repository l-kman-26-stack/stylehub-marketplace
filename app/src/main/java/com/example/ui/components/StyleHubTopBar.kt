package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.local.entities.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleHubTopBar(
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    notificationCount: Int,
    onNotificationClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    title: String = "StyleHub",
    subtitle: String? = "Find Your Style. Find Your Professional."
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Back button or Logo branding
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "StyleHub Logo",
                                tint = CharcoalDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Country indicator badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldContainer.copy(alpha = 0.3f),
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "🇿🇦 ZAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (subtitle != null && onBackClick == null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Right: Role Switcher Pill & Notification Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Role Switcher Dropdown Button
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when (currentRole) {
                                UserRole.CUSTOMER -> MaterialTheme.colorScheme.primaryContainer
                                UserRole.BUSINESS_OWNER -> Color(0xFF1E3A8A).copy(alpha = 0.2f)
                                UserRole.ADMIN -> Color(0xFF7F1D1D).copy(alpha = 0.2f)
                            },
                            border = null,
                            modifier = Modifier
                                .clickable { roleMenuExpanded = true }
                                .testTag("role_switcher_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val roleIcon = when (currentRole) {
                                    UserRole.CUSTOMER -> Icons.Default.Person
                                    UserRole.BUSINESS_OWNER -> Icons.Default.Storefront
                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                }
                                val roleColor = when (currentRole) {
                                    UserRole.CUSTOMER -> GoldPrimary
                                    UserRole.BUSINESS_OWNER -> Color(0xFF3B82F6)
                                    UserRole.ADMIN -> Color(0xFFEF4444)
                                }
                                Icon(
                                    imageVector = roleIcon,
                                    contentDescription = null,
                                    tint = roleColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = when (currentRole) {
                                        UserRole.CUSTOMER -> "Customer"
                                        UserRole.BUSINESS_OWNER -> "Owner"
                                        UserRole.ADMIN -> "Admin"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = roleColor
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = roleColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Customer Mode", fontWeight = FontWeight.Bold)
                                        Text("Discover, compare & book services", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary)
                                },
                                onClick = {
                                    onRoleChange(UserRole.CUSTOMER)
                                    roleMenuExpanded = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Business Owner Mode", fontWeight = FontWeight.Bold)
                                        Text("Manage listing, services & bookings", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF3B82F6))
                                },
                                onClick = {
                                    onRoleChange(UserRole.BUSINESS_OWNER)
                                    roleMenuExpanded = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Administrator Mode", fontWeight = FontWeight.Bold)
                                        Text("Verify, approve & monitor platform", fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFEF4444))
                                },
                                onClick = {
                                    onRoleChange(UserRole.ADMIN)
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }

                    // Notification Button
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (notificationCount > 0) {
                                    Badge(
                                        containerColor = CrimsonCancel,
                                        contentColor = Color.White
                                    ) {
                                        Text(notificationCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
