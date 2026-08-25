package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.local.entities.BusinessEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.StyleHubRepository
import com.example.ui.components.*
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.business.BusinessDashboardScreen
import com.example.ui.screens.business.BusinessOnboardingScreen
import com.example.ui.screens.customer.*
import com.example.ui.theme.StyleHubTheme
import com.example.ui.viewmodel.CustomerTab
import com.example.ui.viewmodel.StyleHubViewModel
import com.example.ui.viewmodel.StyleHubViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: StyleHubViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val repo = StyleHubRepository(db.styleHubDao())
        StyleHubViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StyleHubTheme {
                StyleHubApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleHubApp(viewModel: StyleHubViewModel) {
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val customerTab by viewModel.customerTab.collectAsStateWithLifecycle()
    val selectedBusinessId by viewModel.selectedBusinessId.collectAsStateWithLifecycle()
    val selectedBusiness by viewModel.selectedBusiness.collectAsStateWithLifecycle()
    val selectedBusinessServices by viewModel.selectedBusinessServices.collectAsStateWithLifecycle()
    val selectedBusinessTeam by viewModel.selectedBusinessTeam.collectAsStateWithLifecycle()
    val selectedBusinessHours by viewModel.selectedBusinessHours.collectAsStateWithLifecycle()
    val selectedBusinessReviews by viewModel.selectedBusinessReviews.collectAsStateWithLifecycle()
    val isSelectedBusinessSaved by viewModel.isSelectedBusinessSaved.collectAsStateWithLifecycle()

    val approvedBusinesses by viewModel.approvedBusinesses.collectAsStateWithLifecycle()
    val filteredBusinesses by viewModel.filteredBusinesses.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val savedBusinesses by viewModel.savedBusinesses.collectAsStateWithLifecycle()
    val customerAppointments by viewModel.customerAppointments.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    // Owner State
    val activeOwnerBusiness by viewModel.activeOwnerBusiness.collectAsStateWithLifecycle()
    val ownerAppointments by viewModel.ownerAppointments.collectAsStateWithLifecycle()
    val ownerServices by viewModel.ownerServices.collectAsStateWithLifecycle()
    val ownerTeam by viewModel.ownerTeam.collectAsStateWithLifecycle()
    val isOnboardingOpen by viewModel.isOnboardingOpen.collectAsStateWithLifecycle()

    // Admin State
    val allBusinessesAdmin by viewModel.allBusinessesAdmin.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allAppointmentsAdmin by viewModel.allAppointmentsAdmin.collectAsStateWithLifecycle()
    val allReviewsAdmin by viewModel.allReviewsAdmin.collectAsStateWithLifecycle()

    // Dialog & Sheet States
    val bookingDialogOpen by viewModel.bookingDialogOpen.collectAsStateWithLifecycle()
    val bookingService by viewModel.bookingService.collectAsStateWithLifecycle()
    val bookingTeamMember by viewModel.bookingTeamMember.collectAsStateWithLifecycle()
    val bookingDate by viewModel.bookingDate.collectAsStateWithLifecycle()
    val bookingTime by viewModel.bookingTime.collectAsStateWithLifecycle()
    val bookingNotes by viewModel.bookingNotes.collectAsStateWithLifecycle()

    val reviewAppointment by viewModel.reviewAppointment.collectAsStateWithLifecycle()
    val reviewRating by viewModel.reviewRating.collectAsStateWithLifecycle()
    val reviewComment by viewModel.reviewComment.collectAsStateWithLifecycle()

    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showNotificationSheet by remember { mutableStateOf(false) }

    // Handle Snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Intercept Back Press when in detail view or owner onboarding
    BackHandler(enabled = selectedBusinessId != null) {
        viewModel.closeBusinessView()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedBusinessId == null && !isOnboardingOpen) {
                StyleHubTopBar(
                    currentRole = activeRole,
                    onRoleChange = { role -> viewModel.switchRole(role) },
                    notificationCount = notifications.count { !it.isRead },
                    onNotificationClick = { showNotificationSheet = true },
                    title = when (activeRole) {
                        UserRole.CUSTOMER -> "StyleHub"
                        UserRole.BUSINESS_OWNER -> "Business Hub"
                        UserRole.ADMIN -> "Admin Control"
                    },
                    subtitle = when (activeRole) {
                        UserRole.CUSTOMER -> "Find Your Style. Find Your Professional."
                        UserRole.BUSINESS_OWNER -> activeOwnerBusiness?.name ?: "Business Portal"
                        UserRole.ADMIN -> "Platform Overview & Moderation"
                    }
                )
            }
        },
        bottomBar = {
            if (activeRole == UserRole.CUSTOMER && selectedBusinessId == null && !isOnboardingOpen) {
                StyleHubBottomNav(
                    currentTab = customerTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) },
                    appointmentBadgeCount = customerAppointments.count { it.status == "CONFIRMED" }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (selectedBusinessId != null || isOnboardingOpen) PaddingValues(0.dp) else innerPadding
                )
        ) {
            // Detailed Business View if opened
            if (selectedBusinessId != null && selectedBusiness != null) {
                BusinessProfileScreen(
                    business = selectedBusiness!!,
                    services = selectedBusinessServices,
                    teamMembers = selectedBusinessTeam,
                    openingHours = selectedBusinessHours,
                    reviews = selectedBusinessReviews,
                    isSaved = isSelectedBusinessSaved,
                    onToggleSave = { viewModel.toggleSave(selectedBusiness!!.id) },
                    onBookService = { srv -> viewModel.openBooking(srv) },
                    onBack = { viewModel.closeBusinessView() }
                )
            } else if (isOnboardingOpen) {
                BusinessOnboardingScreen(
                    onDismiss = { viewModel.closeOnboarding() },
                    onSubmit = { name, cat, desc, phone, email, wa, addr, sub, city, prov, post, srvs, team ->
                        viewModel.submitNewBusiness(name, cat, desc, phone, email, wa, addr, sub, city, prov, post, srvs, team)
                    }
                )
            } else {
                // Main Role Driven Screens
                when (activeRole) {
                    UserRole.CUSTOMER -> {
                        when (customerTab) {
                            CustomerTab.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    approvedBusinesses = approvedBusinesses,
                                    onViewBusiness = { id -> viewModel.viewBusiness(id) },
                                    onBookBusiness = { biz ->
                                        viewModel.viewBusiness(biz.id)
                                        viewModel.openBooking()
                                    },
                                    onNavigateTab = { tab -> viewModel.selectTab(tab) },
                                    onSwitchToOwner = { viewModel.switchRole(UserRole.BUSINESS_OWNER) }
                                )
                            }
                            CustomerTab.EXPLORE -> {
                                ExploreScreen(
                                    viewModel = viewModel,
                                    filteredBusinesses = filteredBusinesses,
                                    filterState = filterState,
                                    onViewBusiness = { id -> viewModel.viewBusiness(id) },
                                    onBookBusiness = { biz ->
                                        viewModel.viewBusiness(biz.id)
                                        viewModel.openBooking()
                                    }
                                )
                            }
                            CustomerTab.SAVED -> {
                                SavedScreen(
                                    savedBusinesses = savedBusinesses,
                                    onViewBusiness = { id -> viewModel.viewBusiness(id) },
                                    onBookBusiness = { biz ->
                                        viewModel.viewBusiness(biz.id)
                                        viewModel.openBooking()
                                    },
                                    onToggleSave = { id -> viewModel.toggleSave(id) },
                                    onExplore = { viewModel.selectTab(CustomerTab.EXPLORE) }
                                )
                            }
                            CustomerTab.APPOINTMENTS -> {
                                AppointmentsScreen(
                                    appointments = customerAppointments,
                                    onCancelAppointment = { appt -> viewModel.cancelAppointment(appt) },
                                    onOpenReview = { appt -> viewModel.openReviewDialog(appt) },
                                    onExploreServices = { viewModel.selectTab(CustomerTab.EXPLORE) }
                                )
                            }
                            CustomerTab.PROFILE -> {
                                ProfileScreen(
                                    currentUser = currentUser,
                                    currentRole = activeRole,
                                    onRoleChange = { role -> viewModel.switchRole(role) },
                                    totalBookings = customerAppointments.size,
                                    totalSaved = savedBusinesses.size,
                                    onOpenNotifications = { showNotificationSheet = true }
                                )
                            }
                        }
                    }
                    UserRole.BUSINESS_OWNER -> {
                        BusinessDashboardScreen(
                            viewModel = viewModel,
                            business = activeOwnerBusiness,
                            appointments = ownerAppointments,
                            services = ownerServices,
                            team = ownerTeam,
                            onOpenOnboarding = { viewModel.openOnboarding() }
                        )
                    }
                    UserRole.ADMIN -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            allBusinesses = allBusinessesAdmin,
                            allUsers = allUsers,
                            allAppointments = allAppointmentsAdmin,
                            allReviews = allReviewsAdmin
                        )
                    }
                }
            }
        }
    }

    // Modal Booking Bottom Sheet
    if (bookingDialogOpen && selectedBusiness != null) {
        BookingBottomSheet(
            business = selectedBusiness!!,
            services = selectedBusinessServices,
            teamMembers = selectedBusinessTeam,
            selectedService = bookingService,
            selectedTeamMember = bookingTeamMember,
            selectedDate = bookingDate,
            selectedTime = bookingTime,
            notes = bookingNotes,
            onServiceSelected = { srv -> viewModel.setBookingService(srv) },
            onTeamMemberSelected = { tm -> viewModel.setBookingTeamMember(tm) },
            onDateSelected = { dt -> viewModel.setBookingDate(dt) },
            onTimeSelected = { tm -> viewModel.setBookingTime(tm) },
            onNotesChanged = { n -> viewModel.setBookingNotes(n) },
            onConfirmBooking = { viewModel.submitBooking() },
            onDismiss = { viewModel.closeBooking() }
        )
    }

    // Review Dialog
    if (reviewAppointment != null) {
        ReviewDialog(
            appointment = reviewAppointment!!,
            rating = reviewRating,
            comment = reviewComment,
            onRatingChange = { r -> viewModel.setReviewRating(r) },
            onCommentChange = { c -> viewModel.setReviewComment(c) },
            onSubmit = { viewModel.submitReview() },
            onDismiss = { viewModel.closeReviewDialog() }
        )
    }

    // Notifications Bottom Sheet
    if (showNotificationSheet) {
        NotificationSheet(
            notifications = notifications,
            onDismiss = { showNotificationSheet = false },
            onMarkRead = { }
        )
    }
}
