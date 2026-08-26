package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@Composable
fun PhoneVerificationDialog(
    isOpen: Boolean,
    initialPhone: String,
    onDismiss: () -> Unit,
    onSendOtp: (phone: String) -> String,
    onConfirmOtp: (phone: String, code: String) -> Unit
) {
    if (!isOpen) return

    var phone by remember { mutableStateOf(initialPhone) }
    var code by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = Enter Phone, 2 = Enter Code
    var dispatchedCode by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("phone_verification_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary)
                    Text("South Africa Phone Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                if (step == 1) {
                    Text(
                        "We will send a 6-digit SMS verification code to your South African mobile number:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("+27 82 555 1234") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                dispatchedCode = onSendOtp(phone)
                                step = 2
                            },
                            enabled = phone.length >= 10,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                        ) {
                            Text("Send SMS Code")
                        }
                    }
                } else {
                    Text(
                        "Enter the 6-digit SMS verification code sent to $phone:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "📲 Demo SMS Code: $dispatchedCode (or use 123456)",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalDark
                        )
                    }

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.take(6).filter { c -> c.isDigit() } },
                        label = { Text("6-Digit Code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { step = 1 }) { Text("Back") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onConfirmOtp(phone, code) },
                            enabled = code.length == 6,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                        ) {
                            Text("Verify Number")
                        }
                    }
                }
            }
        }
    }
}
