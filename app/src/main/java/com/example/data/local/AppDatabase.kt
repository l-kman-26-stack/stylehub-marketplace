package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.StyleHubDao
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BusinessEntity::class,
        ServiceEntity::class,
        TeamMemberEntity::class,
        OpeningHourEntity::class,
        AppointmentEntity::class,
        ReviewEntity::class,
        SavedBusinessEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun styleHubDao(): StyleHubDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stylehub_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)): AppDatabase =
            getInstance(context, scope)

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.styleHubDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: StyleHubDao) {
            // 1. Pre-populate Demo Users for All 3 Roles
            val customerId = dao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Leletu Kamana",
                    email = "leletu@stylehub.co.za",
                    phone = "+27 82 555 1234",
                    role = UserRole.CUSTOMER.name,
                    city = "Johannesburg",
                    province = "Gauteng"
                )
            )

            val businessOwnerId = dao.insertUser(
                UserEntity(
                    id = 2,
                    name = "Sipho Dlamini",
                    email = "sipho@legendarybarbers.co.za",
                    phone = "+27 71 888 9900",
                    role = UserRole.BUSINESS_OWNER.name,
                    city = "Sandton",
                    province = "Gauteng"
                )
            )

            val adminId = dao.insertUser(
                UserEntity(
                    id = 3,
                    name = "Admin Thabo",
                    email = "admin@stylehub.co.za",
                    phone = "+27 11 400 0000",
                    role = UserRole.ADMIN.name,
                    city = "Johannesburg",
                    province = "Gauteng"
                )
            )

            // 2. Pre-populate Businesses across South Africa (Barbers, Salons, Braiders, Grooming)
            val biz1 = dao.insertBusiness(
                BusinessEntity(
                    id = 1,
                    ownerId = 2,
                    name = "Legendary Grooming Sandton",
                    category = BusinessCategory.BARBER.displayName,
                    description = "Johannesburg's premier grooming studio specializing in precision skin fades, beard sculpts, hot towel treatments, and executive styling.",
                    phone = "+27 11 883 4567",
                    email = "sandton@legendarygrooming.co.za",
                    whatsapp = "+27718889900",
                    website = "https://legendarysandton.co.za",
                    instagram = "@legendary_sandton",
                    streetAddress = "83 Rivonia Road, Sandton City Mall",
                    suburb = "Sandton",
                    city = "Johannesburg",
                    province = "Gauteng",
                    postalCode = "2196",
                    coverDrawable = "hero_barber",
                    logoDrawable = "ic_app_logo",
                    rating = 4.9f,
                    reviewCount = 128,
                    isVerified = true,
                    isFeatured = true,
                    status = BusinessStatus.APPROVED.name,
                    profileViews = 1420,
                    minPriceZar = 180.0
                )
            )

            val biz2 = dao.insertBusiness(
                BusinessEntity(
                    id = 2,
                    ownerId = 2,
                    name = "The Braiding Bar Camps Bay",
                    category = BusinessCategory.BRAIDER.displayName,
                    description = "Cape Town's elite afro-hair and braiding sanctuary. Master knotless braids, goddess locs, cornrows, and organic hair restoration.",
                    phone = "+27 21 438 9012",
                    email = "bookings@thebraidingbar.co.za",
                    whatsapp = "+27834567890",
                    website = "https://thebraidingbar.co.za",
                    instagram = "@thebraidingbar_ct",
                    streetAddress = "42 Victoria Road, Promenade",
                    suburb = "Camps Bay",
                    city = "Cape Town",
                    province = "Western Cape",
                    postalCode = "8005",
                    coverDrawable = "hero_salon",
                    logoDrawable = "ic_app_logo",
                    rating = 4.8f,
                    reviewCount = 94,
                    isVerified = true,
                    isFeatured = true,
                    status = BusinessStatus.APPROVED.name,
                    profileViews = 980,
                    minPriceZar = 250.0
                )
            )

            val biz3 = dao.insertBusiness(
                BusinessEntity(
                    id = 3,
                    ownerId = 2,
                    name = "Crown & Fade Lounge Rosebank",
                    category = BusinessCategory.GROOMING.displayName,
                    description = "A refined modern aesthetic lounge offering full VIP cut packages, scalp treatments, black mask facials, and complimentary barista coffee.",
                    phone = "+27 11 447 3321",
                    email = "rosebank@crownandfade.co.za",
                    whatsapp = "+27723334455",
                    website = "https://crownandfade.co.za",
                    instagram = "@crownandfade_za",
                    streetAddress = "50 Bath Avenue, Rosebank Link",
                    suburb = "Rosebank",
                    city = "Johannesburg",
                    province = "Gauteng",
                    postalCode = "2196",
                    coverDrawable = "hero_barber",
                    logoDrawable = "ic_app_logo",
                    rating = 4.7f,
                    reviewCount = 76,
                    isVerified = true,
                    isFeatured = false,
                    status = BusinessStatus.APPROVED.name,
                    profileViews = 620,
                    minPriceZar = 200.0
                )
            )

            val biz4 = dao.insertBusiness(
                BusinessEntity(
                    id = 4,
                    ownerId = 2,
                    name = "AfroChic Hair Studio Durban",
                    category = BusinessCategory.SALON.displayName,
                    description = "Award-winning coastal hair salon offering luxury silk presses, custom coloring, balayage, wig installs, and scalp therapies.",
                    phone = "+27 31 312 8844",
                    email = "durban@afrochicstudio.co.za",
                    whatsapp = "+27845556677",
                    website = "https://afrochicdurban.co.za",
                    instagram = "@afrochic_dbn",
                    streetAddress = "198 Florida Road",
                    suburb = "Morningside",
                    city = "Durban",
                    province = "KwaZulu-Natal",
                    postalCode = "4001",
                    coverDrawable = "hero_salon",
                    logoDrawable = "ic_app_logo",
                    rating = 4.9f,
                    reviewCount = 112,
                    isVerified = true,
                    isFeatured = true,
                    status = BusinessStatus.APPROVED.name,
                    profileViews = 890,
                    minPriceZar = 220.0
                )
            )

            val biz5 = dao.insertBusiness(
                BusinessEntity(
                    id = 5,
                    ownerId = 2,
                    name = "Pretoria Executive Barbers Menlyn",
                    category = BusinessCategory.BARBER.displayName,
                    description = "Top-tier precision barbering for the modern professional. Taper fades, beard grooming, shear work, and grooming packages.",
                    phone = "+27 12 368 1122",
                    email = "menlyn@ptagroomers.co.za",
                    whatsapp = "+27761112233",
                    streetAddress = "Menlyn Maine Central Square, Amarand Ave",
                    suburb = "Menlyn",
                    city = "Pretoria",
                    province = "Gauteng",
                    postalCode = "0181",
                    coverDrawable = "hero_barber",
                    logoDrawable = "ic_app_logo",
                    rating = 4.6f,
                    reviewCount = 53,
                    isVerified = false,
                    isFeatured = false,
                    status = BusinessStatus.PENDING_APPROVAL.name,
                    profileViews = 240,
                    minPriceZar = 160.0
                )
            )

            val biz6 = dao.insertBusiness(
                BusinessEntity(
                    id = 6,
                    ownerId = 2,
                    name = "Urban Cuts Mobile Barber JHB",
                    category = BusinessCategory.MOBILE_BARBER.displayName,
                    description = "We bring the luxury barbershop directly to your home or office in Fourways, Bryanston, and Sandton. Premium tools and hygiene.",
                    phone = "+27 82 999 4433",
                    email = "booking@urbancutsmobile.co.za",
                    whatsapp = "+27829994433",
                    streetAddress = "Mobile Service (Northern Suburbs)",
                    suburb = "Fourways",
                    city = "Johannesburg",
                    province = "Gauteng",
                    postalCode = "2055",
                    coverDrawable = "hero_barber",
                    logoDrawable = "ic_app_logo",
                    rating = 4.9f,
                    reviewCount = 38,
                    isVerified = true,
                    isFeatured = false,
                    status = BusinessStatus.APPROVED.name,
                    profileViews = 410,
                    minPriceZar = 280.0
                )
            )

            // 3. Services for Businesses
            dao.insertServices(
                listOf(
                    // Sandton Barbershop
                    ServiceEntity(businessId = 1, name = "Executive Skin Fade & Styling", category = "Haircut", description = "Precision fade, scissor work on top, and styling with premium matte paste.", priceZar = 220.0, durationMinutes = 40),
                    ServiceEntity(businessId = 1, name = "Beard Sculpt & Razor Line", category = "Beard", description = "Beard trimming, hot towel therapy, essential oils, and razor crisp line.", priceZar = 160.0, durationMinutes = 25),
                    ServiceEntity(businessId = 1, name = "The Signature Package", category = "Combo", description = "Skin fade, beard sculpt, hot towel, facial scrub, and energizing scalp massage.", priceZar = 380.0, durationMinutes = 60),
                    ServiceEntity(businessId = 1, name = "Kids & Students Classic Cut", category = "Haircut", description = "Clean tailored cut for boys and students (Valid student card).", priceZar = 180.0, durationMinutes = 30),
                    ServiceEntity(businessId = 1, name = "Black Mask & Detox Facial", category = "Facial", description = "Deep pore purifying peel-off black mask treatment.", priceZar = 150.0, durationMinutes = 20),

                    // Camps Bay Braiding Bar
                    ServiceEntity(businessId = 2, name = "Knotless Box Braids (Medium - Mid-Back)", category = "Braids", description = "Tension-free lightweight knotless box braids with extensions included.", priceZar = 550.0, durationMinutes = 180),
                    ServiceEntity(businessId = 2, name = "Goddess Locs (Waist Length)", category = "Locs", description = "Boho curly-end soft locs with synthetic blend or human hair curls.", priceZar = 750.0, durationMinutes = 210),
                    ServiceEntity(businessId = 2, name = "Stitch Cornrows (6 Lines)", category = "Cornrows", description = "Crisp neat stitch braid cornrows with feed-in hair.", priceZar = 320.0, durationMinutes = 90),
                    ServiceEntity(businessId = 2, name = "Deep Scalp Steam & Hair Treatment", category = "Care", description = "Hydrating shea and tea-tree steam therapy for natural hair.", priceZar = 250.0, durationMinutes = 45),

                    // Rosebank Crown & Fade
                    ServiceEntity(businessId = 3, name = "Gentleman's Taper & Scissor Cut", category = "Haircut", description = "Customized scissor cut and subtle taper.", priceZar = 200.0, durationMinutes = 35),
                    ServiceEntity(businessId = 3, name = "Royal Hot Towel Shave", category = "Shave", description = "Traditional straight razor wet shave with pre-shave oil.", priceZar = 180.0, durationMinutes = 30),
                    ServiceEntity(businessId = 3, name = "Charcoal Beard Tint & Line", category = "Beard", description = "Grey-blending color restoration and sharp outline.", priceZar = 190.0, durationMinutes = 25),

                    // Durban AfroChic Salon
                    ServiceEntity(businessId = 4, name = "Luxury Silk Press & Trim", category = "Styling", description = "Sulfate-free wash, moisture infusion, silk press, and split-end dusting.", priceZar = 420.0, durationMinutes = 90),
                    ServiceEntity(businessId = 4, name = "Wig Customization & Glue-less Install", category = "Wigs", description = "Bleaching knots, plucking hairline, and styling install.", priceZar = 600.0, durationMinutes = 120),
                    ServiceEntity(businessId = 4, name = "Balayage & Color Gloss", category = "Color", description = "Sun-kissed hand-painted highlights with nourishing toner.", priceZar = 650.0, durationMinutes = 150)
                )
            )

            // 4. Team Members
            dao.insertTeamMembers(
                listOf(
                    TeamMemberEntity(businessId = 1, name = "Sipho Dlamini", role = "Master Barber & Founder", bio = "12+ years craft barbering across Sandton and London.", specialties = "Fades, Beard Architecture, Hot Towel", avatarInitials = "SD"),
                    TeamMemberEntity(businessId = 1, name = "Tariro Moyo", role = "Senior Barber", bio = "Fade specialist and shear cut artist.", specialties = "Skin Fades, Scissor Work, Kids", avatarInitials = "TM"),
                    TeamMemberEntity(businessId = 1, name = "Kagiso Ndlovu", role = "Grooming Specialist", bio = "Facial therapies, line perfection, and beard tints.", specialties = "Facials, Beard Sculpt, Shaves", avatarInitials = "KN"),

                    TeamMemberEntity(businessId = 2, name = "Nomsa Khumalo", role = "Lead Braider & Stylist", bio = "Known for fast, painless knotless braids and creative stitch styles.", specialties = "Knotless Braids, Goddess Locs", avatarInitials = "NK"),
                    TeamMemberEntity(businessId = 2, name = "Ayanda Cele", role = "Natural Hair Specialist", bio = "Specialist in loc maintenance, scalp treatments, and cornrows.", specialties = "Cornrows, Scalp Steam, Locs", avatarInitials = "AC"),

                    TeamMemberEntity(businessId = 4, name = "Zanele Mthembu", role = "Senior Creative Stylist", bio = "Silk press connoisseur and precision hair colorist.", specialties = "Silk Press, Balayage, Lace Installs", avatarInitials = "ZM")
                )
            )

            // 5. Opening Hours (Monday - Sunday) for Sandton Barbershop
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            val hours = days.mapIndexed { index, day ->
                OpeningHourEntity(
                    businessId = 1,
                    dayOfWeek = index + 1,
                    dayName = day,
                    openTime = if (index < 5) "08:00" else if (index == 5) "08:30" else "09:00",
                    closeTime = if (index < 5) "18:30" else if (index == 5) "17:00" else "15:00",
                    isClosed = index == 6 // Sunday closed or short hours
                )
            }
            dao.insertOpeningHours(hours)

            // 6. Sample Initial Reviews
            dao.insertReview(
                ReviewEntity(
                    businessId = 1,
                    customerId = 1,
                    customerName = "Leletu Kamana",
                    rating = 5,
                    comment = "Best fade in Sandton without question! Sipho is a master craftsman. Atmosphere is clean, welcoming, and the hot towel finish is exceptional.",
                    serviceName = "The Signature Package"
                )
            )
            dao.insertReview(
                ReviewEntity(
                    businessId = 1,
                    customerId = 4,
                    customerName = "Kgosi Molefe",
                    rating = 5,
                    comment = "Crisp lines and punctual appointments. Booked through StyleHub and walked right into the chair with zero wait time.",
                    serviceName = "Executive Skin Fade & Styling"
                )
            )
            dao.insertReview(
                ReviewEntity(
                    businessId = 2,
                    customerId = 1,
                    customerName = "Naledi Sithole",
                    rating = 5,
                    comment = "Nomsa is incredible! My knotless braids are super neat, light, and pain-free. Will definitely be returning.",
                    serviceName = "Knotless Box Braids"
                )
            )

            // 7. Initial Sample Appointment
            dao.insertAppointment(
                AppointmentEntity(
                    id = 1,
                    customerId = 1,
                    customerName = "Leletu Kamana",
                    customerPhone = "+27 82 555 1234",
                    businessId = 1,
                    businessName = "Legendary Grooming Sandton",
                    serviceId = 1,
                    serviceName = "Executive Skin Fade & Styling",
                    teamMemberId = 1,
                    teamMemberName = "Sipho Dlamini",
                    appointmentDate = "Tomorrow, 2:00 PM",
                    appointmentTime = "14:00",
                    notes = "Please keep the top textured and sharp skin taper on the sides.",
                    priceZar = 220.0,
                    status = AppointmentStatus.CONFIRMED.name,
                    hasReviewed = false
                )
            )

            // 8. Saved Business
            dao.insertSavedBusiness(SavedBusinessEntity(userId = 1, businessId = 1))
            dao.insertSavedBusiness(SavedBusinessEntity(userId = 1, businessId = 2))

            // 9. Initial Notifications
            dao.insertNotification(
                NotificationEntity(
                    userId = 1,
                    title = "Booking Confirmed! 💈",
                    message = "Your appointment at Legendary Grooming Sandton for tomorrow at 14:00 is confirmed.",
                    type = "APPOINTMENT"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = 2,
                    title = "New Booking Request",
                    message = "Leletu Kamana booked 'Executive Skin Fade' for tomorrow at 14:00.",
                    type = "APPOINTMENT"
                )
            )
        }
    }
}
