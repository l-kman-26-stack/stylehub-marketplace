package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.UserRole
import com.example.security.OAuthProvider
import com.example.security.SecurityValidator
import com.example.ui.theme.*

@Composable
fun AuthModal(
    isOpen: Boolean,
    initialTab: Int = 0, // 0 = Sign In, 1 = Sign Up, 2 = Forgot Password
    isLoading: Boolean,
    errorMessage: String?,
    lockoutMinutes: Long?,
    onClose: () -> Unit,
    onSignIn: (email: String, pass: String) -> Unit,
    onSignUp: (name: String, email: String, phone: String, pass: String, role: UserRole, city: String, province: String) -> Unit,
    onOAuthSignIn: (OAuthProvider) -> Unit,
    onRequestPasswordReset: (email: String) -> Unit
) {
    if (!isOpen) return

    var currentTab by remember(initialTab) { mutableStateOf(initialTab) }

    // Form inputs
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+27 ") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var selectedCity by remember { mutableStateOf("Johannesburg") }
    var selectedProvince by remember { mutableStateOf("Gauteng") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isBotVerified by remember { mutableStateOf(true) }

    // Validation
    val passwordStrength = remember(password) { SecurityValidator.evaluatePassword(password) }
    val isEmailValid = remember(email) { SecurityValidator.isValidEmail(email) }

    Dialog(
        onDismissRequest = { if (!isLoading) onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("auth_modal"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CharcoalDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "StyleHub Identity",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Enterprise Security & Auth",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("auth_modal_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher (Sign In / Register / Reset)
                TabRow(
                    selectedTabIndex = currentTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = GoldPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("auth_tabs")
                ) {
                    Tab(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        text = { Text("Sign In", fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        text = { Text("Register", fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        text = { Text("Recovery", fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Error / Lockout Banner
                if (lockoutMinutes != null && lockoutMinutes > 0) {
                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                            Column {
                                Text(
                                    "Account Locked (Rate Limit)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444),
                                    fontSize = 13.sp
                                )
                                Text(
                                    "5 failed attempts detected. Access is temporarily locked for $lockoutMinutes more minutes to protect your account.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Scrollable Content Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (currentTab) {
                        0 -> {
                            // --- SIGN IN TAB ---
                            Text(
                                "Welcome Back",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Sign in to access your South African grooming bookings and account preferences.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signin_email_input")
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signin_password_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { currentTab = 2 }) {
                                    Text("Forgot Password?", fontSize = 12.sp, color = GoldPrimary)
                                }
                            }

                            // Submit Button
                            Button(
                                onClick = { onSignIn(email, password) },
                                enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && (lockoutMinutes == null || lockoutMinutes <= 0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_signin_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CharcoalDark, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Authenticating...")
                                } else {
                                    Icon(Icons.Default.Login, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign In Securely", fontWeight = FontWeight.Bold)
                                }
                            }

                            SocialDivider()
                            OAuthButtons(isLoading = isLoading, onOAuthClick = onOAuthSignIn)
                        }

                        1 -> {
                            // --- REGISTER TAB ---
                            Text(
                                "Create Account",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Join StyleHub South Africa as a Customer or Salon / Barber Business Owner.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Role Selection Chips
                            Text("I want to use StyleHub as:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilterChip(
                                    selected = selectedRole == UserRole.CUSTOMER,
                                    onClick = { selectedRole = UserRole.CUSTOMER },
                                    label = { Text("Customer") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = selectedRole == UserRole.BUSINESS_OWNER,
                                    onClick = { selectedRole = UserRole.BUSINESS_OWNER },
                                    label = { Text("Business Owner") },
                                    leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_name_input")
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_email_input")
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Mobile Number (South Africa)") },
                                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_phone_input")
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_password_input")
                            )

                            // Password Strength Bar
                            PasswordStrengthIndicator(strength = passwordStrength)

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                isError = confirmPassword.isNotBlank() && confirmPassword != password,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // City / Province Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedCity,
                                    onValueChange = { selectedCity = it },
                                    label = { Text("City") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = selectedProvince,
                                    onValueChange = { selectedProvince = it },
                                    label = { Text("Province") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Bot Protection Checkbox
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = isBotVerified,
                                            onCheckedChange = { isBotVerified = it },
                                            modifier = Modifier.testTag("auth_bot_protection_checkbox")
                                        )
                                        Text("I'm not a robot", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Text("reCAPTCHA v3", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Button(
                                onClick = {
                                    onSignUp(name, email, phone, password, selectedRole, selectedCity, selectedProvince)
                                },
                                enabled = !isLoading && isEmailValid && passwordStrength.isValid && password == confirmPassword && name.isNotBlank() && isBotVerified,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_signup_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CharcoalDark, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Create Secure Account", fontWeight = FontWeight.Bold)
                                }
                            }

                            SocialDivider()
                            OAuthButtons(isLoading = isLoading, onOAuthClick = onOAuthSignIn)
                        }

                        2 -> {
                            // --- RECOVERY TAB ---
                            Text(
                                "Account Recovery",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Enter your registered email address and we'll send you an encrypted link and token to reset your password securely.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Registered Email Address") },
                                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_recovery_email_input")
                            )

                            Button(
                                onClick = { onRequestPasswordReset(email) },
                                enabled = !isLoading && isEmailValid,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("auth_recovery_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Recovery Link", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { currentTab = 0 },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back to Sign In")
                            }
                        }
                    }

                    // Security & Legal Notice Footer
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Shield, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Secured with PBKDF2 cryptography, RFC 6238 TOTP, and Meta Graph API OAuth token management.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            "  or continue with  ",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OAuthButtons(
    isLoading: Boolean,
    onOAuthClick: (OAuthProvider) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Google Sign In
        OutlinedButton(
            onClick = { onOAuthClick(OAuthProvider.Google) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("oauth_google_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFFEA4335))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }

        // Apple Sign In
        OutlinedButton(
            onClick = { onOAuthClick(OAuthProvider.Apple) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("oauth_apple_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.PhoneIphone, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Apple", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }

        // Facebook / Meta Sign In
        OutlinedButton(
            onClick = { onOAuthClick(OAuthProvider.Facebook) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("oauth_facebook_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF1877F2))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Facebook (Meta)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: SecurityValidator.PasswordStrength) {
    val color = when {
        strength.score >= 80 -> Color(0xFF22C55E)
        strength.score >= 60 -> GoldPrimary
        strength.score >= 40 -> Color(0xFFF97316)
        else -> Color(0xFFEF4444)
    }

    val label = when {
        strength.score >= 80 -> "Strong Password"
        strength.score >= 60 -> "Good"
        strength.score >= 40 -> "Fair"
        else -> "Weak"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Password Strength", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }

        LinearProgressIndicator(
            progress = { strength.score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StrengthBadge("8+ Chars", strength.isLengthValid)
            StrengthBadge("A-Z", strength.hasUppercase)
            StrengthBadge("a-z", strength.hasLowercase)
            StrengthBadge("0-9", strength.hasDigit)
            StrengthBadge("Symbol", strength.hasSpecialChar)
        }
    }
}

@Composable
private fun StrengthBadge(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            if (isMet) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isMet) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
