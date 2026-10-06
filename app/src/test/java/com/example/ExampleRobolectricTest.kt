package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CampaignRecord
import com.example.data.SalesplusDatabase
import com.example.data.StoreProfile
import com.example.data.TransactionRecord
import com.example.ui.CustomerSegment
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.LocalizationProvider
import com.example.ui.SalesplusViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
}
