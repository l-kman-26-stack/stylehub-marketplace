package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long,
    val name: String,
    val role: String, // e.g. Master Barber, Senior Stylist, Master Braider
    val bio: String = "",
    val specialties: String = "", // e.g. "Fade, Beard Sculpt, Hot Towel"
    val avatarInitials: String = ""
)
