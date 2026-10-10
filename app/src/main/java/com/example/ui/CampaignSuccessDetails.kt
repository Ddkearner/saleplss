package com.example.ui

data class CampaignSuccessDetails(
    val title: String,
    val messageText: String,
    val targetAudience: String,
    val recipientCount: Int,
    val imageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
