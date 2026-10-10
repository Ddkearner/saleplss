package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CampaignRecord
import com.example.data.SalesplusDatabase
import com.example.data.SalesSyncManager
import com.example.data.StoreProfile
import com.example.data.SyncStatus
import com.example.data.TransactionRecord
import com.example.ui.CustomerSegment
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.LocalizationProvider
import com.example.ui.SalesplusViewModel
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("salesplus", appName)
    }

    @Test
    fun `test store profile language and currency persistence`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = SalesplusDatabase.getDatabase(context)
        val storeDao = db.storeDao()
        val txnDao = db.transactionDao()
        val campaignDao = db.campaignDao()

        // Test saving initial store setup with Hindi language and fixed Rupee
        val initialProfile = StoreProfile(
            id = 1,
            storeName = "Sharma Garments",
            storeLocation = "Connaught Place, New Delhi",
            primaryCurrency = "₹",
            language = "hi",
            isConfigured = true
        )
        storeDao.saveStoreProfile(initialProfile)

        val retrievedProfile = storeDao.getStoreProfile().first()
        assertNotNull(retrievedProfile)
        assertEquals("Sharma Garments", retrievedProfile?.storeName)
        assertEquals("₹", retrievedProfile?.primaryCurrency)
        assertEquals("hi", retrievedProfile?.language)
        assertTrue(retrievedProfile?.isConfigured == true)

        // Test localized Hindi strings
        val strings = LocalizationProvider.getStrings(retrievedProfile?.language ?: "en")
        assertEquals("आज की बिक्री", strings.todaySales)

        // Test recording a transaction with categories
        val txn = TransactionRecord(
            customerName = "Rahul Verma",
            phoneNumber = "9876543210",
            amount = 125000.0,
            categories = "Saree, Suit / Kurti"
        )
        val txnId = txnDao.insertTransaction(txn)
        assertTrue(txnId > 0)

        val allTxns = txnDao.getAllTransactions().first()
        assertTrue(allTxns.isNotEmpty())
        assertEquals("Rahul Verma", allTxns.first().customerName)
        assertEquals(125000.0, allTxns.first().amount, 0.001)
        assertEquals("Saree, Suit / Kurti", allTxns.first().categories)

        // Test Indian Lakhs format: 1,25,000
        val formatted = IndianCurrencyFormatter.format(125000.0)
        assertEquals("₹ 1,25,000", formatted)

        // Test campaign persistence
        val campaign = CampaignRecord(
            title = "New Festive Saree Collection",
            messageText = "Naya stock aa gaya hai",
            targetAudience = "VIP",
            recipientCount = 12,
            isActive = true
        )
        val campId = campaignDao.insertCampaign(campaign)
        assertTrue(campId > 0)

        val allCamps = campaignDao.getAllCampaigns().first()
        assertEquals(1, allCamps.size)
        assertEquals("New Festive Saree Collection", allCamps.first().title)
        assertEquals("VIP", allCamps.first().targetAudience)

        // Test segmentation logic
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = SalesplusViewModel(app)
        val profiles = vm.computeCustomerProfiles(allTxns)
        assertTrue(profiles.isNotEmpty())
        assertEquals(CustomerSegment.VIP, profiles.first().segment)
    }

    @Test
    fun `test local-first Room offline sales entry and background synchronization`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = SalesplusDatabase.getDatabase(context)
        val txnDao = db.transactionDao()
        txnDao.deleteAllTransactions()

        val networkMonitor = NetworkMonitor(context)
        // Simulate device offline
        networkMonitor.setSimulatedOffline(true)
        assertFalse(networkMonitor.isConnected())

        val syncManager = SalesSyncManager(txnDao, networkMonitor)

        // 1. Enter sales while completely offline
        val offlineSale1 = TransactionRecord(
            customerName = "Sunita Rao",
            phoneNumber = "9820011223",
            amount = 4500.0,
            categories = "Saree x1",
            invoiceNumber = "INV-OFF001",
            syncStatus = SyncStatus.PENDING,
            offlineCreated = true
        )
        val offlineSale2 = TransactionRecord(
            customerName = "Manish Joshi",
            phoneNumber = "9819922334",
            amount = 3200.0,
            categories = "Kurti x2",
            invoiceNumber = "INV-OFF002",
            syncStatus = SyncStatus.PENDING,
            offlineCreated = true
        )

        txnDao.insertTransaction(offlineSale1)
        txnDao.insertTransaction(offlineSale2)

        // Verify local Room persistence worked while offline
        val pendingList = txnDao.getPendingSyncTransactions()
        assertEquals(2, pendingList.size)
        assertEquals(2, txnDao.getPendingSyncCount().first())

        val savedFirst = pendingList.first()
        assertEquals("Sunita Rao", savedFirst.customerName)
        assertEquals(SyncStatus.PENDING, savedFirst.syncStatus)
        assertTrue(savedFirst.offlineCreated)

        // Attempting to sync while offline should fail gracefully without crashing
        val syncedWhileOffline = syncManager.syncPendingTransactions()
        assertEquals(0, syncedWhileOffline)
        assertEquals(2, txnDao.getPendingSyncCount().first())

        // 2. Network connection is restored!
        networkMonitor.setSimulatedOffline(false)
        assertTrue(networkMonitor.isConnected())

        // 3. Trigger synchronization
        val syncedCount = syncManager.syncPendingTransactions()
        assertEquals(2, syncedCount)

        // Verify all records in Room are now marked SYNCED
        assertEquals(0, txnDao.getPendingSyncCount().first())
        val allTxns = txnDao.getAllTransactions().first()
        assertEquals(2, allTxns.size)
        for (txn in allTxns) {
            assertEquals(SyncStatus.SYNCED, txn.syncStatus)
            assertNotNull(txn.syncedAt)
        }
    }
}
