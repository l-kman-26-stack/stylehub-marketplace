package com.example.data.repository

import com.example.data.local.dao.StyleHubDao
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class StyleHubRepository(private val dao: StyleHubDao) {

    val authManager = com.example.security.AuthManager(dao)

    // --- Users & Roles ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun getUser(id: Long): UserEntity? = dao.getUserById(id)

    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    suspend fun toggleUserSuspension(userId: Long) {
        val user = dao.getUserById(userId) ?: return
        dao.updateUser(user.copy(isSuspended = !user.isSuspended))
    }

    // --- Businesses ---
    val approvedBusinesses: Flow<List<BusinessEntity>> = dao.getAllApprovedBusinesses()
    val allBusinessesAdmin: Flow<List<BusinessEntity>> = dao.getAllBusinesses()

    fun getBusinessFlow(businessId: Long): Flow<BusinessEntity?> = dao.getBusinessFlowById(businessId)

    suspend fun getBusiness(businessId: Long): BusinessEntity? = dao.getBusinessById(businessId)

    fun getBusinessesByOwner(ownerId: Long): Flow<List<BusinessEntity>> = dao.getBusinessesByOwner(ownerId)

    suspend fun incrementViews(businessId: Long) = dao.incrementProfileViews(businessId)

    suspend fun createBusinessListing(
        business: BusinessEntity,
        services: List<ServiceEntity>,
        teamMembers: List<TeamMemberEntity>,
        hours: List<OpeningHourEntity>
    ): Long {
        val bizId = dao.insertBusiness(business)
        if (services.isNotEmpty()) {
            dao.insertServices(services.map { it.copy(businessId = bizId) })
        }
        if (teamMembers.isNotEmpty()) {
            dao.insertTeamMembers(teamMembers.map { it.copy(businessId = bizId) })
        }
        if (hours.isNotEmpty()) {
            dao.insertOpeningHours(hours.map { it.copy(businessId = bizId) })
        }
        // Notify admin
        dao.insertNotification(
            NotificationEntity(
                userId = 3, // Admin
                title = "New Business Listing Submitted",
                message = "${business.name} has been submitted for verification and approval.",
                type = "APPROVAL"
            )
        )
        return bizId
    }

    suspend fun updateBusiness(business: BusinessEntity) = dao.updateBusiness(business)

    suspend fun updateBusinessStatus(businessId: Long, status: BusinessStatus) {
        val biz = dao.getBusinessById(businessId) ?: return
        dao.updateBusiness(biz.copy(status = status.name))
        // Notify owner
        dao.insertNotification(
            NotificationEntity(
                userId = biz.ownerId,
                title = "Listing Status Update",
                message = "Your listing '${biz.name}' status is now: ${status.name.replace("_", " ")}",
                type = "APPROVAL"
            )
        )
    }

    suspend fun toggleBusinessVerification(businessId: Long) {
        val biz = dao.getBusinessById(businessId) ?: return
        val newStatus = !biz.isVerified
        dao.updateBusiness(biz.copy(isVerified = newStatus))
        dao.insertNotification(
            NotificationEntity(
                userId = biz.ownerId,
                title = if (newStatus) "Business Verified! 🛡️" else "Verification Removed",
                message = if (newStatus)
                    "Congratulations! '${biz.name}' has been officially verified by StyleHub Admins."
                else
                    "Verification badge removed for '${biz.name}'.",
                type = "APPROVAL"
            )
        )
    }

    suspend fun toggleBusinessFeatured(businessId: Long) {
        val biz = dao.getBusinessById(businessId) ?: return
        dao.updateBusiness(biz.copy(isFeatured = !biz.isFeatured))
    }

    suspend fun deleteBusiness(business: BusinessEntity) = dao.deleteBusiness(business)

    // --- Services ---
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    fun getServices(businessId: Long): Flow<List<ServiceEntity>> = dao.getServicesByBusiness(businessId)

    suspend fun addService(service: ServiceEntity): Long = dao.insertService(service)

    suspend fun updateService(service: ServiceEntity) = dao.updateService(service)

    suspend fun deleteService(service: ServiceEntity) = dao.deleteService(service)

    // --- Team Members ---
    fun getTeamMembers(businessId: Long): Flow<List<TeamMemberEntity>> = dao.getTeamByBusiness(businessId)

    suspend fun addTeamMember(member: TeamMemberEntity): Long = dao.insertTeamMember(member)

    suspend fun updateTeamMember(member: TeamMemberEntity) = dao.updateTeamMember(member)

    suspend fun deleteTeamMember(member: TeamMemberEntity) = dao.deleteTeamMember(member)

    // --- Opening Hours ---
    fun getOpeningHours(businessId: Long): Flow<List<OpeningHourEntity>> = dao.getOpeningHoursByBusiness(businessId)

    suspend fun updateOpeningHour(hour: OpeningHourEntity) = dao.updateOpeningHour(hour)

    // --- Appointments ---
    fun getCustomerAppointments(customerId: Long): Flow<List<AppointmentEntity>> = dao.getAppointmentsForCustomer(customerId)

    fun getBusinessAppointments(businessId: Long): Flow<List<AppointmentEntity>> = dao.getAppointmentsForBusiness(businessId)

    val allAppointmentsAdmin: Flow<List<AppointmentEntity>> = dao.getAllAppointments()

    suspend fun getBookedSlotsForDate(businessId: Long, date: String): List<AppointmentEntity> =
        dao.getBookedSlotsForDate(businessId, date)

    suspend fun requestAppointment(appointment: AppointmentEntity): Long {
        val id = dao.insertAppointment(appointment)
        val biz = dao.getBusinessById(appointment.businessId)
        // Notify business owner
        if (biz != null) {
            dao.insertNotification(
                NotificationEntity(
                    userId = biz.ownerId,
                    title = "New Appointment Request",
                    message = "${appointment.customerName} requested ${appointment.serviceName} on ${appointment.appointmentDate} at ${appointment.appointmentTime}.",
                    type = "APPOINTMENT"
                )
            )
        }
        // Notify customer
        dao.insertNotification(
            NotificationEntity(
                userId = appointment.customerId,
                title = "Appointment Booked! 📅",
                message = "Your booking for ${appointment.serviceName} at ${appointment.businessName} is set for ${appointment.appointmentDate} at ${appointment.appointmentTime}.",
                type = "APPOINTMENT"
            )
        )
        return id
    }

    suspend fun updateAppointmentStatus(appointmentId: Long, status: AppointmentStatus, customerId: Long, businessName: String) {
        dao.updateAppointmentStatus(appointmentId, status.name)
        dao.insertNotification(
            NotificationEntity(
                userId = customerId,
                title = "Booking ${status.name.lowercase().replaceFirstChar { it.uppercase() }}",
                message = "Your appointment at $businessName is now ${status.name}.",
                type = "APPOINTMENT"
            )
        )
    }

    // --- Reviews ---
    fun getReviews(businessId: Long): Flow<List<ReviewEntity>> = dao.getReviewsForBusiness(businessId)

    val allReviewsAdmin: Flow<List<ReviewEntity>> = dao.getAllReviews()

    suspend fun submitReview(review: ReviewEntity) {
        dao.insertReview(review)
        if (review.appointmentId > 0) {
            dao.markAppointmentReviewed(review.appointmentId)
        }
        // Recalculate business rating
        val biz = dao.getBusinessById(review.businessId)
        if (biz != null) {
            val allRev = dao.getReviewsForBusiness(review.businessId).firstOrNull() ?: listOf(review)
            val avg = (allRev.sumOf { it.rating }.toFloat() + review.rating) / (allRev.size + 1).coerceAtLeast(1)
            dao.updateBusiness(biz.copy(rating = avg, reviewCount = biz.reviewCount + 1))
            // Notify owner
            dao.insertNotification(
                NotificationEntity(
                    userId = biz.ownerId,
                    title = "New Customer Review ⭐",
                    message = "${review.customerName} left a ${review.rating}-star review: \"${review.comment.take(60)}...\"",
                    type = "REVIEW"
                )
            )
        }
    }

    suspend fun deleteReview(review: ReviewEntity) = dao.deleteReview(review)

    // --- Saved Businesses ---
    fun getSavedBusinesses(userId: Long): Flow<List<SavedBusinessEntity>> = dao.getSavedBusinessesForUser(userId)

    fun isBusinessSaved(userId: Long, businessId: Long): Flow<Boolean> = dao.isBusinessSaved(userId, businessId)

    suspend fun toggleSaveBusiness(userId: Long, businessId: Long, isSaved: Boolean) {
        if (isSaved) {
            dao.deleteSavedBusiness(userId, businessId)
        } else {
            dao.insertSavedBusiness(SavedBusinessEntity(userId = userId, businessId = businessId))
        }
    }

    // --- Notifications ---
    fun getNotifications(userId: Long): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId)

    suspend fun markNotificationRead(id: Long) = dao.markNotificationAsRead(id)
 
     // --- Enterprise Security & Session Management ---
     fun getActiveSessions(userId: Long): Flow<List<SessionEntity>> = authManager.getActiveSessions(userId)

     suspend fun revokeSession(sessionId: String, userId: Long) = authManager.revokeSession(sessionId, userId)

     suspend fun revokeAllOtherSessions(userId: Long, currentSessionId: String) = authManager.revokeAllOtherSessions(userId, currentSessionId)

     fun getAuditLogs(userId: Long): Flow<List<SecurityAuditLogEntity>> = authManager.getAuditLogs(userId)

     fun getAllAuditLogs(): Flow<List<SecurityAuditLogEntity>> = authManager.getAllAuditLogs()
}
