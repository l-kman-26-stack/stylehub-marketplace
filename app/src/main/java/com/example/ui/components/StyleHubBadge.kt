package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StyleHubDesignTokens

enum class StyleHubStatusType {
    ACTIVE,
    VERIFIED,
    PENDING,
    UNDER_REVIEW,
    SUSPENDED,
    DEACTIVATED,
    REJECTED,
    CANCELLED,
    COMPLETED,
    INFO
}

/**
 * Standard enterprise Status Badge component.
 * Displays both an informative icon and clear text so status is NEVER communicated by color alone.
 */
@Composable
fun StyleHubStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    customType: StyleHubStatusType? = null
) {
    val type = customType ?: when (status.uppercase()) {
        "ACTIVE", "VERIFIED", "APPROVED", "CONFIRMED" -> StyleHubStatusType.ACTIVE
        "PENDING", "PENDING_APPROVAL", "UNDER_REVIEW", "SCHEDULED" -> StyleHubStatusType.PENDING
        "SUSPENDED" -> StyleHubStatusType.SUSPENDED
        "DEACTIVATED" -> StyleHubStatusType.DEACTIVATED
        "REJECTED" -> StyleHubStatusType.REJECTED
        "CANCELLED" -> StyleHubStatusType.CANCELLED
        "COMPLETED" -> StyleHubStatusType.COMPLETED
        else -> StyleHubStatusType.INFO
    }

    val (bgColor, textColor, borderColor, icon: ImageVector) = when (type) {
        StyleHubStatusType.ACTIVE, StyleHubStatusType.VERIFIED -> Quadruple(
            StyleHubDesignTokens.StatusSuccess.copy(alpha = 0.15f),
            StyleHubDesignTokens.StatusSuccess,
            StyleHubDesignTokens.StatusSuccess.copy(alpha = 0.4f),
            Icons.Default.CheckCircle
        )
        StyleHubStatusType.PENDING, StyleHubStatusType.UNDER_REVIEW -> Quadruple(
            StyleHubDesignTokens.Primary.copy(alpha = 0.15f),
            StyleHubDesignTokens.Primary,
            StyleHubDesignTokens.Primary.copy(alpha = 0.4f),
            Icons.Default.HourglassTop
        )
        StyleHubStatusType.SUSPENDED -> Quadruple(
            StyleHubDesignTokens.StatusError.copy(alpha = 0.15f),
            StyleHubDesignTokens.StatusError,
            StyleHubDesignTokens.StatusError.copy(alpha = 0.4f),
            Icons.Default.Block
        )
        StyleHubStatusType.DEACTIVATED, StyleHubStatusType.CANCELLED -> Quadruple(
            Color(0xFF6B7280).copy(alpha = 0.15f),
            Color(0xFF9CA3AF),
            Color(0xFF6B7280).copy(alpha = 0.4f),
            Icons.Default.Cancel
        )
        StyleHubStatusType.REJECTED -> Quadruple(
            StyleHubDesignTokens.StatusError.copy(alpha = 0.15f),
            StyleHubDesignTokens.StatusError,
            StyleHubDesignTokens.StatusError.copy(alpha = 0.4f),
            Icons.Default.HighlightOff
        )
        StyleHubStatusType.COMPLETED -> Quadruple(
            StyleHubDesignTokens.StatusInfo.copy(alpha = 0.15f),
            StyleHubDesignTokens.StatusInfo,
            StyleHubDesignTokens.StatusInfo.copy(alpha = 0.4f),
            Icons.Default.TaskAlt
        )
        StyleHubStatusType.INFO -> Quadruple(
            StyleHubDesignTokens.StatusInfo.copy(alpha = 0.15f),
            StyleHubDesignTokens.StatusInfo,
            StyleHubDesignTokens.StatusInfo.copy(alpha = 0.4f),
            Icons.Default.Info
        )
    }

    val displayLabel = when (status.uppercase()) {
        "PENDING_APPROVAL" -> "Under Review"
        "PENDING_DELETION" -> "Pending Deletion"
        else -> status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
    }

    Surface(
        shape = StyleHubDesignTokens.RadiusSmall,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = displayLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
