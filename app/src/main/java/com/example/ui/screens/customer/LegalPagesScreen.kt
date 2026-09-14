package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

enum class LegalTab(val title: String) {
    TERMS("Terms of Service"),
    PRIVACY("POPIA Privacy Policy"),
    BUSINESS_TERMS("Business Merchant Agreement"),
    COOKIES("Cookie Policy"),
    COMMUNITY("Community & Review Guidelines")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalPagesScreen(
    initialTab: LegalTab = LegalTab.TERMS,
    companyRegPlaceholder: String = "StyleHub Technologies (Pty) Ltd [Reg: 2026/012948/07 (Pending Final Verification)]",
    supportEmail: String = "support@stylehub.co.za",
    infoOfficerEmail: String = "privacy@stylehub.co.za",
    onBack: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Legal & Compliance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Republic of South Africa • POPIA Act 4 of 2013",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("legal_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .testTag("legal_pages_screen")
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                contentColor = GoldPrimary
            ) {
                LegalTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Legal Content Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Regulatory Notice Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.1f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary.copy(alpha = 0.3f)))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Official Commercial Disclosure",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Operating Entity: $companyRegPlaceholder. All terms comply with the Consumer Protection Act (CPA) and POPIA.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                when (selectedTab) {
                    LegalTab.TERMS -> TermsOfServiceContent(supportEmail)
                    LegalTab.PRIVACY -> PrivacyPolicyContent(infoOfficerEmail, supportEmail)
                    LegalTab.BUSINESS_TERMS -> BusinessMerchantTermsContent(supportEmail)
                    LegalTab.COOKIES -> CookiePolicyContent(supportEmail)
                    LegalTab.COMMUNITY -> CommunityGuidelinesContent(supportEmail)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TermsOfServiceContent(supportEmail: String) {
    LegalSectionHeader("1. Acceptance of Terms")
    LegalParagraph(
        "By accessing or utilizing the StyleHub web or mobile platform, you acknowledge and agree to be bound by these Terms of Service. If you do not agree to these terms, you must not access or use the services."
    )

    LegalSectionHeader("2. Marketplace Platform Model")
    LegalParagraph(
        "StyleHub operates as an intermediary software marketplace connecting consumers with independent barbers, braiders, stylists, and salons across South Africa. StyleHub provides directory, scheduling, discovery, and communication technology. StyleHub does not directly employ service providers or independently perform barbering or cosmetology treatments."
    )

    LegalSectionHeader("3. Appointments, Bookings & Payments")
    LegalParagraph(
        "Booking requests made through StyleHub are confirmations of intent between the customer and the business partner. During the current MVP release, payments for services are settled directly at the salon via Cash, Card Machine, or Instant EFT upon service completion. All prices listed on StyleHub are in South African Rand (ZAR)."
    )

    LegalSectionHeader("4. Cancellation & No-Show Standards")
    LegalParagraph(
        "Customers are encouraged to provide at least 2 hours advance notice when cancelling an appointment. Repeated failure to attend confirmed appointments without prior communication may result in temporary account restrictions."
    )

    LegalSectionHeader("5. Customer Support & Contact")
    LegalParagraph(
        "For legal inquiries, dispute resolution assistance, or general queries, please contact our support desk at $supportEmail."
    )
}

@Composable
private fun PrivacyPolicyContent(infoOfficerEmail: String, supportEmail: String) {
    LegalSectionHeader("1. Commitment to POPIA Compliance")
    LegalParagraph(
        "StyleHub strictly adheres to the Protection of Personal Information Act No. 4 of 2013 (POPIA) and the Electronic Communications and Transactions Act (ECTA). We are committed to safeguarding the personal information and privacy rights of all South African users."
    )

    LegalSectionHeader("2. Personal Information Collected")
    LegalParagraph(
        "We collect only information necessary to deliver marketplace services, including: Full Name, Email Address, South African Mobile Phone Number (+27), Preferred Location (Province, City, Suburb), and Appointment Booking History. For business partners, we also record business name, trading address, service catalog, and operating hours."
    )

    LegalSectionHeader("3. Purpose of Processing")
    LegalParagraph(
        "Personal information is processed solely for account authentication, booking confirmation notifications, salon coordination, security audit logging, and customer support. We never sell or lease user personal information to third-party marketing brokers."
    )

    LegalSectionHeader("4. Information Officer & POPIA Rights")
    LegalParagraph(
        "Under POPIA, you retain the right to access, rectify, or request deletion of your personal data at any time via your Account Security settings. Our designated Information Officer can be reached directly at $infoOfficerEmail or $supportEmail."
    )
}

@Composable
private fun BusinessMerchantTermsContent(supportEmail: String) {
    LegalSectionHeader("1. Business Partner Eligibility")
    LegalParagraph(
        "Barbershops, hair salons, independent stylists, and beauty studios operating within the Republic of South Africa are eligible to list services on StyleHub. Partners must maintain valid municipal permits, health compliance standards, and deliver professional services."
    )

    LegalSectionHeader("2. Pricing Transparency Mandate")
    LegalParagraph(
        "Partners agree that all service pricing published on StyleHub reflects accurate, non-misleading South African Rand (ZAR) amounts. Hidden surcharges or unauthorized price changes upon client arrival are strictly prohibited."
    )

    LegalSectionHeader("3. MVP Listing Tier & Commission Model")
    LegalParagraph(
        "During the current StyleHub Marketplace rollout, standard business listings and booking requests are provided at 0% platform commission. Premium promotional tools and automated payment settlements may be introduced under transparent commercial addenda in future updates."
    )
}

@Composable
private fun CookiePolicyContent(supportEmail: String) {
    LegalSectionHeader("1. Local Storage & Session State")
    LegalParagraph(
        "StyleHub utilizes modern web local storage and secure on-device Android Room database caching to maintain user authentication sessions, save favorite salons, and remember your location filters. We do not employ third-party behavioral cross-site trackers."
    )

    LegalSectionHeader("2. Managing Preferences")
    LegalParagraph(
        "You can clear local session tokens and device storage at any time by signing out or using the 'Revoke All Sessions' tool in the Account Security Dashboard."
    )
}

@Composable
private fun CommunityGuidelinesContent(supportEmail: String) {
    LegalSectionHeader("1. Authentic Customer Reviews")
    LegalParagraph(
        "Reviews on StyleHub must reflect genuine, first-hand experiences from confirmed appointments. Fabricated reviews, competitor defamation, hate speech, harassment, and discriminatory behavior are strictly prohibited and result in immediate permanent banning."
    )

    LegalSectionHeader("2. Professionalism & Respect")
    LegalParagraph(
        "We foster a welcoming, diverse community celebrating African hair textures, modern styling trends, and professional entrepreneurship. Stylists and clients are expected to treat one another with dignity and punctuality."
    )
}

@Composable
private fun LegalSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun LegalParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
    )
}
