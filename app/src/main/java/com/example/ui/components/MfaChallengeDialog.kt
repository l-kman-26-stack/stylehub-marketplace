package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@Composable
fun MfaChallengeDialog(
    isOpen: Boolean,
    userEmail: String?,
    maskedPhone: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onVerifyCode: (String) -> Unit,
    onCancel: () -> Unit
) {
    if (!isOpen) return

    var codeInput by remember { mutableStateOf("") }
    var useRecoveryCodeMode by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isLoading) onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("mfa_challenge_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shield Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (useRecoveryCodeMode) Icons.Outlined.Key else Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Title & Description
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (useRecoveryCodeMode) "Emergency Recovery Code" else "Two-Factor Authentication",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (useRecoveryCodeMode)
                            "Enter one of your 8-character single-use emergency backup recovery codes (e.g. A1B2-C3D4)."
                        else
                            "Enter the 6-digit verification code from your authenticator app (Google Authenticator, Microsoft Authenticator, or 1Password) for ${userEmail ?: "your account"}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }

                // Error Message
                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Text(errorMessage, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                        }
                    }
                }

                // Code Input Field
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        val filtered = if (useRecoveryCodeMode) it.take(9).uppercase() else it.filter { c -> c.isDigit() }.take(6)
                        codeInput = filtered
                        if (!useRecoveryCodeMode && filtered.length == 6) {
                            onVerifyCode(filtered)
                        }
                    },
                    label = { Text(if (useRecoveryCodeMode) "Recovery Code (e.g. A1B2-C3D4)" else "6-Digit Code") },
                    placeholder = { Text(if (useRecoveryCodeMode) "XXXX-XXXX" else "123456") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (useRecoveryCodeMode) KeyboardType.Text else KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (codeInput.isNotBlank()) onVerifyCode(codeInput)
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mfa_challenge_code_input")
                )

                // Toggle Recovery Code Mode
                TextButton(
                    onClick = {
                        useRecoveryCodeMode = !useRecoveryCodeMode
                        codeInput = ""
                    }
                ) {
                    Text(
                        text = if (useRecoveryCodeMode) "Use 6-digit Authenticator app code" else "Lost authenticator device? Use backup recovery code",
                        fontSize = 12.sp,
                        color = GoldPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onVerifyCode(codeInput) },
                        enabled = !isLoading && (if (useRecoveryCodeMode) codeInput.length >= 8 else codeInput.length == 6),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mfa_challenge_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CharcoalDark, strokeWidth = 2.dp)
                        } else {
                            Text("Verify & Sign In", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
