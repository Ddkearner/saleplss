package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton configuration for the retail store.
 * Currency is always and only Indian Rupee (₹).
 */
@Entity(tableName = "store_profile")
data class StoreProfile(
    @PrimaryKey val id: Int = 1,
    val storeName: String,
    val storeLocation: String,
    val primaryCurrency: String = "₹",
    val language: String = "en",
    val isConfigured: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
