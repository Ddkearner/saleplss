package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val messageText: String,
    val targetAudience: String, // "ALL", "VIP", "REGULAR", "INACTIVE"
    val recipientCount: Int,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
