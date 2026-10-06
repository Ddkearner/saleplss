package com.example.data

import kotlinx.coroutines.flow.Flow

class SalesplusRepository(
    private val storeDao: StoreDao,
    private val transactionDao: TransactionDao,
    private val campaignDao: CampaignDao
) {
    val storeProfile: Flow<StoreProfile?> = storeDao.getStoreProfile()
    val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()
    val allCampaigns: Flow<List<CampaignRecord>> = campaignDao.getAllCampaigns()

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
        customTimestamp: Long? = null
    ): Long {
        val record = TransactionRecord(
            customerName = customerName.trim(),
            phoneNumber = phoneNumber.trim(),
            amount = amount,
            categories = categories.trim(),
            timestamp = customTimestamp ?: System.currentTimeMillis()
        )
        return transactionDao.insertTransaction(record)
    }

    suspend fun insertTransactionRecord(record: TransactionRecord): Long {
        return transactionDao.insertTransaction(record)
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
