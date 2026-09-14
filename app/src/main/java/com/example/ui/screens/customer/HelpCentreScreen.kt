package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.SupportTicket

data class HelpArticle(
    val id: String,
    val category: String,
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCentreScreen(
    supportEmail: String = "support@stylehub.co.za",
    supportPhone: String = "+27 10 824 9000",
    userEmail: String = "",
    tickets: List<SupportTicket> = emptyList(),
    onSubmitTicket: (category: String, subject: String, description: String, priority: String, email: String) -> String,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showTicketModal by remember { mutableStateOf(false) }

    val categories = listOf("All", "Booking & Appointments", "Business Listings", "Account & Security", "Payments & Pricing", "Safety & POPIA")

    val articles = remember {
        listOf(
            HelpArticle(
                id = "1",
                category = "Booking & Appointments",
                question = "How do I book an appointment on StyleHub?",
                answer = "Browse or search for your favorite salon, select a service and stylist, choose your preferred date and time, and click 'Request Appointment'. The salon will instantly receive your request and confirm."
            ),
            HelpArticle(
                id = "2",
                category = "Booking & Appointments",
                question = "How do I cancel or reschedule a booking?",
                answer = "Go to the 'Appointments' tab in your StyleHub app, select the upcoming booking, and tap 'Cancel Appointment'. Please try to provide at least 2 hours advance notice."
            ),
            HelpArticle(
                id = "3",
                category = "Payments & Pricing",
                question = "How and when do I pay for my service?",
                answer = "In the current StyleHub release, all payments are settled in South African Rand (ZAR) directly at the salon upon completion of your styling service via Cash, Card machine, or Instant EFT."
            ),
            HelpArticle(
                id = "4",
                category = "Business Listings",
                question = "How can I list my barber shop or hair salon?",
                answer = "Tap 'For Businesses' in the top navigation or bottom menu, then click 'List Your Business'. Complete the 3-step registration with your service menu, pricing in ZAR, and operating hours."
            ),
            HelpArticle(
                id = "5",
                category = "Business Listings",
                question = "What does the 'Verified Partner' badge mean?",
                answer = "The gold 'Verified Partner' badge signifies that our StyleHub South Africa operations team has confirmed the physical location, contact details, and professional credentials of the salon."
            ),
            HelpArticle(
                id = "6",
                category = "Account & Security",
                question = "How do I set up Two-Factor Authentication (2FA)?",
                answer = "Go to Profile > Account Security & Identity > Enable Multi-Factor Authentication. Scan the TOTP secret with Google Authenticator, 1Password, or Microsoft Authenticator, and enter the 6-digit code to activate."
            ),
            HelpArticle(
                id = "7",
                category = "Safety & POPIA",
                question = "How is my personal information protected under POPIA?",
                answer = "StyleHub operates in strict compliance with South Africa's POPIA Act 4 of 2013. Your telephone numbers and appointment records are encrypted and never shared with third-party brokers."
            )
        )
    }

    val filteredArticles = remember(searchQuery, selectedCategoryFilter) {
        articles.filter { art ->
            val matchesCat = selectedCategoryFilter == "All" || art.category == selectedCategoryFilter
            val matchesQuery = searchQuery.isBlank() ||
                    art.question.contains(searchQuery, ignoreCase = true) ||
                    art.answer.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Help Centre & Support",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "South Africa Support Desk • 24/7 Knowledge Base",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("help_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showTicketModal = true },
                        modifier = Modifier.testTag("open_support_ticket_btn"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = GoldPrimary.copy(alpha = 0.2f),
                            contentColor = GoldPrimary
                        )
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Contact Desk", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Help Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "How can we assist you today?",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Search answers for booking appointments, listing your South African salon, payment questions, or account security.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search help articles (e.g. 'cancellation', 'pricing', '2FA')") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }

            // Quick Direct Contact Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Text("Email Support", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                        Text(supportEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                            Text("Helpline", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                        Text(supportPhone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Category Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Browse by Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // FAQ Articles List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Frequently Asked Questions (${filteredArticles.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (filteredArticles.isEmpty()) {
                    Text(
                        text = "No matching articles found. You can submit a support ticket directly below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    filteredArticles.forEach { article ->
                        var isExpanded by remember { mutableStateOf(false) }
                        OutlinedCard(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = article.question,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = GoldPrimary
                                    )
                                }
                                AnimatedVisibility(visible = isExpanded) {
                                    Column {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = article.answer,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Active Support Tickets Section
            if (tickets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your Support Inquiries & Tickets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                tickets.forEach { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ticket.id,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    fontSize = 13.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (ticket.status.contains("Resolved")) Color(0xFF2E7D32).copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = ticket.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ticket.status.contains("Resolved")) Color(0xFF2E7D32) else GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(text = ticket.subject, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(text = ticket.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Logged: ${ticket.createdAt} • Category: ${ticket.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Support Ticket Submission Modal Dialog
    if (showTicketModal) {
        SupportTicketDialog(
            userEmail = userEmail,
            onDismiss = { showTicketModal = false },
            onSubmit = { cat, sub, desc, pri, em ->
                onSubmitTicket(cat, sub, desc, pri, em)
                showTicketModal = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportTicketDialog(
    userEmail: String,
    onDismiss: () -> Unit,
    onSubmit: (category: String, subject: String, description: String, priority: String, email: String) -> Unit
) {
    var category by remember { mutableStateOf("Booking & Appointments") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") }
    var email by remember { mutableStateOf(userEmail.ifBlank { "client@stylehub.co.za" }) }

    val categories = listOf("Booking & Appointments", "Business Account & Listing", "Payment Query", "Account Security & 2FA", "POPIA Data Request", "Technical Bug / Other")
    val priorities = listOf("Low", "Normal", "High", "Urgent")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = GoldPrimary)
                Text("Submit Support Ticket", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Our South African support desk responds within 24 hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Contact Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    category = c
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Summary") },
                    placeholder = { Text("Brief issue description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description") },
                    placeholder = { Text("Provide details, salon name, or booking reference...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Priority", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && description.isNotBlank()) {
                        onSubmit(category, subject, description, priority, email)
                    }
                },
                enabled = subject.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark),
                modifier = Modifier.testTag("submit_ticket_confirm_btn")
            ) {
                Text("Log Ticket", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
