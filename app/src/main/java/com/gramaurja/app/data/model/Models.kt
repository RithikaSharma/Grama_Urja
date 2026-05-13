@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.gramaurja.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Zone(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("power_status") val powerStatus: String = "unknown",
    @SerialName("last_updated_at") val lastUpdatedAt: String? = null,
    @SerialName("last_updated_by") val lastUpdatedBy: String? = null,
)

@Serializable
data class Profile(
    val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("zone_id") val zoneId: String? = null,
    val language: String = "en",
    val phone: String? = null,
)

@Serializable
data class StatusUpdate(
    val id: String,
    @SerialName("zone_id") val zoneId: String,
    @SerialName("user_id") val userId: String? = null,
    val status: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class StatusInsert(
    @SerialName("zone_id") val zoneId: String,
    @SerialName("user_id") val userId: String,
    val status: String,
)

@Serializable
data class Crop(
    val id: String,
    val name: String,
    @SerialName("recommended_minutes") val recommendedMinutes: Int,
    @SerialName("water_liters_per_acre") val waterLitersPerAcre: Int? = null,
)

@Serializable
data class UserRole(
    @SerialName("user_id") val userId: String,
    val role: String,
)

@Serializable
data class ProfileUpdate(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("zone_id") val zoneId: String? = null,
    val language: String? = null,
    val phone: String? = null,
)

@Serializable
data class ZoneInsert(
    val name: String,
    val description: String? = null,
)