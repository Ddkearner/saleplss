package com.example.data

import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class SalesplusRepository(
    private val storeDao: StoreDao,
    private val transactionDao: TransactionDao,
    private val campaignDao: CampaignDao,
    val syncManager: SalesSyncManager? = null,
    val networkMonitor: NetworkMonitor? = null
) {
    val storeProfile: Flow<StoreProfile?> = storeDao.getStoreProfile()
    val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()
    val allCampaigns: Flow<List<CampaignRecord>> = campaignDao.getAllCampaigns()
    val pendingSyncCount: Flow<Int> = transactionDao.getPendingSyncCount()

    suspend fun saveStoreProfile(name: String, location: String, language: String) {
        val profile = StoreProfile(
            id = 1,
            storeName = name.trim(),
            storeLocation = location.trim(),
            primaryCurrency = "₹",
            language = language,
            isConfigured = true,
            updatedAt = System.currentTimeMillis()
        )
        storeDao.saveStoreProfile(profile)
    }

    suspend fun addTransaction(
        customerName: String,
        phoneNumber: String,
        amount: Double,
        categories: String = "",
        paymentMode: PaymentMode = PaymentMode.CASH,
        customTimestamp: Long? = null,
        customInvoiceNumber: String? = null
    ): Long {
        val isDeviceOnline = networkMonitor?.isConnected() ?: true
        val generatedInv = customInvoiceNumber ?: "INV-${(System.currentTimeMillis() % 1000000).toString().padStart(6, '0')}"

        val record = TransactionRecord(
            uuid = UUID.randomUUID().toString(),
            invoiceNumber = generatedInv,
            customerName = customerName.trim(),
            phoneNumber = phoneNumber.trim(),
            amount = amount,
            categories = categories.trim(),
            paymentMode = paymentMode,
            timestamp = customTimestamp ?: System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING,
            syncedAt = null,
            offlineCreated = !isDeviceOnline
        )

        // 1. Guaranteed Local-First Room persistence
        val insertedId = transactionDao.insertTransaction(record)

        // 2. If online, trigger background sync
        if (isDeviceOnline) {
            syncManager?.syncPendingTransactions()
        }

        return insertedId
    }

    suspend fun insertTransactionRecord(record: TransactionRecord): Long {
        return transactionDao.insertTransaction(record)
    }

    suspend fun syncPendingTransactions(): Int {
        return syncManager?.syncPendingTransactions() ?: 0
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    suspend fun addCampaign(
        title: String,
        messageText: String,
        targetAudience: String,
        recipientCount: Int,
        imageUri: String? = null
    ): Long {
        val campaign = CampaignRecord(
            title = title.trim(),
            messageText = messageText.trim(),
            targetAudience = targetAudience,
            recipientCount = recipientCount,
            imageUri = imageUri,
            timestamp = System.currentTimeMillis(),
            isActive = true
        )
        return campaignDao.insertCampaign(campaign)
    }

    suspend fun deleteCampaign(id: Long) {
        campaignDao.deleteCampaign(id)
    }

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
        campaignDao.deleteAllCampaigns()
    }
}
