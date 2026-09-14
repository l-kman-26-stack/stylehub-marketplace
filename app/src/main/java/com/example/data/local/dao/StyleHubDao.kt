package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StyleHubDao {

    // --- Users ---
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE authProvider = :provider AND providerUid = :uid LIMIT 1")
    suspend fun getUserByProviderUid(provider: String, uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Businesses ---
    @Query("SELECT * FROM businesses WHERE status = 'APPROVED' ORDER BY isFeatured DESC, rating DESC")
    fun getAllApprovedBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses ORDER BY createdAt DESC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE id = :businessId LIMIT 1")
    fun getBusinessFlowById(businessId: Long): Flow<BusinessEntity?>

    @Query("SELECT * FROM businesses WHERE id = :businessId LIMIT 1")
    suspend fun getBusinessById(businessId: Long): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE ownerId = :ownerId")
    fun getBusinessesByOwner(ownerId: Long): Flow<List<BusinessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    @Query("UPDATE businesses SET profileViews = profileViews + 1 WHERE id = :businessId")
    suspend fun incrementProfileViews(businessId: Long)

    @Delete
    suspend fun deleteBusiness(business: BusinessEntity)

    // --- Services ---
    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE businessId = :businessId ORDER BY priceZar ASC")
    fun getServicesByBusiness(businessId: Long): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    // --- Team Members ---
    @Query("SELECT * FROM team_members WHERE businessId = :businessId ORDER BY id ASC")
    fun getTeamByBusiness(businessId: Long): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TeamMemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMembers(members: List<TeamMemberEntity>)

    @Update
    suspend fun updateTeamMember(member: TeamMemberEntity)

    @Delete
    suspend fun deleteTeamMember(member: TeamMemberEntity)

    // --- Opening Hours ---
    @Query("SELECT * FROM opening_hours WHERE businessId = :businessId ORDER BY dayOfWeek ASC")
    fun getOpeningHoursByBusiness(businessId: Long): Flow<List<OpeningHourEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpeningHours(hours: List<OpeningHourEntity>)

    @Update
    suspend fun updateOpeningHour(hour: OpeningHourEntity)

    // --- Appointments ---
    @Query("SELECT * FROM appointments WHERE customerId = :customerId ORDER BY id DESC")
    fun getAppointmentsForCustomer(customerId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE businessId = :businessId ORDER BY id DESC")
    fun getAppointmentsForBusiness(businessId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE businessId = :businessId AND appointmentDate = :date AND status != 'CANCELLED'")
    suspend fun getBookedSlotsForDate(businessId: Long, date: String): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    suspend fun updateAppointmentStatus(appointmentId: Long, status: String)

    @Query("UPDATE appointments SET hasReviewed = 1 WHERE id = :appointmentId")
    suspend fun markAppointmentReviewed(appointmentId: Long)

    // --- Reviews ---
    @Query("SELECT * FROM reviews WHERE businessId = :businessId AND isFlagged = 0 ORDER BY createdAt DESC")
    fun getReviewsForBusiness(businessId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Query("UPDATE reviews SET isFlagged = :isFlagged WHERE id = :reviewId")
    suspend fun updateReviewFlag(reviewId: Long, isFlagged: Boolean)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    // --- Saved Businesses ---
    @Query("SELECT * FROM saved_businesses WHERE userId = :userId")
    fun getSavedBusinessesForUser(userId: Long): Flow<List<SavedBusinessEntity>>

    @Query("SELECT COUNT(*) > 0 FROM saved_businesses WHERE userId = :userId AND businessId = :businessId")
    fun isBusinessSaved(userId: Long, businessId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedBusiness(saved: SavedBusinessEntity)

    @Query("DELETE FROM saved_businesses WHERE userId = :userId AND businessId = :businessId")
    suspend fun deleteSavedBusiness(userId: Long, businessId: Long)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Long)

    // --- Sessions & Device Management ---
    @Query("SELECT * FROM sessions WHERE userId = :userId AND isRevoked = 0 ORDER BY lastActiveAt DESC")
    fun getActiveSessionsForUser(userId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("UPDATE sessions SET isRevoked = 1 WHERE id = :sessionId")
    suspend fun revokeSession(sessionId: String)

    @Query("UPDATE sessions SET isRevoked = 1 WHERE userId = :userId AND id != :currentSessionId")
    suspend fun revokeAllSessionsExcept(userId: Long, currentSessionId: String)

    @Query("UPDATE sessions SET isRevoked = 1 WHERE userId = :userId")
    suspend fun revokeAllSessions(userId: Long)

    // --- Security Audit Logs ---
    @Query("SELECT * FROM security_audit_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getAuditLogsForUser(userId: Long): Flow<List<SecurityAuditLogEntity>>

    @Query("SELECT * FROM security_audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAuditLogs(): Flow<List<SecurityAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: SecurityAuditLogEntity): Long
}
