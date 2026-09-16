package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.StyleHubDesignTokens

/**
 * Standard confirmation dialog for critical and destructive actions.
 * Clearly articulates:
 * 1. What will happen.
 * 2. Whether the action is reversible or permanent.
 * 3. Primary action button (destructive or standard) and Cancel.
 */
@Composable
fun StyleHubConfirmationDialog(
    isOpen: Boolean,
    title: String,
    message: String,
    consequenceNote: String? = null,
    confirmButtonText: String = "Confirm",
    cancelButtonText: String = "Cancel",
    isDestructive: Boolean = true,
    icon: ImageVector = Icons.Default.Warning,
    isLoading: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Surface(
            shape = StyleHubDesignTokens.RadiusCard,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = StyleHubDesignTokens.ElevationModal,
            shadowElevation = StyleHubDesignTokens.ElevationModal,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDestructive) StyleHubDesignTokens.StatusError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDestructive) StyleHubDesignTokens.StatusError.copy(alpha = 0.15f) else StyleHubDesignTokens.Primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isDestructive) StyleHubDesignTokens.StatusError else StyleHubDesignTokens.Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                }

                // Message body
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Consequence or reversibility notice
                if (consequenceNote != null) {
                    Surface(
                        shape = StyleHubDesignTokens.RadiusSmall,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = consequenceNote,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDestructive) StyleHubDesignTokens.StatusError else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StyleHubSecondaryButton(
                        text = cancelButtonText,
                        onClick = onDismiss,
                        enabled = !isLoading
                    )

                    if (isDestructive) {
                        StyleHubDestructiveButton(
                            text = confirmButtonText,
                            onClick = onConfirm,
                            isLoading = isLoading
                        )
                    } else {
                        StyleHubPrimaryButton(
                            text = confirmButtonText,
                            onClick = onConfirm,
                            isLoading = isLoading
                        )
                    }
                }
            }
        }
    }
}
