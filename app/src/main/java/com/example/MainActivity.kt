package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.HistorySubTab
import com.example.ui.SalesplusViewModel
import com.example.ui.Screen
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.CampaignSuccessScreen
import com.example.ui.screens.CreateCampaignScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PaymentSuccessScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.SalesplusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesplusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppleSystemBg
                ) {
                    SalesplusApp()
                }
            }
        }
    }
}

@Composable
fun SalesplusApp(
    viewModel: SalesplusViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val historySubTab by viewModel.historySubTab.collectAsState()
    val storeProfile by viewModel.storeProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val setupForm by viewModel.setupForm.collectAsState()
    val addTxnForm by viewModel.addTxnForm.collectAsState()
    val campaignForm by viewModel.campaignForm.collectAsState()
    val isDemoMode by viewModel.isDemoMode.collectAsState()
    val dismissedCampaignId by viewModel.dismissedCampaignId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isSimulatedOffline by viewModel.isSimulatedOffline.collectAsState()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsState()

    val customerProfiles = remember(transactions) {
        viewModel.computeCustomerProfiles(transactions)
    }

    val comprehensiveReport = remember(transactions) {
        viewModel.computeComprehensiveReport(transactions)
    }

    val weeklyActivity = remember(transactions) {
        viewModel.computeWeeklyActivity(transactions)
    }

    Crossfade(
        targetState = currentScreen,
        label = "screen_crossfade"
    ) { screen ->
        when (screen) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        viewModel.onSplashFinished()
                    }
                )
            }

            is Screen.Setup -> {
                SetupScreen(
                    state = setupForm,
                    onLanguageChange = { viewModel.updateSetupLanguage(it) },
                    onStoreNameChange = { viewModel.updateSetupStoreName(it) },
                    onStoreLocationChange = { viewModel.updateSetupStoreLocation(it) },
                    onNextStep = { viewModel.nextSetupStep() },
                    onPrevStep = { viewModel.prevSetupStep() }
                )
            }

            is Screen.Main -> {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppleSystemBg)
                        .statusBarsPadding(),
                    containerColor = AppleSystemBg,
                    bottomBar = {
                        AppleBottomNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            AppTab.HOME -> {
                                HomeScreen(
                                    storeProfile = storeProfile,
                                    transactions = transactions,
                                    customerProfiles = customerProfiles,
                                    campaigns = campaigns,
                                    dismissedCampaignId = dismissedCampaignId,
                                    isDemoMode = isDemoMode,
                                    todaySales = comprehensiveReport.todaySales,
                                    todaySalesCount = comprehensiveReport.todayCount,
                                    isOnline = isOnline,
                                    isSimulatedOffline = isSimulatedOffline,
                                    pendingSyncCount = pendingSyncCount,
                                    onToggleSimulatedOffline = { viewModel.toggleSimulatedOffline() },
                                    onTriggerSync = { viewModel.triggerManualSync() },
                                    onOpenAddTransaction = {
                                        viewModel.navigateTo(Screen.AddTransaction)
                                    },
                                    onOpenCreateCampaign = {
                                        viewModel.navigateTo(Screen.CreateCampaign)
                                    },
                                    onToggleDemoMode = {
                                        viewModel.toggleDemoMode()
                                    },
                                    onDismissCampaignNotification = { id ->
                                        viewModel.dismissHomeNotification(id)
                                    }
                                )
                            }

                            AppTab.REPORT -> {
                                ReportScreen(
                                    report = comprehensiveReport,
                                    weeklyActivity = weeklyActivity,
                                    campaigns = campaigns
                                )
                            }

                            AppTab.HISTORY -> {
                                HistoryScreen(
                                    transactions = transactions,
                                    customerProfiles = customerProfiles,
                                    historySubTab = historySubTab,
                                    searchQuery = searchQuery,
                                    isOnline = isOnline,
                                    pendingSyncCount = pendingSyncCount,
                                    onTriggerSync = { viewModel.triggerManualSync() },
                                    onSubTabChange = { viewModel.selectHistorySubTab(it) },
                                    onSearchQueryChange = { viewModel.updateSearchQuery(it) }
                                )
                            }
                        }
                    }
                }
            }

            is Screen.AddTransaction -> {
                AddTransactionScreen(
                    language = storeProfile?.language ?: setupForm.language,
                    formState = addTxnForm,
                    isOnline = isOnline,
                    onCustomerNameChange = { viewModel.updateCustomerName(it) },
                    onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
                    onAddCartItem = { viewModel.addCartItem(it) },
                    onRemoveCartItem = { viewModel.removeCartItem(it) },
                    onUpdateCartItemCategory = { id, cat -> viewModel.updateCartItemCategory(id, cat) },
                    onUpdateCartItemQuantity = { id, qty -> viewModel.updateCartItemQuantity(id, qty) },
                    onUpdateCartItemPrice = { id, price -> viewModel.updateCartItemPrice(id, price) },
                    onSubmitWithMode = { mode ->
                        viewModel.submitTransactionWithPaymentMode(mode)
                    },
                    onBack = {
                        viewModel.navigateTo(Screen.Main)
                    }
                )
            }

            is Screen.PaymentSuccess -> {
                PaymentSuccessScreen(
                    details = screen.details,
                    onDone = {
                        viewModel.navigateTo(Screen.Main)
                    },
                    onNewSale = {
                        viewModel.resetAddTxnForm()
                        viewModel.navigateTo(Screen.AddTransaction)
                    }
                )
            }

            is Screen.CreateCampaign -> {
                CreateCampaignScreen(
                    formState = campaignForm,
                    customerProfiles = customerProfiles,
                    storeName = storeProfile?.storeName ?: "Salesplus Store",
                    onTitleChange = { viewModel.updateCampaignTitle(it) },
                    onMessageChange = { viewModel.updateCampaignMessage(it) },
                    onAudienceChange = { viewModel.updateCampaignAudience(it) },
                    onImageChange = { viewModel.updateCampaignImage(it) },
                    onSubmit = {
                        viewModel.submitCampaign(
                            customerProfiles = customerProfiles,
                            onSuccess = {
                            }
                        )
                    },
                    onBack = {
                        viewModel.navigateTo(Screen.Main)
                    }
                )
            }

            is Screen.CampaignSuccess -> {
                CampaignSuccessScreen(
                    details = screen.details,
                    onDone = {
                        viewModel.navigateTo(Screen.Main)
                    },
                    onNewOffer = {
                        viewModel.navigateTo(Screen.CreateCampaign)
                    }
                )
            }
        }
    }
}

@Composable
fun AppleBottomNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppleCardBg)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            color = AppleSystemGray5,
            thickness = 0.8.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppleBottomNavItem(
                icon = Icons.Default.Storefront,
                label = "Home",
                isSelected = selectedTab == AppTab.HOME,
                testTag = "tab_home",
                onClick = { onTabSelected(AppTab.HOME) }
            )
            AppleBottomNavItem(
                icon = Icons.Default.Assessment,
                label = "Reports",
                isSelected = selectedTab == AppTab.REPORT,
                testTag = "tab_report",
                onClick = { onTabSelected(AppTab.REPORT) }
            )
            AppleBottomNavItem(
                icon = Icons.Default.ReceiptLong,
                label = "History",
                isSelected = selectedTab == AppTab.HISTORY,
                testTag = "tab_history",
                onClick = { onTabSelected(AppTab.HISTORY) }
            )
        }
    }
}

@Composable
private fun AppleBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppleBlack else AppleSystemGray,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = AppleFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) AppleBlack else AppleSystemGray
        )
    }
}
