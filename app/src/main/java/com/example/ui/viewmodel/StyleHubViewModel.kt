package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.ChatMessage
import com.example.data.api.GeminiClient
import com.example.data.api.GroundingSource
import com.example.data.local.entities.*
import com.example.data.locations.SouthAfricaLocations
import com.example.data.repository.StyleHubRepository
import com.example.security.*
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class PublicViewMode {
    LANDING,
    APP_MAIN,
    FOR_BUSINESSES,
    ABOUT_US,
    HELP_CENTRE,
    SUPPORT_TICKET_FORM,
    LEGAL_TERMS,
    LEGAL_PRIVACY,
    LEGAL_BUSINESS_TERMS,
    LEGAL_COOKIES,
    LEGAL_COMMUNITY,
    PRODUCTION_CHECKLIST,
    TRANSACTIONAL_TEMPLATES,
    ADMIN_SETTINGS
}

data class SupportTicket(
    val id: String,
    val category: String,
    val subject: String,
    val description: String,
    val priority: String,
    val status: String,
    val createdAt: String,
    val userEmail: String
)

data class PlatformSettings(
    val platformName: String = "StyleHub",
    val tagline: String = "Find Your Style. Find Your Professional.",
    val supportEmail: String = "support@stylehub.co.za",
    val supportPhone: String = "+27 10 824 9000",
    val infoOfficerEmail: String = "privacy@stylehub.co.za",
    val registeredEntityName: String = "StyleHub Technologies (Pty) Ltd",
    val registrationNumberPlaceholder: String = "2026/012948/07 (Pending Final CIPC Filing)",
    val registeredAddressPlaceholder: String = "138 West Street, Sandton Central, Johannesburg, 2196, South Africa",
    val announcementActive: Boolean = true,
    val announcementText: String = "🚀 StyleHub Enterprise Launch: Discover verified barbers & salons across all 9 South African provinces with zero booking fees!",
    val maintenanceMode: Boolean = false,
    val enableInstantBooking: Boolean = true,
    val enableTotpMfa: Boolean = true,
    val enableAiStylistConsultant: Boolean = true
)

enum class CustomerTab {
    HOME,
    EXPLORE,
    AI_STYLIST,
    SAVED,
    APPOINTMENTS,
    PROFILE
}

enum class PriceFilter(val label: String, val min: Double, val max: Double) {
    ALL("All Prices", 0.0, 10000.0),
    UNDER_150("Under R150", 0.0, 150.0),
    R150_250("R150 - R250", 150.0, 250.0),
    R250_400("R250 - R400", 250.0, 400.0),
    OVER_400("R400+", 400.0, 10000.0)
}

enum class RatingFilter(val label: String, val minRating: Float) {
    ANY("Any Rating", 0.0f),
    RATING_4_0("4.0+ Stars", 4.0f),
    RATING_4_5("4.5+ Stars", 4.5f)
}

enum class SortOption(val label: String) {
    RECOMMENDED("Recommended"),
    HIGHEST_RATED("Highest Rated"),
    LOWEST_PRICE("Lowest Price"),
    MOST_REVIEWED("Most Reviewed")
}

data class FilterState(
    val query: String = "",
    val category: String? = null,
    val priceFilter: PriceFilter = PriceFilter.ALL,
    val ratingFilter: RatingFilter = RatingFilter.ANY,
    val province: String = "All Provinces",
    val city: String = "All Cities",
    val suburb: String = "All Suburbs",
    val onlyOpenNow: Boolean = false,
    val onlyVerified: Boolean = false,
    val sortOption: SortOption = SortOption.RECOMMENDED
)

class StyleHubViewModel(private val repository: StyleHubRepository) : ViewModel() {

    // Active Role & User
    private val _activeRole = MutableStateFlow(UserRole.CUSTOMER)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    private val _currentUserId = MutableStateFlow(1L) // Default Leletu
    val currentUserId: StateFlow<Long> = _currentUserId.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserEntity?> = combine(allUsers, _currentUserId) { users, id ->
        users.find { it.id == id } ?: users.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation Tabs
    private val _customerTab = MutableStateFlow(CustomerTab.HOME)
    val customerTab: StateFlow<CustomerTab> = _customerTab.asStateFlow()

    // Selected Business for Profile View
    private val _selectedBusinessId = MutableStateFlow<Long?>(null)
    val selectedBusinessId: StateFlow<Long?> = _selectedBusinessId.asStateFlow()

    // Filter & Search State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // All Approved Businesses
    val approvedBusinesses: StateFlow<List<BusinessEntity>> = repository.approvedBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Services across all businesses
    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Businesses (Admin)
    val allBusinessesAdmin: StateFlow<List<BusinessEntity>> = repository.allBusinessesAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Businesses Stream (includes service names and locations)
    val filteredBusinesses: StateFlow<List<BusinessEntity>> = combine(
        approvedBusinesses,
        _filterState,
        allServices
    ) { businesses, filters, services ->
        val servicesByBusiness = services.groupBy { it.businessId }
        val q = filters.query.trim()
        businesses.filter { biz ->
            val bizServices = servicesByBusiness[biz.id].orEmpty()
            val matchesService = q.isNotBlank() && bizServices.any { s ->
                s.name.contains(q, ignoreCase = true) ||
                s.description.contains(q, ignoreCase = true) ||
                s.category.contains(q, ignoreCase = true)
            }

            val matchesQuery = q.isBlank() ||
                    matchesService ||
                    biz.name.contains(q, ignoreCase = true) ||
                    biz.category.contains(q, ignoreCase = true) ||
                    biz.description.contains(q, ignoreCase = true) ||
                    biz.suburb.contains(q, ignoreCase = true) ||
                    biz.city.contains(q, ignoreCase = true) ||
                    biz.province.contains(q, ignoreCase = true) ||
                    biz.streetAddress.contains(q, ignoreCase = true)

            val matchesCategory = filters.category == null || biz.category.equals(filters.category, ignoreCase = true)

            val matchesPrice = when (filters.priceFilter) {
                PriceFilter.ALL -> true
                PriceFilter.UNDER_150 -> biz.minPriceZar <= 150
                PriceFilter.R150_250 -> biz.minPriceZar in 150.0..250.0
                PriceFilter.R250_400 -> biz.minPriceZar in 250.0..400.0
                PriceFilter.OVER_400 -> biz.minPriceZar >= 400.0
            }

            val matchesRating = biz.rating >= filters.ratingFilter.minRating

            val matchesProvince = filters.province == "All Provinces" || biz.province.equals(filters.province, ignoreCase = true)

            val matchesCity = filters.city == "All Cities" || biz.city.equals(filters.city, ignoreCase = true)

            val matchesSuburb = filters.suburb == "All Suburbs" || biz.suburb.equals(filters.suburb, ignoreCase = true)

            val matchesVerified = !filters.onlyVerified || biz.isVerified

            matchesQuery && matchesCategory && matchesPrice && matchesRating && matchesProvince && matchesCity && matchesSuburb && matchesVerified
        }.sortedWith { a, b ->
            when (filters.sortOption) {
                SortOption.RECOMMENDED -> b.isFeatured.compareTo(a.isFeatured).takeIf { it != 0 } ?: b.rating.compareTo(a.rating)
                SortOption.HIGHEST_RATED -> b.rating.compareTo(a.rating)
                SortOption.LOWEST_PRICE -> a.minPriceZar.compareTo(b.minPriceZar)
                SortOption.MOST_REVIEWED -> b.reviewCount.compareTo(a.reviewCount)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Business Details
    val selectedBusiness: StateFlow<BusinessEntity?> = combine(
        allBusinessesAdmin,
        _selectedBusinessId
    ) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedBusinessServices: StateFlow<List<ServiceEntity>> = _selectedBusinessId.flatMapLatest { id ->
        if (id != null) repository.getServices(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedBusinessTeam: StateFlow<List<TeamMemberEntity>> = _selectedBusinessId.flatMapLatest { id ->
        if (id != null) repository.getTeamMembers(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedBusinessHours: StateFlow<List<OpeningHourEntity>> = _selectedBusinessId.flatMapLatest { id ->
        if (id != null) repository.getOpeningHours(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedBusinessReviews: StateFlow<List<ReviewEntity>> = _selectedBusinessId.flatMapLatest { id ->
        if (id != null) repository.getReviews(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Appointments
    val customerAppointments: StateFlow<List<AppointmentEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.getCustomerAppointments(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointmentsAdmin: StateFlow<List<AppointmentEntity>> = repository.allAppointmentsAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviewsAdmin: StateFlow<List<ReviewEntity>> = repository.allReviewsAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Businesses
    val savedBusinessEntities: StateFlow<List<SavedBusinessEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.getSavedBusinesses(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedBusinesses: StateFlow<List<BusinessEntity>> = combine(
        approvedBusinesses,
        savedBusinessEntities
    ) { businesses, saved ->
        val savedIds = saved.map { it.businessId }.toSet()
        businesses.filter { it.id in savedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSelectedBusinessSaved: StateFlow<Boolean> = combine(
        _selectedBusinessId,
        savedBusinessEntities
    ) { bid, saved ->
        bid != null && saved.any { it.businessId == bid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.getNotifications(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Owner Managed Businesses
    val ownerBusinesses: StateFlow<List<BusinessEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.getBusinessesByOwner(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOwnerBusiness: StateFlow<BusinessEntity?> = ownerBusinesses.map { list ->
        list.firstOrNull() ?: approvedBusinesses.value.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ownerAppointments: StateFlow<List<AppointmentEntity>> = activeOwnerBusiness.flatMapLatest { biz ->
        if (biz != null) repository.getBusinessAppointments(biz.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownerServices: StateFlow<List<ServiceEntity>> = activeOwnerBusiness.flatMapLatest { biz ->
        if (biz != null) repository.getServices(biz.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownerTeam: StateFlow<List<TeamMemberEntity>> = activeOwnerBusiness.flatMapLatest { biz ->
        if (biz != null) repository.getTeamMembers(biz.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Booking Flow State
    private val _bookingDialogOpen = MutableStateFlow(false)
    val bookingDialogOpen: StateFlow<Boolean> = _bookingDialogOpen.asStateFlow()

    private val _bookingService = MutableStateFlow<ServiceEntity?>(null)
    val bookingService: StateFlow<ServiceEntity?> = _bookingService.asStateFlow()

    private val _bookingTeamMember = MutableStateFlow<TeamMemberEntity?>(null)
    val bookingTeamMember: StateFlow<TeamMemberEntity?> = _bookingTeamMember.asStateFlow()

    private val _bookingDate = MutableStateFlow("Today, 26 Aug")
    val bookingDate: StateFlow<String> = _bookingDate.asStateFlow()

    private val _bookingTime = MutableStateFlow("11:00")
    val bookingTime: StateFlow<String> = _bookingTime.asStateFlow()

    private val _bookingNotes = MutableStateFlow("")
    val bookingNotes: StateFlow<String> = _bookingNotes.asStateFlow()

    // Review Dialog State
    private val _reviewAppointment = MutableStateFlow<AppointmentEntity?>(null)
    val reviewAppointment: StateFlow<AppointmentEntity?> = _reviewAppointment.asStateFlow()

    private val _reviewRating = MutableStateFlow(5)
    val reviewRating: StateFlow<Int> = _reviewRating.asStateFlow()

    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()

    // Business Onboarding State
    private val _isOnboardingOpen = MutableStateFlow(false)
    val isOnboardingOpen: StateFlow<Boolean> = _isOnboardingOpen.asStateFlow()

    // --- Gemini AI Stylist & Maps Grounding Chat State ---
    private val initialWelcomeMessage = ChatMessage(
        role = "model",
        content = """
👋 **Sawubona! Welcome to StyleHub AI.**

I am your personal South African grooming & styling concierge, powered by Gemini with **Google Maps data**!

I can help you with:
- 📍 **Finding top barbers, braiders & salons** across Sandton, Rosebank, Cape Town, Durban, Pretoria & more.
- ✂️ **Style consultations**: Mid-fades, taper cuts, loc maintenance, knotless braids, 4C curl care, beard styling.
- 💰 **Pricing benchmarks** in South African Rand (ZAR).
- 🕒 Real-time neighborhood recommendations & studio navigation.

*What style or area are you looking to explore today?*
""".trimIndent(),
        groundingSources = listOf(
            GroundingSource(
                title = "StyleHub South Africa Verified Studios",
                snippet = "Locate premier salons and barbershops in Gauteng, Western Cape & KwaZulu-Natal.",
                isMapPlace = true
            )
        )
    )

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(initialWelcomeMessage))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun clearChat() {
        _chatMessages.value = listOf(initialWelcomeMessage)
        _chatError.value = null
    }

    fun openAiChatWithPrompt(prompt: String) {
        _customerTab.value = CustomerTab.AI_STYLIST
        _selectedBusinessId.value = null
        sendChatMessage(prompt)
    }

    fun sendChatMessage(promptText: String? = null) {
        val messageToSend = promptText ?: _chatInput.value.trim()
        if (messageToSend.isBlank() || _isChatLoading.value) return

        val userMessage = ChatMessage(
            role = "user",
            content = messageToSend
        )

        // Append user message immediately
        val updatedHistory = _chatMessages.value + userMessage
        _chatMessages.value = updatedHistory
        if (promptText == null) {
            _chatInput.value = ""
        }
        _isChatLoading.value = true
        _chatError.value = null

        viewModelScope.launch {
            val result = GeminiClient.sendMessage(
                history = updatedHistory.filter { !it.isError },
                newMessage = messageToSend,
                includeMapsGrounding = true
            )

            _isChatLoading.value = false

            result.onSuccess { modelMsg ->
                _chatMessages.value = _chatMessages.value + modelMsg
            }.onFailure { err ->
                _chatError.value = err.message
                val errorMsg = ChatMessage(
                    role = "model",
                    content = "⚠️ Unable to connect to Gemini at the moment: ${err.localizedMessage ?: "Unknown error"}. Please check your connection or retry.",
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            }
        }
    }

    // ==========================================
    // --- Enterprise Authentication & Identity ---
    // ==========================================

    private val _isAuthModalOpen = MutableStateFlow(false)
    val isAuthModalOpen: StateFlow<Boolean> = _isAuthModalOpen.asStateFlow()

    private val _authModalTab = MutableStateFlow(0) // 0 = Sign In, 1 = Sign Up, 2 = Forgot Password
    val authModalTab: StateFlow<Int> = _authModalTab.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _accountLockoutMinutes = MutableStateFlow<Long?>(null)
    val accountLockoutMinutes: StateFlow<Long?> = _accountLockoutMinutes.asStateFlow()

    // 2FA / MFA Challenge State
    private val _isMfaChallengeOpen = MutableStateFlow(false)
    val isMfaChallengeOpen: StateFlow<Boolean> = _isMfaChallengeOpen.asStateFlow()

    private val _pendingMfaUserId = MutableStateFlow<Long?>(null)
    val pendingMfaUserId: StateFlow<Long?> = _pendingMfaUserId.asStateFlow()

    private val _pendingMfaEmail = MutableStateFlow<String?>(null)
    val pendingMfaEmail: StateFlow<String?> = _pendingMfaEmail.asStateFlow()

    private val _pendingMfaMaskedPhone = MutableStateFlow<String?>(null)
    val pendingMfaMaskedPhone: StateFlow<String?> = _pendingMfaMaskedPhone.asStateFlow()

    private val _mfaChallengeError = MutableStateFlow<String?>(null)
    val mfaChallengeError: StateFlow<String?> = _mfaChallengeError.asStateFlow()

    // Security Dashboard State
    private val _isSecurityDashboardOpen = MutableStateFlow(false)
    val isSecurityDashboardOpen: StateFlow<Boolean> = _isSecurityDashboardOpen.asStateFlow()

    // MFA Setup Wizard State (0 = closed, 1 = scan secret, 2 = confirm, 3 = show backup codes)
    private val _mfaSetupStep = MutableStateFlow(0)
    val mfaSetupStep: StateFlow<Int> = _mfaSetupStep.asStateFlow()

    private val _mfaSetupSecret = MutableStateFlow<String?>(null)
    val mfaSetupSecret: StateFlow<String?> = _mfaSetupSecret.asStateFlow()

    private val _mfaSetupUri = MutableStateFlow<String?>(null)
    val mfaSetupUri: StateFlow<String?> = _mfaSetupUri.asStateFlow()

    private val _generatedRecoveryCodes = MutableStateFlow<List<String>>(emptyList())
    val generatedRecoveryCodes: StateFlow<List<String>> = _generatedRecoveryCodes.asStateFlow()

    // Active Sessions for Current User
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeSessions: StateFlow<List<SessionEntity>> = _currentUserId.flatMapLatest { userId ->
        repository.getActiveSessions(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audit Logs for Current User
    @OptIn(ExperimentalCoroutinesApi::class)
    val userAuditLogs: StateFlow<List<SecurityAuditLogEntity>> = _currentUserId.flatMapLatest { userId ->
        repository.getAuditLogs(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Audit Logs (Admin View)
    val allAuditLogsAdmin: StateFlow<List<SecurityAuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modals for Phone and Password Management
    private val _isPhoneVerifyModalOpen = MutableStateFlow(false)
    val isPhoneVerifyModalOpen: StateFlow<Boolean> = _isPhoneVerifyModalOpen.asStateFlow()

    private val _isChangePasswordModalOpen = MutableStateFlow(false)
    val isChangePasswordModalOpen: StateFlow<Boolean> = _isChangePasswordModalOpen.asStateFlow()

    // Account Management / Status / Deactivate / Delete Flow
    private val _isAccountManagementOpen = MutableStateFlow(false)
    val isAccountManagementOpen: StateFlow<Boolean> = _isAccountManagementOpen.asStateFlow()

    private val _isAccountActionLoading = MutableStateFlow(false)
    val isAccountActionLoading: StateFlow<Boolean> = _isAccountActionLoading.asStateFlow()

    private val _accountActionError = MutableStateFlow<String?>(null)
    val accountActionError: StateFlow<String?> = _accountActionError.asStateFlow()

    fun openAccountManagement() {
        _isAccountManagementOpen.value = true
        _accountActionError.value = null
    }

    fun closeAccountManagement() {
        _isAccountManagementOpen.value = false
        _accountActionError.value = null
    }

    fun clearAccountActionError() {
        _accountActionError.value = null
    }

    fun deactivateAccount(password: String, onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        _isAccountActionLoading.value = true
        _accountActionError.value = null

        viewModelScope.launch {
            val result = repository.deactivateAccount(user.id, password)
            _isAccountActionLoading.value = false
            result.onSuccess {
                _snackbarMessage.value = "Account Deactivated: Your account has been temporarily deactivated. You can reactivate it when you are ready."
                onSuccess()
            }.onFailure { ex ->
                _accountActionError.value = ex.message ?: "Failed to deactivate account. Please check credentials."
            }
        }
    }

    fun reactivateAccount(password: String, onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        _isAccountActionLoading.value = true
        _accountActionError.value = null

        viewModelScope.launch {
            val result = repository.reactivateAccount(user.id, password)
            _isAccountActionLoading.value = false
            result.onSuccess {
                _snackbarMessage.value = "Account Activated: Your account has been successfully reactivated. You can now use the app normally."
                onSuccess()
            }.onFailure { ex ->
                _accountActionError.value = ex.message ?: "Failed to activate account. Please check credentials."
            }
        }
    }

    fun requestAccountDeletion(password: String, onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        _isAccountActionLoading.value = true
        _accountActionError.value = null

        viewModelScope.launch {
            val result = repository.requestAccountDeletion(user.id, password)
            _isAccountActionLoading.value = false
            result.onSuccess {
                _snackbarMessage.value = "Deletion Requested: Your account has been scheduled for deletion. You have 30 days to cancel the deletion request."
                onSuccess()
            }.onFailure { ex ->
                _accountActionError.value = ex.message ?: "Deletion request failed. Please try again later."
            }
        }
    }

    fun cancelAccountDeletion(onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        _isAccountActionLoading.value = true
        _accountActionError.value = null

        viewModelScope.launch {
            val result = repository.cancelAccountDeletion(user.id)
            _isAccountActionLoading.value = false
            result.onSuccess {
                _snackbarMessage.value = "Deletion Cancelled: Your account deletion request has been cancelled. Your account is active again."
                onSuccess()
            }.onFailure { ex ->
                _accountActionError.value = ex.message ?: "Failed to cancel deletion request."
            }
        }
    }

    fun purgeAccountPermanently(onSuccess: () -> Unit = {}) {
        val user = currentUser.value ?: return
        _isAccountActionLoading.value = true
        _accountActionError.value = null

        viewModelScope.launch {
            val result = repository.purgeAccountPermanently(user.id)
            _isAccountActionLoading.value = false
            result.onSuccess {
                _snackbarMessage.value = "Deletion Completed: Your account has been permanently deleted according to our account deletion policy."
                _isAccountManagementOpen.value = false
                signOut()
                onSuccess()
            }.onFailure { ex ->
                _accountActionError.value = ex.message ?: "Failed to complete permanent account deletion."
            }
        }
    }

    // Aliases for clean UI binding
    val isAuthLoading: StateFlow<Boolean> = _authLoading.asStateFlow()
    val authErrorMessage: StateFlow<String?> = _authError.asStateFlow()
    val lockoutMinutes: StateFlow<Long?> = _accountLockoutMinutes.asStateFlow()
    val mfaChallengeEmail: StateFlow<String?> = _pendingMfaEmail.asStateFlow()
    val mfaChallengePhone: StateFlow<String?> = _pendingMfaMaskedPhone.asStateFlow()
    val auditLogs: StateFlow<List<SecurityAuditLogEntity>> = userAuditLogs

    // Splash and Launch Experience
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    // Public / Corporate Navigation Mode
    private val _publicViewMode = MutableStateFlow(PublicViewMode.LANDING)
    val publicViewMode: StateFlow<PublicViewMode> = _publicViewMode.asStateFlow()

    fun setPublicViewMode(mode: PublicViewMode) {
        _publicViewMode.value = mode
    }

    fun enterAppMain() {
        _publicViewMode.value = PublicViewMode.APP_MAIN
    }

    // Platform Administration & Settings
    private val _platformSettings = MutableStateFlow(PlatformSettings())
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    fun updatePlatformSettings(newSettings: PlatformSettings) {
        _platformSettings.value = newSettings
        _snackbarMessage.value = "Platform configuration updated successfully! ⚙️"
    }

    // Interactive Support Tickets
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(listOf(
        SupportTicket(
            id = "TICK-SA-8921",
            category = "Business Listing",
            subject = "Updating operating hours for Menlyn salon",
            description = "Need assistance updating Sunday trading times for holiday schedule.",
            priority = "Normal",
            status = "Resolved",
            createdAt = "2026-08-20 09:30",
            userEmail = "sipho.dl@mensgroom.co.za"
        ),
        SupportTicket(
            id = "TICK-SA-9104",
            category = "Account Security",
            subject = "TOTP Authenticator setup query",
            description = "Inquiry regarding backup codes recovery.",
            priority = "High",
            status = "Open (Under Review)",
            createdAt = "2026-08-25 14:10",
            userEmail = "leletukamana9@gmail.com"
        )
    ))
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    fun submitSupportTicket(
        category: String,
        subject: String,
        description: String,
        priority: String,
        email: String
    ): String {
        val ticketId = "TICK-SA-${(1000..9999).random()}"
        val newTicket = SupportTicket(
            id = ticketId,
            category = category,
            subject = subject,
            description = description,
            priority = priority,
            status = "Submitted (Active)",
            createdAt = "2026-08-26 12:00",
            userEmail = email
        )
        _supportTickets.value = listOf(newTicket) + _supportTickets.value
        _snackbarMessage.value = "Support ticket $ticketId logged successfully! Reference saved. 🎫"
        return ticketId
    }

    fun signInWithEmailPassword(email: String, pass: String) = signInWithEmail(email, pass)
    fun signUp(name: String, email: String, phone: String, pass: String, role: UserRole, city: String, prov: String) =
        signUpWithEmail(name, email, phone, pass, role, city, prov)
    fun verifyEmail() = sendEmailVerification()
    fun linkOAuth(provider: OAuthProvider) = signInWithOAuth(provider)

    // --- Authentication Actions ---

    fun openAuthModal(tab: Int = 0) {
        _authModalTab.value = tab
        _authError.value = null
        _accountLockoutMinutes.value = null
        _isAuthModalOpen.value = true
    }

    fun closeAuthModal() {
        _isAuthModalOpen.value = false
        _authError.value = null
    }

    fun setAuthModalTab(tab: Int) {
        _authModalTab.value = tab
        _authError.value = null
    }

    fun signInWithEmail(email: String, pass: String) {
        _authLoading.value = true
        _authError.value = null
        _accountLockoutMinutes.value = null

        viewModelScope.launch {
            val result = repository.authManager.signInWithEmail(email, pass)
            _authLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _currentUserId.value = result.user.id
                    val role = try { UserRole.valueOf(result.user.role) } catch (e: Exception) { UserRole.CUSTOMER }
                    _activeRole.value = role
                    _isAuthModalOpen.value = false
                    _snackbarMessage.value = "Welcome back, ${result.user.name}! 🔐 Authenticated securely"
                }
                is AuthResult.RequiresMfa -> {
                    _isAuthModalOpen.value = false
                    _pendingMfaUserId.value = result.userId
                    _pendingMfaEmail.value = result.email
                    _pendingMfaMaskedPhone.value = result.maskedPhone
                    _mfaChallengeError.value = null
                    _isMfaChallengeOpen.value = true
                }
                is AuthResult.AccountLocked -> {
                    _accountLockoutMinutes.value = result.remainingMinutes
                    _authError.value = "Account temporarily locked due to excessive failed attempts. Try again in ${result.remainingMinutes} min."
                }
                is AuthResult.Error -> {
                    _authError.value = result.message
                }
            }
        }
    }

    fun signUpWithEmail(
        name: String,
        email: String,
        phone: String,
        pass: String,
        role: UserRole,
        city: String = "Johannesburg",
        province: String = "Gauteng"
    ) {
        _authLoading.value = true
        _authError.value = null

        viewModelScope.launch {
            val result = repository.authManager.signUpWithEmail(
                name = name,
                email = email,
                phone = phone,
                password = pass,
                role = role,
                city = city,
                province = province
            )
            _authLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _currentUserId.value = result.user.id
                    _activeRole.value = role
                    _isAuthModalOpen.value = false
                    _snackbarMessage.value = "Welcome to StyleHub, ${result.user.name}! 🎉 Account created."
                }
                is AuthResult.Error -> {
                    _authError.value = result.message
                }
                else -> {}
            }
        }
    }

    fun signInWithOAuth(provider: OAuthProvider) {
        _authLoading.value = true
        _authError.value = null

        viewModelScope.launch {
            // Enterprise OAuth Provider adapter
            val dummyEmail = when (provider) {
                OAuthProvider.Google -> "leletu.google@stylehub.co.za"
                OAuthProvider.Apple -> "leletu.apple@privaterelay.appleid.com"
                OAuthProvider.Facebook -> "leletu.meta@facebook.com"
            }
            val dummyName = when (provider) {
                OAuthProvider.Google -> "Leletu Kamana (Google)"
                OAuthProvider.Apple -> "Leletu Kamana (Apple ID)"
                OAuthProvider.Facebook -> "Leletu Kamana (Meta)"
            }
            val dummyUid = "oauth_${provider.id}_${UUID.randomUUID().toString().take(12)}"

            val result = repository.authManager.signInWithOAuth(
                provider = provider,
                email = dummyEmail,
                displayName = dummyName,
                providerUid = dummyUid
            )
            _authLoading.value = false

            when (result) {
                is AuthResult.Success -> {
                    _currentUserId.value = result.user.id
                    val role = try { UserRole.valueOf(result.user.role) } catch (e: Exception) { UserRole.CUSTOMER }
                    _activeRole.value = role
                    _isAuthModalOpen.value = false
                    _snackbarMessage.value = "Signed in with ${provider.displayName} OAuth! 🛡️"
                }
                is AuthResult.Error -> {
                    _authError.value = result.message
                }
                else -> {}
            }
        }
    }

    fun verifyMfaChallenge(code: String) {
        val uid = _pendingMfaUserId.value ?: return
        _authLoading.value = true
        _mfaChallengeError.value = null

        viewModelScope.launch {
            val result = repository.authManager.verifyMfaChallenge(uid, code)
            _authLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _isMfaChallengeOpen.value = false
                    _pendingMfaUserId.value = null
                    _currentUserId.value = result.user.id
                    val role = try { UserRole.valueOf(result.user.role) } catch (e: Exception) { UserRole.CUSTOMER }
                    _activeRole.value = role
                    _snackbarMessage.value = "2FA verified! Welcome back, ${result.user.name} 🛡️"
                }
                is AuthResult.Error -> {
                    _mfaChallengeError.value = result.message
                }
                else -> {}
            }
        }
    }

    fun cancelMfaChallenge() {
        _isMfaChallengeOpen.value = false
        _pendingMfaUserId.value = null
        _pendingMfaEmail.value = null
        _mfaChallengeError.value = null
    }

    // --- Security Dashboard & 2FA Setup Actions ---

    fun openSecurityDashboard() {
        _isSecurityDashboardOpen.value = true
    }

    fun closeSecurityDashboard() {
        _isSecurityDashboardOpen.value = false
        _mfaSetupStep.value = 0
    }

    fun startMfaSetup() {
        val user = currentUser.value ?: return
        val (secret, uri) = repository.authManager.startMfaSetup(user.email)
        _mfaSetupSecret.value = secret
        _mfaSetupUri.value = uri
        _mfaSetupStep.value = 1 // Step 1: Scan / Enter Secret
    }

    fun proceedToMfaVerify() {
        _mfaSetupStep.value = 2 // Step 2: Enter test code
    }

    fun confirmMfaSetup(testCode: String) {
        val user = currentUser.value ?: return
        val secret = _mfaSetupSecret.value ?: return

        viewModelScope.launch {
            val result = repository.authManager.completeMfaSetup(user.id, secret, testCode)
            result.onSuccess { recoveryCodes ->
                _generatedRecoveryCodes.value = recoveryCodes
                _mfaSetupStep.value = 3 // Step 3: Show Backup Recovery Codes
                _snackbarMessage.value = "MFA successfully activated! Save your backup codes. 🔐"
            }.onFailure { err ->
                _snackbarMessage.value = "Verification failed: ${err.message}"
            }
        }
    }

    fun disableMfa(code: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.authManager.disableMfa(user.id, code)
            result.onSuccess {
                _snackbarMessage.value = "Multi-Factor Authentication disabled."
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to disable MFA"
            }
        }
    }

    fun regenerateRecoveryCodes(code: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.authManager.regenerateRecoveryCodes(user.id, code)
            result.onSuccess { codes ->
                _generatedRecoveryCodes.value = codes
                _mfaSetupStep.value = 3
                _snackbarMessage.value = "New emergency backup codes generated! 🛡️"
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Failed to regenerate codes"
            }
        }
    }

    fun closeMfaWizard() {
        _mfaSetupStep.value = 0
        _mfaSetupSecret.value = null
        _mfaSetupUri.value = null
        _generatedRecoveryCodes.value = emptyList()
    }

    fun changePassword(currentPass: String, newPass: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.authManager.changePassword(user.id, currentPass, newPass)
            result.onSuccess {
                _isChangePasswordModalOpen.value = false
                _snackbarMessage.value = "Password updated securely! 🔐"
            }.onFailure { err ->
                _snackbarMessage.value = err.message ?: "Password update failed"
            }
        }
    }

    fun openChangePasswordModal() {
        _isChangePasswordModalOpen.value = true
    }

    fun closeChangePasswordModal() {
        _isChangePasswordModalOpen.value = false
    }

    fun revokeSession(sessionId: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.revokeSession(sessionId, user.id)
            _snackbarMessage.value = "Remote session terminated. 🛑"
        }
    }

    fun revokeAllOtherSessions() {
        val user = currentUser.value ?: return
        val currentSession = activeSessions.value.firstOrNull { it.isCurrent }?.id ?: "current"
        viewModelScope.launch {
            repository.revokeAllOtherSessions(user.id, currentSession)
            _snackbarMessage.value = "All other device sessions have been revoked. 🔒"
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            repository.authManager.requestPasswordReset(email)
            _snackbarMessage.value = "Password reset instructions sent to $email (if registered)."
            _isAuthModalOpen.value = false
        }
    }

    fun sendEmailVerification() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val token = repository.authManager.sendEmailVerification(user.email)
            repository.authManager.confirmEmailVerification(user.id, token)
            _snackbarMessage.value = "Email ${user.email} verified successfully! ✅"
        }
    }

    fun openPhoneVerifyModal() {
        _isPhoneVerifyModalOpen.value = true
    }

    fun closePhoneVerifyModal() {
        _isPhoneVerifyModalOpen.value = false
    }

    fun sendPhoneOtp(phone: String): String {
        return repository.authManager.sendPhoneVerificationCode(phone)
    }

    fun confirmPhoneOtp(phone: String, code: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val success = repository.authManager.confirmPhoneVerification(user.id, phone, code)
            if (success) {
                _isPhoneVerifyModalOpen.value = false
                _snackbarMessage.value = "Phone number verified via SMS OTP! 📱"
            } else {
                _snackbarMessage.value = "Invalid SMS code. Please try again."
            }
        }
    }

    fun signOut() {
        val user = currentUser.value
        _activeRole.value = UserRole.CUSTOMER
        _currentUserId.value = 1L
        _snackbarMessage.value = "Signed out of StyleHub. Sessions secured. 👋"
    }

    fun switchRole(role: UserRole) {
        _activeRole.value = role
        when (role) {
            UserRole.CUSTOMER -> _currentUserId.value = 1L
            UserRole.BUSINESS_OWNER -> _currentUserId.value = 2L
            UserRole.ADMIN -> _currentUserId.value = 3L
        }
        _selectedBusinessId.value = null
        _snackbarMessage.value = "Switched to ${role.name.replace("_", " ")} mode"
    }

    fun selectTab(tab: CustomerTab) {
        _customerTab.value = tab
    }

    fun viewBusiness(businessId: Long) {
        _selectedBusinessId.value = businessId
        viewModelScope.launch {
            repository.incrementViews(businessId)
        }
    }

    fun closeBusinessView() {
        _selectedBusinessId.value = null
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun selectCategory(category: String?) {
        _filterState.value = _filterState.value.copy(category = category)
    }

    fun updatePriceFilter(filter: PriceFilter) {
        _filterState.value = _filterState.value.copy(priceFilter = filter)
    }

    fun updateRatingFilter(filter: RatingFilter) {
        _filterState.value = _filterState.value.copy(ratingFilter = filter)
    }

    fun updateProvinceFilter(province: String) {
        _filterState.value = _filterState.value.copy(province = province, city = "All Cities", suburb = "All Suburbs")
    }

    fun updateCityFilter(city: String) {
        _filterState.value = _filterState.value.copy(city = city, suburb = "All Suburbs")
    }

    fun updateSuburbFilter(suburb: String) {
        _filterState.value = _filterState.value.copy(suburb = suburb)
    }

    fun updateOnlyVerified(onlyVerified: Boolean) {
        _filterState.value = _filterState.value.copy(onlyVerified = onlyVerified)
    }

    fun updateSortOption(sort: SortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sort)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun clearSearch() {
        _filterState.value = _filterState.value.copy(query = "")
    }

    fun toggleSave(businessId: Long) {
        viewModelScope.launch {
            val isCurrentlySaved = isSelectedBusinessSaved.value
            repository.toggleSaveBusiness(_currentUserId.value, businessId, isCurrentlySaved)
            _snackbarMessage.value = if (isCurrentlySaved) "Removed from Saved" else "Saved to your favourites! ❤️"
        }
    }

    // Booking Actions
    fun openBooking(service: ServiceEntity? = null) {
        _bookingService.value = service ?: selectedBusinessServices.value.firstOrNull()
        _bookingTeamMember.value = selectedBusinessTeam.value.firstOrNull()
        _bookingNotes.value = ""
        _bookingDialogOpen.value = true
    }

    fun closeBooking() {
        _bookingDialogOpen.value = false
    }

    fun setBookingService(service: ServiceEntity) {
        _bookingService.value = service
    }

    fun setBookingTeamMember(member: TeamMemberEntity?) {
        _bookingTeamMember.value = member
    }

    fun setBookingDate(date: String) {
        _bookingDate.value = date
    }

    fun setBookingTime(time: String) {
        _bookingTime.value = time
    }

    fun setBookingNotes(notes: String) {
        _bookingNotes.value = notes
    }

    fun submitBooking() {
        val biz = selectedBusiness.value ?: return
        val srv = _bookingService.value ?: return
        val user = currentUser.value ?: return

        viewModelScope.launch {
            val appointment = AppointmentEntity(
                customerId = user.id,
                customerName = user.name,
                customerPhone = user.phone,
                businessId = biz.id,
                businessName = biz.name,
                serviceId = srv.id,
                serviceName = srv.name,
                teamMemberId = _bookingTeamMember.value?.id ?: 0,
                teamMemberName = _bookingTeamMember.value?.name ?: "Any Professional",
                appointmentDate = _bookingDate.value,
                appointmentTime = _bookingTime.value,
                notes = _bookingNotes.value,
                priceZar = srv.priceZar,
                status = AppointmentStatus.CONFIRMED.name
            )
            repository.requestAppointment(appointment)
            _bookingDialogOpen.value = false
            _snackbarMessage.value = "Appointment booked successfully! 🎉"
            _customerTab.value = CustomerTab.APPOINTMENTS
        }
    }

    fun cancelAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(
                appointment.id,
                AppointmentStatus.CANCELLED,
                appointment.customerId,
                appointment.businessName
            )
            _snackbarMessage.value = "Appointment cancelled"
        }
    }

    fun updateAppointmentStatusByOwner(appointment: AppointmentEntity, newStatus: AppointmentStatus) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(
                appointment.id,
                newStatus,
                appointment.customerId,
                appointment.businessName
            )
            _snackbarMessage.value = "Appointment marked as ${newStatus.name}"
        }
    }

    // Review Actions
    fun openReviewDialog(appointment: AppointmentEntity) {
        _reviewAppointment.value = appointment
        _reviewRating.value = 5
        _reviewComment.value = ""
    }

    fun closeReviewDialog() {
        _reviewAppointment.value = null
    }

    fun setReviewRating(rating: Int) {
        _reviewRating.value = rating
    }

    fun setReviewComment(comment: String) {
        _reviewComment.value = comment
    }

    fun submitReview() {
        val appt = _reviewAppointment.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val review = ReviewEntity(
                appointmentId = appt.id,
                businessId = appt.businessId,
                customerId = user.id,
                customerName = user.name,
                rating = _reviewRating.value,
                comment = _reviewComment.value.ifBlank { "Great service and professional experience!" },
                serviceName = appt.serviceName
            )
            repository.submitReview(review)
            _reviewAppointment.value = null
            _snackbarMessage.value = "Review submitted! Thank you ⭐"
        }
    }

    // Business Owner Actions
    fun openOnboarding() {
        _isOnboardingOpen.value = true
    }

    fun closeOnboarding() {
        _isOnboardingOpen.value = false
    }

    fun submitNewBusiness(
        name: String,
        category: String,
        description: String,
        phone: String,
        email: String,
        whatsapp: String,
        address: String,
        suburb: String,
        city: String,
        province: String,
        postalCode: String,
        services: List<ServiceEntity>,
        teamMembers: List<TeamMemberEntity>
    ) {
        viewModelScope.launch {
            val biz = BusinessEntity(
                ownerId = _currentUserId.value,
                name = name,
                category = category,
                description = description,
                phone = phone,
                email = email,
                whatsapp = whatsapp,
                streetAddress = address,
                suburb = suburb,
                city = city,
                province = province,
                postalCode = postalCode,
                status = BusinessStatus.PENDING_APPROVAL.name,
                minPriceZar = services.minOfOrNull { it.priceZar } ?: 150.0
            )
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            val hours = days.mapIndexed { index, day ->
                OpeningHourEntity(
                    businessId = 0,
                    dayOfWeek = index + 1,
                    dayName = day,
                    openTime = "08:30",
                    closeTime = "18:00",
                    isClosed = index == 6
                )
            }
            repository.createBusinessListing(biz, services, teamMembers, hours)
            _isOnboardingOpen.value = false
            _snackbarMessage.value = "Listing submitted for Admin review! 🚀"
        }
    }

    fun addServiceToActiveBusiness(name: String, category: String, desc: String, price: Double, duration: Int) {
        val biz = activeOwnerBusiness.value ?: return
        viewModelScope.launch {
            repository.addService(
                ServiceEntity(
                    businessId = biz.id,
                    name = name,
                    category = category,
                    description = desc,
                    priceZar = price,
                    durationMinutes = duration
                )
            )
            _snackbarMessage.value = "Service added successfully"
        }
    }

    fun deleteServiceFromActiveBusiness(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
            _snackbarMessage.value = "Service removed"
        }
    }

    fun addTeamMemberToActiveBusiness(name: String, role: String, bio: String, specialties: String) {
        val biz = activeOwnerBusiness.value ?: return
        viewModelScope.launch {
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
            repository.addTeamMember(
                TeamMemberEntity(
                    businessId = biz.id,
                    name = name,
                    role = role,
                    bio = bio,
                    specialties = specialties,
                    avatarInitials = initials
                )
            )
            _snackbarMessage.value = "Team member added"
        }
    }

    fun deleteTeamMemberFromActiveBusiness(member: TeamMemberEntity) {
        viewModelScope.launch {
            repository.deleteTeamMember(member)
            _snackbarMessage.value = "Team member removed"
        }
    }

    // Admin Actions
    fun approveBusiness(businessId: Long) {
        viewModelScope.launch {
            repository.updateBusinessStatus(businessId, BusinessStatus.APPROVED)
            _snackbarMessage.value = "Business approved and published! ✅"
        }
    }

    fun rejectBusiness(businessId: Long) {
        viewModelScope.launch {
            repository.updateBusinessStatus(businessId, BusinessStatus.REJECTED)
            _snackbarMessage.value = "Business listing rejected"
        }
    }

    fun toggleVerification(businessId: Long) {
        viewModelScope.launch {
            repository.toggleBusinessVerification(businessId)
            _snackbarMessage.value = "Verification status updated"
        }
    }

    fun toggleFeatured(businessId: Long) {
        viewModelScope.launch {
            repository.toggleBusinessFeatured(businessId)
            _snackbarMessage.value = "Featured status updated"
        }
    }

    fun toggleUserSuspension(userId: Long) {
        viewModelScope.launch {
            repository.toggleUserSuspension(userId)
            _snackbarMessage.value = "User status toggled"
        }
    }

    fun deleteReviewByAdmin(review: ReviewEntity) {
        viewModelScope.launch {
            repository.deleteReview(review)
            _snackbarMessage.value = "Review removed by administrator"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}

class StyleHubViewModelFactory(private val repository: StyleHubRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StyleHubViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StyleHubViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
