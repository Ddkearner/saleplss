package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Payment method for retail transactions: CASH or UPI / QR.
 */
enum class PaymentMode {
    CASH,
    UPI_QR
}

/**
 * Sync status for offline-first local Room architecture.
 */
enum class SyncStatus {
    PENDING,    // Saved locally, pending background upload
    SYNCING,    // Actively syncing to cloud
    SYNCED,     // Successfully synced and confirmed in cloud
    FAILED      // Temporary sync failure, will retry automatically
}

/**
 * Transaction entity recording each sale at POS with category tags, payment mode,
 * and offline-first local synchronization metadata.
 */
@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val invoiceNumber: String = "",
    val customerName: String,
    val phoneNumber: String,
    val amount: Double,
    val categories: String = "", // Comma-separated categories e.g. "Saree x2, Kurti x1"
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val syncedAt: Long? = null,
    val offlineCreated: Boolean = true
)
