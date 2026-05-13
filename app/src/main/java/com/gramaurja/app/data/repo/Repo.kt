package com.gramaurja.app.data.repo

import com.gramaurja.app.data.Supa
import com.gramaurja.app.data.model.*
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

@kotlinx.serialization.Serializable
private data class CropInsert(val name: String, @kotlinx.serialization.SerialName("recommended_minutes") val recommendedMinutes: Int)

object Repo {
    private val client = Supa.client

    suspend fun signIn(email: String, password: String) =
        client.auth.signInWith(Email) { this.email = email; this.password = password }

    suspend fun signUp(email: String, password: String, displayName: String) =
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = kotlinx.serialization.json.buildJsonObject {
                put("display_name", kotlinx.serialization.json.JsonPrimitive(displayName))
            }
        }

    suspend fun signOut() = client.auth.signOut()
    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    suspend fun getProfile(uid: String): Profile? =
        client.from("profiles").select { filter { eq("id", uid) }; limit(1) }
            .decodeList<Profile>().firstOrNull()

    suspend fun updateProfile(uid: String, upd: ProfileUpdate) {
        client.from("profiles").update(upd) { filter { eq("id", uid) } }
    }

    suspend fun isAdmin(uid: String): Boolean =
        client.from("user_roles").select { filter { eq("user_id", uid) } }
            .decodeList<UserRole>().any { it.role == "admin" }

    suspend fun listZones(): List<Zone> =
        client.from("zones").select { order("name", Order.ASCENDING) }.decodeList()

    suspend fun getZone(id: String): Zone? =
        client.from("zones").select { filter { eq("id", id) }; limit(1) }
            .decodeList<Zone>().firstOrNull()

    suspend fun reportStatus(zoneId: String, userId: String, status: String) {
        client.from("status_updates").insert(StatusInsert(zoneId, userId, status))
    }

    suspend fun recentUpdates(zoneId: String, n: Int = 8): List<StatusUpdate> =
        client.from("status_updates").select {
            filter { eq("zone_id", zoneId) }
            order("created_at", Order.DESCENDING)
            limit(n.toLong())
        }.decodeList()

    suspend fun listCrops(): List<Crop> =
        client.from("crops").select { order("name", Order.ASCENDING) }.decodeList()

    suspend fun addZone(name: String, description: String?) {
        client.from("zones").insert(ZoneInsert(name, description))
    }
    suspend fun deleteZone(id: String) {
        client.from("zones").delete { filter { eq("id", id) } }
    }
    suspend fun addCrop(name: String, minutes: Int) {
        client.from("crops").insert(CropInsert(name, minutes))
    }
    suspend fun deleteCrop(id: String) {
        client.from("crops").delete { filter { eq("id", id) } }
    }
}
