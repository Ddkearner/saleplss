package com.example.ui

import com.example.data.PaymentMode
import com.example.data.SyncStatus

/**
 * Data needed to render the PhonePe-style payment success screen with offline/sync info.
 */
data class PaymentSuccessDetails(
    val transactionId: Long,
    val customerName: String,
    val phoneNumber: String,
    val totalAmount: Double,
    val paymentMode: PaymentMode,
    val itemsSummary: String,
    val timestamp: Long = System.currentTimeMillis(),
    val invoiceNumber: String = "",
    val isOfflineCreated: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)
