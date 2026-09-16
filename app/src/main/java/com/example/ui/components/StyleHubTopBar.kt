package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onRoleChange: ((UserRole) -> Unit)? = null,
    notificationCount: Int,
    onNotificationClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    title: String = "StyleHub",
    subtitle: String? = "Find Your Style. Find Your Professional."
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = StyleHubDesignTokens.ElevationCard,
        shadowElevation = StyleHubDesignTokens.ElevationLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .defaultMinSize(minWidth = StyleHubDesignTokens.MinTouchTarget, minHeight = StyleHubDesignTokens.MinTouchTarget)
                                .testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(StyleHubDesignTokens.RadiusMedium)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Country indicator badge
                            Surface(
                                shape = StyleHubDesignTokens.RadiusSmall,
                                color = GoldContainer.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "🇿🇦 ZAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (subtitle != null && onBackClick == null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Right: Active Portal Badge & Notification Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show role badge if non-customer (e.g. Owner or Admin portal mode)
                    if (currentRole != UserRole.CUSTOMER) {
                        Surface(
                            shape = StyleHubDesignTokens.RadiusFull,
                            color = when (currentRole) {
                                UserRole.BUSINESS_OWNER -> StyleHubDesignTokens.StatusInfo.copy(alpha = 0.15f)
                                UserRole.ADMIN -> StyleHubDesignTokens.StatusError.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (currentRole == UserRole.BUSINESS_OWNER) StyleHubDesignTokens.StatusInfo.copy(alpha = 0.4f)
                                else StyleHubDesignTokens.StatusError.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clickable(enabled = onRoleChange != null) {
                                    onRoleChange?.invoke(UserRole.CUSTOMER)
                                }
                                .testTag("top_bar_portal_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentRole == UserRole.BUSINESS_OWNER) Icons.Default.Storefront else Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (currentRole == UserRole.BUSINESS_OWNER) StyleHubDesignTokens.StatusInfo else StyleHubDesignTokens.StatusError,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (currentRole == UserRole.BUSINESS_OWNER) "Partner Workspace" else "Admin Console",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole == UserRole.BUSINESS_OWNER) StyleHubDesignTokens.StatusInfo else StyleHubDesignTokens.StatusError
                                )
                            }
                        }
                    }

                    // Notification Button
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .defaultMinSize(minWidth = StyleHubDesignTokens.MinTouchTarget, minHeight = StyleHubDesignTokens.MinTouchTarget)
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
