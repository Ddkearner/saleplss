package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CampaignRecord
import com.example.data.PaymentMode
import com.example.data.SalesplusDatabase
import com.example.data.SalesplusRepository
import com.example.data.SalesSyncManager
import com.example.data.StoreProfile
import com.example.data.SyncState
import com.example.data.SyncStatus
import com.example.data.TransactionRecord
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Splash : Screen()
    object Setup : Screen()
    object Main : Screen() // Houses Bottom Navigation: Home, Report, History
    object AddTransaction : Screen() // Quick Multi-Item Sale
    object CreateCampaign : Screen() // Offer / Message broadcast
    data class PaymentSuccess(val details: PaymentSuccessDetails) : Screen() // PhonePe style success screen
    data class CampaignSuccess(val details: CampaignSuccessDetails) : Screen() // Campaign launched success screen
}

enum class AppTab {
    HOME, REPORT, HISTORY
}

enum class HistorySubTab {
    RECENT_SALES, ALL_CUSTOMERS
}

enum class CustomerSegment {
    VIP,        // Heavy spenders (₹5,000+ total or single purchase)
    REGULAR,    // Frequent buyers (2+ visits)
    DORMANT,    // Not visited in 45+ days
    NEW_BUYER   // Single recent purchase
}

data class CustomerProfile(
    val phoneNumber: String,
    val customerName: String,
    val totalSpend: Double,
    val visitCount: Int,
    val lastVisitTimestamp: Long,
    val categories: List<String>,
    val segment: CustomerSegment
)

val DefaultGarmentCategories = listOf(
    "Saree",
    "Suit / Kurti",
    "Menswear",
    "Footwear / Access."
)

data class DayActivity(
    val dayLabel: String,
    val amount: Double,
    val count: Int,
    val isToday: Boolean
)

data class CategoryBreakdown(
    val categoryName: String,
    val count: Int,
    val totalAmount: Double
)

data class ComprehensiveReport(
    val todaySales: Double,
    val todayCount: Int,
    val weekSales: Double,
    val weekCount: Int,
    val totalSales: Double,
    val totalCount: Int,
    val totalCashSales: Double,
    val totalCashCount: Int,
    val totalOnlineSales: Double,
    val totalOnlineCount: Int,
    val totalUniqueCustomers: Int,
    val repeatCustomersCount: Int,
    val repeatRatePercentage: Double,
    val averageTicket: Double,
    val categoryBreakdowns: List<CategoryBreakdown>,
    val vipCustomersCount: Int,
    val dormantCustomersCount: Int,
    val regularCustomersCount: Int
)

data class SetupFormState(
    val step: Int = 1,
    val language: String = "en",
    val storeName: String = "",
    val storeLocation: String = "",
    val error: String? = null
)

data class AddTxnFormState(
    val customerName: String = "",
    val phoneNumber: String = "",
    val matchedCustomerProfile: CustomerProfile? = null,
    val items: List<CartItem> = listOf(CartItem(category = "Saree", quantity = 1, price = 0.0)),
    val error: String? = null
) {
    val totalAmount: Double get() = items.sumOf { it.total }
}

data class CreateCampaignFormState(
    val title: String = "",
    val messageText: String = "",
    val targetAudience: String = "ALL", // "ALL", "VIP", "REGULAR", "INACTIVE"
    val imageUri: String? = null,
    val error: String? = null
)

class SalesplusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SalesplusRepository
    val networkMonitor: NetworkMonitor
    val syncManager: SalesSyncManager

    init {
        val database = SalesplusDatabase.getDatabase(application)
        networkMonitor = NetworkMonitor(application)
        syncManager = SalesSyncManager(database.transactionDao(), networkMonitor)
        repository = SalesplusRepository(
            database.storeDao(),
            database.transactionDao(),
            database.campaignDao(),
            syncManager,
            networkMonitor
        )
        // Automatic background synchronization when network is available / restored
        syncManager.startAutoSync(viewModelScope)
    }

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
    val isSimulatedOffline: StateFlow<Boolean> = networkMonitor.isSimulatedOffline
    val pendingSyncCount: StateFlow<Int> = syncManager.pendingCountFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val syncState: StateFlow<SyncState> = syncManager.syncState
    val lastSyncTimestamp: StateFlow<Long?> = syncManager.lastSyncTimestamp

    fun toggleSimulatedOffline() {
        val currentlySimulated = networkMonitor.isSimulatedOffline.value
        networkMonitor.setSimulatedOffline(!currentlySimulated)
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            syncManager.syncPendingTransactions()
        }
    }

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppTab.HOME)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _historySubTab = MutableStateFlow(HistorySubTab.RECENT_SALES)
    val historySubTab: StateFlow<HistorySubTab> = _historySubTab.asStateFlow()

    val storeProfile: StateFlow<StoreProfile?> = repository.storeProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val transactions: StateFlow<List<TransactionRecord>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val campaigns: StateFlow<List<CampaignRecord>> = repository.allCampaigns.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _setupForm = MutableStateFlow(SetupFormState())
    val setupForm: StateFlow<SetupFormState> = _setupForm.asStateFlow()

    private val _addTxnForm = MutableStateFlow(AddTxnFormState())
    val addTxnForm: StateFlow<AddTxnFormState> = _addTxnForm.asStateFlow()

    private val _campaignForm = MutableStateFlow(CreateCampaignFormState())
    val campaignForm: StateFlow<CreateCampaignFormState> = _campaignForm.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _dismissedCampaignId = MutableStateFlow<Long?>(null)
    val dismissedCampaignId: StateFlow<Long?> = _dismissedCampaignId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun selectHistorySubTab(subTab: HistorySubTab) {
        _historySubTab.value = subTab
    }

    fun onSplashFinished() {
        val profile = storeProfile.value
        if (profile != null && profile.isConfigured && profile.storeName.isNotBlank()) {
            _currentScreen.value = Screen.Main
        } else {
            _currentScreen.value = Screen.Setup
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Setup Form Actions
    fun updateSetupLanguage(languageCode: String) {
        _setupForm.value = _setupForm.value.copy(language = languageCode, error = null)
    }

    fun updateSetupStoreName(name: String) {
        _setupForm.value = _setupForm.value.copy(storeName = name, error = null)
    }

    fun updateSetupStoreLocation(location: String) {
        _setupForm.value = _setupForm.value.copy(storeLocation = location, error = null)
    }

    fun nextSetupStep() {
        val current = _setupForm.value
        when (current.step) {
            1 -> {
                _setupForm.value = current.copy(step = 2, error = null)
            }
            2 -> {
                if (current.storeName.trim().isEmpty()) {
                    _setupForm.value = current.copy(error = "Please enter store name")
                    return
                }
                val location = current.storeLocation.trim().ifEmpty { "Main Market" }
                _setupForm.value = current.copy(storeLocation = location, error = null)
                completeSetup()
            }
            else -> {
                completeSetup()
            }
        }
    }

    fun prevSetupStep() {
        val current = _setupForm.value
        if (current.step > 1) {
            _setupForm.value = current.copy(step = current.step - 1, error = null)
        }
    }

    fun completeSetup() {
        val current = _setupForm.value
        viewModelScope.launch {
            repository.saveStoreProfile(
                name = current.storeName,
                location = current.storeLocation,
                language = current.language
            )
            _currentScreen.value = Screen.Main
        }
    }

    // Quick Sale Entry Actions with Cart Items
    fun updateCustomerName(name: String) {
        _addTxnForm.value = _addTxnForm.value.copy(customerName = name, error = null)
    }

    fun updatePhoneNumber(phone: String) {
        val trimmed = phone.trim()
        val allProfiles = computeCustomerProfiles(transactions.value)
        val matched = if (trimmed.length >= 10) {
            allProfiles.firstOrNull { it.phoneNumber.endsWith(trimmed.takeLast(10)) }
        } else null

        val name = when {
            matched != null && matched.customerName.isNotBlank() && matched.customerName != "Customer" -> matched.customerName
            _addTxnForm.value.customerName.isNotBlank() -> _addTxnForm.value.customerName
            else -> ""
        }

        _addTxnForm.value = _addTxnForm.value.copy(
            phoneNumber = phone,
            customerName = name,
            matchedCustomerProfile = matched,
            error = null
        )
    }

    fun addCartItem(defaultCategory: String = "Saree") {
        val updated = _addTxnForm.value.items.toMutableList()
        updated.add(CartItem(category = defaultCategory, quantity = 1, price = 0.0))
        _addTxnForm.value = _addTxnForm.value.copy(items = updated, error = null)
    }

    fun removeCartItem(itemId: String) {
        val currentList = _addTxnForm.value.items
        if (currentList.size <= 1) {
            // Keep at least one item, just reset it
            _addTxnForm.value = _addTxnForm.value.copy(
                items = listOf(CartItem(category = "Saree", quantity = 1, price = 0.0)),
                error = null
            )
            return
        }
        val updated = currentList.filterNot { it.id == itemId }
        _addTxnForm.value = _addTxnForm.value.copy(items = updated, error = null)
    }

    fun updateCartItemCategory(itemId: String, category: String) {
        val updated = _addTxnForm.value.items.map { item ->
            if (item.id == itemId) item.copy(category = category) else item
        }
        _addTxnForm.value = _addTxnForm.value.copy(items = updated, error = null)
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        val validQty = quantity.coerceAtLeast(1)
        val updated = _addTxnForm.value.items.map { item ->
            if (item.id == itemId) item.copy(quantity = validQty) else item
        }
        _addTxnForm.value = _addTxnForm.value.copy(items = updated, error = null)
    }

    fun updateCartItemPrice(itemId: String, price: Double) {
        val updated = _addTxnForm.value.items.map { item ->
            if (item.id == itemId) item.copy(price = price.coerceAtLeast(0.0)) else item
        }
        _addTxnForm.value = _addTxnForm.value.copy(items = updated, error = null)
    }

    fun resetAddTxnForm() {
        _addTxnForm.value = AddTxnFormState()
    }

    fun submitTransactionWithPaymentMode(mode: PaymentMode) {
        val form = _addTxnForm.value
        val name = form.customerName.trim().ifEmpty { "Customer" }
        val phone = form.phoneNumber.trim()
        val totalAmount = form.totalAmount

        if (phone.isEmpty()) {
            _addTxnForm.value = form.copy(error = "Please enter customer mobile number")
            return
        }
        if (totalAmount <= 0.0) {
            _addTxnForm.value = form.copy(error = "Please enter product price (₹)")
            return
        }

        // Build item summary e.g. "Saree (x2), Suit / Kurti (x1)"
        val categoriesStr = form.items.filter { it.price > 0.0 }.joinToString(", ") { item ->
            val cat = item.category.ifBlank { "Garment" }
            if (item.quantity > 1) "$cat x${item.quantity}" else cat
        }.ifBlank { "General Sale" }

        val isDeviceOnline = networkMonitor.isConnected()
        val invoiceNum = "INV-${(System.currentTimeMillis() % 1000000).toString().padStart(6, '0')}"

        viewModelScope.launch {
            val recordId = repository.addTransaction(
                customerName = name,
                phoneNumber = phone,
                amount = totalAmount,
                categories = categoriesStr,
                paymentMode = mode,
                customInvoiceNumber = invoiceNum
            )

            val successDetails = PaymentSuccessDetails(
                transactionId = recordId,
                customerName = name,
                phoneNumber = phone,
                totalAmount = totalAmount,
                paymentMode = mode,
                itemsSummary = categoriesStr,
                timestamp = System.currentTimeMillis(),
                invoiceNumber = invoiceNum,
                isOfflineCreated = !isDeviceOnline,
                syncStatus = if (isDeviceOnline) SyncStatus.SYNCED else SyncStatus.PENDING
            )

            // Reset form and navigate to PhonePe-like payment success screen
            _addTxnForm.value = AddTxnFormState()
            _currentScreen.value = Screen.PaymentSuccess(successDetails)
        }
    }

    // Campaign Actions
    fun updateCampaignTitle(title: String) {
        _campaignForm.value = _campaignForm.value.copy(title = title, error = null)
    }

    fun updateCampaignMessage(msg: String) {
        _campaignForm.value = _campaignForm.value.copy(messageText = msg, error = null)
    }

    fun updateCampaignAudience(audience: String) {
        _campaignForm.value = _campaignForm.value.copy(targetAudience = audience, error = null)
    }

    fun updateCampaignImage(uri: String?) {
        _campaignForm.value = _campaignForm.value.copy(imageUri = uri)
    }

    fun dismissHomeNotification(campaignId: Long) {
        _dismissedCampaignId.value = campaignId
    }

    fun submitCampaign(customerProfiles: List<CustomerProfile>, onSuccess: () -> Unit) {
        val form = _campaignForm.value
        val title = form.title.trim().ifEmpty { "Store Offer" }
        val msg = form.messageText.trim()
        if (msg.isEmpty()) {
            _campaignForm.value = form.copy(error = "Please enter or select message")
            return
        }

        val targetCount = when (form.targetAudience) {
            "VIP" -> customerProfiles.count { it.segment == CustomerSegment.VIP }
            "REGULAR" -> customerProfiles.count { it.segment == CustomerSegment.REGULAR }
            "INACTIVE" -> customerProfiles.count { it.segment == CustomerSegment.DORMANT }
            else -> customerProfiles.size
        }

        viewModelScope.launch {
            repository.addCampaign(
                title = title,
                messageText = msg,
                targetAudience = form.targetAudience,
                recipientCount = if (targetCount > 0) targetCount else 1,
                imageUri = form.imageUri
            )
            val successDetails = CampaignSuccessDetails(
                title = title,
                messageText = msg,
                targetAudience = form.targetAudience,
                recipientCount = if (targetCount > 0) targetCount else 1,
                imageUri = form.imageUri
            )
            _campaignForm.value = CreateCampaignFormState()
            _dismissedCampaignId.value = null
            _currentScreen.value = Screen.CampaignSuccess(successDetails)
            onSuccess()
        }
    }

    // Toggle Demo / Test Simulation Mode
    fun toggleDemoMode() {
        viewModelScope.launch {
            if (_isDemoMode.value) {
                repository.clearAllData()
                _isDemoMode.value = false
            } else {
                seedRealisticDemoData()
                _isDemoMode.value = true
            }
        }
    }

    private suspend fun seedRealisticDemoData() {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        val sampleList = listOf(
            // Today sales with CASH and UPI_QR (One offline-created pending sale)
            TransactionRecord(
                customerName = "Pooja Sharma",
                phoneNumber = "9820123456",
                amount = 6500.0,
                categories = "Saree x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (2 * oneHour),
                invoiceNumber = "INV-849102",
                syncStatus = SyncStatus.PENDING,
                offlineCreated = true
            ),
            TransactionRecord(
                customerName = "Rajesh Kumar",
                phoneNumber = "9811234567",
                amount = 3200.0,
                categories = "Menswear x2",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (4 * oneHour),
                invoiceNumber = "INV-849098",
                syncStatus = SyncStatus.SYNCED,
                syncedAt = now - (3 * oneHour),
                offlineCreated = false
            ),
            TransactionRecord(
                customerName = "Sunita Gupta",
                phoneNumber = "9899123456",
                amount = 2800.0,
                categories = "Suit / Kurti x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (6 * oneHour),
                invoiceNumber = "INV-849091",
                syncStatus = SyncStatus.SYNCED,
                syncedAt = now - (5 * oneHour),
                offlineCreated = false
            ),
            // Yesterday
            TransactionRecord(
                customerName = "Amit Verma",
                phoneNumber = "9876543210",
                amount = 14500.0,
                categories = "Saree x2, Suit / Kurti x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (1 * oneDay)
            ),
            TransactionRecord(
                customerName = "Neha Patel",
                phoneNumber = "9821345678",
                amount = 4200.0,
                categories = "Footwear / Access. x1",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (1 * oneDay + 3 * oneHour)
            ),
            // 3 days ago (Pooja repeat purchase)
            TransactionRecord(
                customerName = "Pooja Sharma",
                phoneNumber = "9820123456",
                amount = 5500.0,
                categories = "Suit / Kurti x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (3 * oneDay)
            ),
            TransactionRecord(
                customerName = "Vikram Singh",
                phoneNumber = "9833456789",
                amount = 3800.0,
                categories = "Menswear x1",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (3 * oneDay + 2 * oneHour)
            ),
            // 5 days ago (Sunita repeat purchase)
            TransactionRecord(
                customerName = "Sunita Gupta",
                phoneNumber = "9899123456",
                amount = 1900.0,
                categories = "Suit / Kurti x1",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (5 * oneDay)
            ),
            TransactionRecord(
                customerName = "Anjali Mehta",
                phoneNumber = "9810987654",
                amount = 2400.0,
                categories = "Saree x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (5 * oneDay + 4 * oneHour)
            ),
            // 6 days ago
            TransactionRecord(
                customerName = "Ramesh Joshi",
                phoneNumber = "9845123456",
                amount = 7200.0,
                categories = "Menswear x2",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (6 * oneDay)
            ),
            // 20 days ago (Deepak repeat visits)
            TransactionRecord(
                customerName = "Deepak Rao",
                phoneNumber = "9870123456",
                amount = 1800.0,
                categories = "Footwear / Access. x1",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (20 * oneDay)
            ),
            TransactionRecord(
                customerName = "Deepak Rao",
                phoneNumber = "9870123456",
                amount = 2100.0,
                categories = "Menswear x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (22 * oneDay)
            ),
            // 50+ days ago (Inactive customers)
            TransactionRecord(
                customerName = "Priyanka Tiwari",
                phoneNumber = "9819876543",
                amount = 3500.0,
                categories = "Saree x1",
                paymentMode = PaymentMode.CASH,
                timestamp = now - (52 * oneDay)
            ),
            TransactionRecord(
                customerName = "Manish Malhotra",
                phoneNumber = "9867543210",
                amount = 4800.0,
                categories = "Suit / Kurti x1",
                paymentMode = PaymentMode.UPI_QR,
                timestamp = now - (58 * oneDay)
            )
        )

        for (record in sampleList) {
            repository.insertTransactionRecord(record)
        }

        // Add active demo campaign
        repository.addCampaign(
            title = "New Festive Sarees & Suits",
            messageText = "Namaste! Hamare store par naya party-wear collection khul gaya hai. Aapke taste ke hisaab se shandar stock aaya hai, zaroor aayiye.",
            targetAudience = "VIP",
            recipientCount = 3
        )
    }

    // Customer Segmentation Calculation
    fun computeCustomerProfiles(txns: List<TransactionRecord>): List<CustomerProfile> {
        val groupedByPhone = txns.groupBy { it.phoneNumber }
        val now = System.currentTimeMillis()
        val fortyFiveDaysMillis = 45L * 24 * 60 * 60 * 1000

        return groupedByPhone.map { (phone, userTxns) ->
            val totalSpend = userTxns.sumOf { it.amount }
            val visitCount = userTxns.size
            val latestTxn = userTxns.maxByOrNull { it.timestamp } ?: userTxns.first()
            val lastVisit = latestTxn.timestamp
            val name = userTxns.lastOrNull { it.customerName.isNotBlank() && it.customerName != "Customer" }?.customerName
                ?: userTxns.first().customerName

            val allCats = userTxns.flatMap { it.categories.split(",") }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            val isDormant = (now - lastVisit) > fortyFiveDaysMillis
            val segment = when {
                totalSpend >= 5000.0 -> CustomerSegment.VIP
                isDormant && visitCount > 0 -> CustomerSegment.DORMANT
                visitCount >= 2 -> CustomerSegment.REGULAR
                else -> CustomerSegment.NEW_BUYER
            }

            CustomerProfile(
                phoneNumber = phone,
                customerName = name,
                totalSpend = totalSpend,
                visitCount = visitCount,
                lastVisitTimestamp = lastVisit,
                categories = allCats,
                segment = segment
            )
        }.sortedByDescending { it.lastVisitTimestamp }
    }

    // Report Computation with Cash vs UPI breakdown
    fun computeComprehensiveReport(txns: List<TransactionRecord>): ComprehensiveReport {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        val calWeek = Calendar.getInstance()
        calWeek.set(Calendar.HOUR_OF_DAY, 0)
        calWeek.set(Calendar.MINUTE, 0)
        calWeek.set(Calendar.SECOND, 0)
        calWeek.set(Calendar.MILLISECOND, 0)
        calWeek.add(Calendar.DAY_OF_YEAR, -6)
        val startOfWeek = calWeek.timeInMillis

        val todayTxns = txns.filter { it.timestamp >= startOfToday }
        val weekTxns = txns.filter { it.timestamp >= startOfWeek }

        val totalSales = txns.sumOf { it.amount }
        val totalCount = txns.size
        val avgTicket = if (totalCount > 0) totalSales / totalCount else 0.0

        val cashTxns = txns.filter { it.paymentMode == PaymentMode.CASH }
        val onlineTxns = txns.filter { it.paymentMode == PaymentMode.UPI_QR }

        val customerProfiles = computeCustomerProfiles(txns)
        val totalUnique = customerProfiles.size
        val repeatCustomers = customerProfiles.count { it.visitCount > 1 }
        val repeatRate = if (totalUnique > 0) (repeatCustomers.toDouble() / totalUnique) * 100.0 else 0.0

        val breakdowns = DefaultGarmentCategories.map { cat ->
            val matching = txns.filter { it.categories.contains(cat, ignoreCase = true) }
            CategoryBreakdown(
                categoryName = cat,
                count = matching.size,
                totalAmount = matching.sumOf { it.amount }
            )
        }

        return ComprehensiveReport(
            todaySales = todayTxns.sumOf { it.amount },
            todayCount = todayTxns.size,
            weekSales = weekTxns.sumOf { it.amount },
            weekCount = weekTxns.size,
            totalSales = totalSales,
            totalCount = totalCount,
            totalCashSales = cashTxns.sumOf { it.amount },
            totalCashCount = cashTxns.size,
            totalOnlineSales = onlineTxns.sumOf { it.amount },
            totalOnlineCount = onlineTxns.size,
            totalUniqueCustomers = totalUnique,
            repeatCustomersCount = repeatCustomers,
            repeatRatePercentage = repeatRate,
            averageTicket = avgTicket,
            categoryBreakdowns = breakdowns,
            vipCustomersCount = customerProfiles.count { it.segment == CustomerSegment.VIP },
            dormantCustomersCount = customerProfiles.count { it.segment == CustomerSegment.DORMANT },
            regularCustomersCount = customerProfiles.count { it.segment == CustomerSegment.REGULAR }
        )
    }

    fun computeWeeklyActivity(txns: List<TransactionRecord>): List<DayActivity> {
        val result = mutableListOf<DayActivity>()
        val dayFormat = SimpleDateFormat("EEE", Locale.US)

        val dayStarts = mutableListOf<Long>()
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            c.add(Calendar.DAY_OF_YEAR, -i)
            dayStarts.add(c.timeInMillis)
        }

        for (index in dayStarts.indices) {
            val start = dayStarts[index]
            val end = start + (24 * 60 * 60 * 1000L)
            val dayTxns = txns.filter { it.timestamp in start until end }
            val amount = dayTxns.sumOf { it.amount }
            val count = dayTxns.size
            val isToday = (index == dayStarts.size - 1)
            val label = dayFormat.format(Date(start)).uppercase(Locale.US)

            result.add(
                DayActivity(
                    dayLabel = label,
                    amount = amount,
                    count = count,
                    isToday = isToday
                )
            )
        }

        return result
    }
}

// Indian Number System Formatter
object IndianCurrencyFormatter {
    fun format(amount: Double): String {
        val rounded = kotlin.math.round(amount).toLong()
        if (rounded < 1000) {
            return "₹ $rounded"
        }
        val s = rounded.toString()
        val lastThree = s.substring(s.length - 3)
        val otherNumbers = s.substring(0, s.length - 3)
        val formattedOther = otherNumbers.reversed().chunked(2).joinToString(",").reversed()
        return "₹ $formattedOther,$lastThree"
    }
}
