package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Transaction entity recording each 4-second sale at POS with category tags.
 */
@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val phoneNumber: String,
    val amount: Double,
    val categories: String = "", // Comma-separated categories e.g. "Saree, Suit / Kurti"
    val timestamp: Long = System.currentTimeMillis()
)
