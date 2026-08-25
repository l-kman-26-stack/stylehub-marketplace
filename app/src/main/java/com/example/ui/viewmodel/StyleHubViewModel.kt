package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.*
import com.example.data.repository.StyleHubRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class CustomerTab {
    HOME,
    EXPLORE,
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
    val city: String = "All Cities",
    val suburb: String = "All Suburbs",
    val onlyOpenNow: Boolean = false,
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

    // All Businesses (Admin)
    val allBusinessesAdmin: StateFlow<List<BusinessEntity>> = repository.allBusinessesAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Businesses Stream
    val filteredBusinesses: StateFlow<List<BusinessEntity>> = combine(
        approvedBusinesses,
        _filterState
    ) { businesses, filters ->
        businesses.filter { biz ->
            val matchesQuery = filters.query.isBlank() ||
                    biz.name.contains(filters.query, ignoreCase = true) ||
                    biz.category.contains(filters.query, ignoreCase = true) ||
                    biz.description.contains(filters.query, ignoreCase = true) ||
                    biz.suburb.contains(filters.query, ignoreCase = true) ||
                    biz.city.contains(filters.query, ignoreCase = true)

            val matchesCategory = filters.category == null || biz.category.equals(filters.category, ignoreCase = true)

            val matchesPrice = when (filters.priceFilter) {
                PriceFilter.ALL -> true
                PriceFilter.UNDER_150 -> biz.minPriceZar <= 150
                PriceFilter.R150_250 -> biz.minPriceZar in 150.0..250.0
                PriceFilter.R250_400 -> biz.minPriceZar in 250.0..400.0
                PriceFilter.OVER_400 -> biz.minPriceZar >= 400.0
            }

            val matchesRating = biz.rating >= filters.ratingFilter.minRating

            val matchesCity = filters.city == "All Cities" || biz.city.equals(filters.city, ignoreCase = true)

            val matchesSuburb = filters.suburb == "All Suburbs" || biz.suburb.equals(filters.suburb, ignoreCase = true)

            matchesQuery && matchesCategory && matchesPrice && matchesRating && matchesCity && matchesSuburb
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

    // --- Actions ---

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

    fun updateCityFilter(city: String) {
        _filterState.value = _filterState.value.copy(city = city)
    }

    fun updateSortOption(sort: SortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sort)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
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
