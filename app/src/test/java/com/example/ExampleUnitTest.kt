package com.example

import com.example.data.local.entities.BusinessCategory
import com.example.data.local.entities.BusinessEntity
import com.example.data.local.entities.ServiceEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun filter_by_service_name_matches_correct_business() {
        val biz1 = BusinessEntity(
            id = 1,
            ownerId = 2,
            name = "Legendary Grooming Sandton",
            category = BusinessCategory.BARBER.displayName,
            description = "Barbershop",
            phone = "0118834567",
            email = "sandton@groom.co.za",
            whatsapp = "0118834567",
            streetAddress = "5th Street, Sandton City",
            postalCode = "2196",
            suburb = "Sandton",
            city = "Johannesburg",
            province = "Gauteng",
            minPriceZar = 180.0
        )
        val biz2 = BusinessEntity(
            id = 2,
            ownerId = 2,
            name = "The Braiding Bar Camps Bay",
            category = BusinessCategory.BRAIDER.displayName,
            description = "Braiding salon",
            phone = "0214389012",
            email = "campsbay@braid.co.za",
            whatsapp = "0214389012",
            streetAddress = "Victoria Road",
            postalCode = "8005",
            suburb = "Camps Bay",
            city = "Cape Town",
            province = "Western Cape",
            minPriceZar = 550.0
        )

        val services = listOf(
            ServiceEntity(id = 1, businessId = 1, name = "Executive Skin Fade & Styling", category = "Haircut", description = "Skin fade", priceZar = 220.0, durationMinutes = 40),
            ServiceEntity(id = 2, businessId = 2, name = "Knotless Box Braids", category = "Braids", description = "Braids with extensions", priceZar = 550.0, durationMinutes = 180)
        )

        val servicesByBiz = services.groupBy { it.businessId }

        // Test filtering by service name "Fade"
        val queryFade = "Fade"
        val fadeMatches = listOf(biz1, biz2).filter { biz ->
            val bizServices = servicesByBiz[biz.id].orEmpty()
            bizServices.any { it.name.contains(queryFade, ignoreCase = true) } ||
                    biz.city.contains(queryFade, ignoreCase = true) ||
                    biz.suburb.contains(queryFade, ignoreCase = true)
        }
        assertEquals(1, fadeMatches.size)
        assertEquals("Legendary Grooming Sandton", fadeMatches.first().name)

        // Test filtering by service name "Braids"
        val queryBraids = "Braids"
        val braidMatches = listOf(biz1, biz2).filter { biz ->
            val bizServices = servicesByBiz[biz.id].orEmpty()
            bizServices.any { it.name.contains(queryBraids, ignoreCase = true) } ||
                    biz.city.contains(queryBraids, ignoreCase = true) ||
                    biz.suburb.contains(queryBraids, ignoreCase = true)
        }
        assertEquals(1, braidMatches.size)
        assertEquals("The Braiding Bar Camps Bay", braidMatches.first().name)
    }

    @Test
    fun filter_by_location_matches_correct_business() {
        val biz1 = BusinessEntity(
            id = 1,
            ownerId = 2,
            name = "Legendary Grooming Sandton",
            category = BusinessCategory.BARBER.displayName,
            description = "Barbershop",
            phone = "0118834567",
            email = "sandton@groom.co.za",
            whatsapp = "0118834567",
            streetAddress = "5th Street, Sandton City",
            postalCode = "2196",
            suburb = "Sandton",
            city = "Johannesburg",
            province = "Gauteng",
            minPriceZar = 180.0
        )
        val biz2 = BusinessEntity(
            id = 2,
            ownerId = 2,
            name = "The Braiding Bar Camps Bay",
            category = BusinessCategory.BRAIDER.displayName,
            description = "Braiding salon",
            phone = "0214389012",
            email = "campsbay@braid.co.za",
            whatsapp = "0214389012",
            streetAddress = "Victoria Road",
            postalCode = "8005",
            suburb = "Camps Bay",
            city = "Cape Town",
            province = "Western Cape",
            minPriceZar = 550.0
        )

        // Test location "Cape Town"
        val queryLocation = "Cape Town"
        val ctMatches = listOf(biz1, biz2).filter { biz ->
            biz.city.contains(queryLocation, ignoreCase = true) ||
                    biz.suburb.contains(queryLocation, ignoreCase = true) ||
                    biz.province.contains(queryLocation, ignoreCase = true)
        }
        assertEquals(1, ctMatches.size)
        assertEquals("The Braiding Bar Camps Bay", ctMatches.first().name)

        // Test location "Sandton"
        val querySandton = "Sandton"
        val sandtonMatches = listOf(biz1, biz2).filter { biz ->
            biz.city.contains(querySandton, ignoreCase = true) ||
                    biz.suburb.contains(querySandton, ignoreCase = true) ||
                    biz.province.contains(querySandton, ignoreCase = true)
        }
        assertEquals(1, sandtonMatches.size)
        assertEquals("Legendary Grooming Sandton", sandtonMatches.first().name)
    }

    @Test
    fun accountLifecycle_statusTransitionsAndGracePeriod() {
        val now = System.currentTimeMillis()
        val gracePeriodMs = 30L * 24 * 60 * 60 * 1000L
        val scheduledDeletion = now + gracePeriodMs

        val activeUser = com.example.data.local.entities.UserEntity(
            id = 10,
            name = "Test User",
            email = "user@test.co.za",
            phone = "0821234567",
            passwordHash = "hash123",
            salt = "salt123",
            role = "CUSTOMER",
            accountStatus = com.example.data.local.entities.AccountStatus.ACTIVE.name
        )
        assertEquals("ACTIVE", activeUser.accountStatus)

        // Deactivate
        val deactivatedUser = activeUser.copy(
            accountStatus = com.example.data.local.entities.AccountStatus.DEACTIVATED.name,
            deactivatedAt = now
        )
        assertEquals("DEACTIVATED", deactivatedUser.accountStatus)
        assertEquals(now, deactivatedUser.deactivatedAt)

        // Reactivate
        val reactivatedUser = deactivatedUser.copy(
            accountStatus = com.example.data.local.entities.AccountStatus.ACTIVE.name,
            deactivatedAt = null
        )
        assertEquals("ACTIVE", reactivatedUser.accountStatus)
        assertNull(reactivatedUser.deactivatedAt)

        // Request Deletion (30-day grace period)
        val pendingUser = reactivatedUser.copy(
            accountStatus = com.example.data.local.entities.AccountStatus.PENDING_DELETION.name,
            deletionRequestedAt = now,
            scheduledDeletionAt = scheduledDeletion
        )
        assertEquals("PENDING_DELETION", pendingUser.accountStatus)
        val daysRemaining = ((pendingUser.scheduledDeletionAt!! - now) / (1000L * 60 * 60 * 24)).toInt()
        assertEquals(30, daysRemaining)

        // Cancel Deletion
        val restoredUser = pendingUser.copy(
            accountStatus = com.example.data.local.entities.AccountStatus.ACTIVE.name,
            deletionRequestedAt = null,
            scheduledDeletionAt = null
        )
        assertEquals("ACTIVE", restoredUser.accountStatus)
        assertNull(restoredUser.deletionRequestedAt)
        assertNull(restoredUser.scheduledDeletionAt)
    }

    @Test
    fun passwordSecurity_hashingAndVerification() {
        val salt = com.example.security.SecurityValidator.generateSalt()
        assertTrue(salt.isNotEmpty())
        val password = "SecurePassword@2026"
        val hash = com.example.security.SecurityValidator.hashPassword(password, salt)
        val testHash = com.example.security.SecurityValidator.hashPassword(password, salt)
        assertEquals(hash, testHash)

        val wrongHash = com.example.security.SecurityValidator.hashPassword("WrongPassword123", salt)
        assertNotEquals(hash, wrongHash)
    }
}
