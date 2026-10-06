package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CampaignRecord
import com.example.data.StoreProfile
import com.example.data.TransactionRecord
import com.example.ui.AppStrings
import com.example.ui.CustomerProfile
import com.example.ui.CustomerSegment
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.LocalizationProvider
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleNearBlack
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    storeProfile: StoreProfile?,
    transactions: List<TransactionRecord>,
    customerProfiles: List<CustomerProfile>,
    campaigns: List<CampaignRecord>,
    dismissedCampaignId: Long?,
    isDemoMode: Boolean,
    todaySales: Double,
    todaySalesCount: Int,
    onOpenAddTransaction: () -> Unit,
    onOpenCreateCampaign: () -> Unit,
    onToggleDemoMode: () -> Unit,
    onDismissCampaignNotification: (Long) -> Unit
) {
    val language = storeProfile?.language ?: "en"
    val strings: AppStrings = LocalizationProvider.getStrings(language)
    val storeName = storeProfile?.storeName?.ifBlank { "Salesplus Store" } ?: "Salesplus Store"
    val storeLocation = storeProfile?.storeLocation?.ifBlank { "India" } ?: "India"

    // Active customer group filter: null for all, or specific segment
    var selectedSegment by remember { mutableStateOf<CustomerSegment?>(null) }

    val vipList = remember(customerProfiles) {
        customerProfiles.filter { it.segment == CustomerSegment.VIP }
    }
    val regularList = remember(customerProfiles) {
        customerProfiles.filter { it.segment == CustomerSegment.REGULAR }
    }
    val dormantList = remember(customerProfiles) {
        customerProfiles.filter { it.segment == CustomerSegment.DORMANT }
    }

    val displayCustomers = remember(customerProfiles, selectedSegment) {
        if (selectedSegment == null) {
            customerProfiles.take(10)
        } else {
            customerProfiles.filter { it.segment == selectedSegment }
        }
    }

    val latestActiveCampaign = remember(campaigns, dismissedCampaignId) {
        campaigns.firstOrNull { it.isActive && it.id != dismissedCampaignId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .testTag("home_screen_container")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Store Location, Name, and Test Mode Button (Replacing currency symbol)
            item(key = "home_clean_header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = storeLocation.uppercase(Locale.ROOT),
                            fontSize = 11.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = AppleSystemGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Test Mode / Simulation button (requested in place of rupees symbol)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDemoMode) AppleBlack else AppleCardBg)
                                .clickable { onToggleDemoMode() }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("toggle_demo_mode_button")
                        ) {
                            Icon(
                                imageVector = if (isDemoMode) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = if (isDemoMode) "Reset Test Data" else "Run Test Data",
                                tint = if (isDemoMode) AppleWhite else AppleBlack,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isDemoMode) "Test Mode Active" else "Test Run",
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (isDemoMode) AppleWhite else AppleBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = storeName,
                        fontSize = 28.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.6).sp,
                        color = AppleBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("home_store_name")
                    )
                }
            }

            // Active Campaign Notification Banner
            if (latestActiveCampaign != null) {
                item(key = "home_active_campaign_banner") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppleBlack)
                            .padding(16.dp)
                            .testTag("active_campaign_banner")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (latestActiveCampaign.imageUri != null) {
                                    AsyncImage(
                                        model = latestActiveCampaign.imageUri,
                                        contentDescription = "Campaign Photo",
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AppleWhite.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Campaign,
                                            contentDescription = "Campaign Active",
                                            tint = AppleWhite,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Campaign Running",
                                        fontSize = 11.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        color = AppleWhite.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = latestActiveCampaign.title,
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Sent to ${latestActiveCampaign.recipientCount} ${latestActiveCampaign.targetAudience.lowercase()} customers",
                                        fontSize = 12.sp,
                                        fontFamily = AppleFontFamily,
                                        color = AppleWhite.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDismissCampaignNotification(latestActiveCampaign.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = AppleWhite.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Today's Sales Card
            item(key = "home_hero_card") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(AppleCardBg)
                        .padding(22.dp)
                        .testTag("hero_today_sales_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.todaySales.uppercase(Locale.ROOT),
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = AppleSystemGray
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppleSystemGray6)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$todaySalesCount ${strings.salesToday}",
                                    fontSize = 11.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleNearBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = IndianCurrencyFormatter.format(todaySales),
                            fontSize = 36.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1.0).sp,
                            color = AppleBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AppleBlack)
                                    .clickable { onOpenAddTransaction() }
                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                                    .testTag("hero_add_sale_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = strings.newSaleBtn,
                                    tint = AppleWhite,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = strings.newSaleBtn,
                                    fontSize = 13.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleWhite
                                )
                            }
                        }
                    }
                }
            }

            // Customer Group Filter Tiles
            item(key = "home_rfm_tiles") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomerGroupTile(
                        modifier = Modifier.weight(1f),
                        title = strings.vipCustomers,
                        count = vipList.size,
                        subtitle = "₹5,000+",
                        isSelected = selectedSegment == CustomerSegment.VIP,
                        onClick = {
                            selectedSegment = if (selectedSegment == CustomerSegment.VIP) null else CustomerSegment.VIP
                        }
                    )
                    CustomerGroupTile(
                        modifier = Modifier.weight(1f),
                        title = strings.regularCustomers,
                        count = regularList.size,
                        subtitle = "2+ visits",
                        isSelected = selectedSegment == CustomerSegment.REGULAR,
                        onClick = {
                            selectedSegment = if (selectedSegment == CustomerSegment.REGULAR) null else CustomerSegment.REGULAR
                        }
                    )
                    CustomerGroupTile(
                        modifier = Modifier.weight(1f),
                        title = strings.inactiveCustomers,
                        count = dormantList.size,
                        subtitle = "45+ days",
                        isSelected = selectedSegment == CustomerSegment.DORMANT,
                        onClick = {
                            selectedSegment = if (selectedSegment == CustomerSegment.DORMANT) null else CustomerSegment.DORMANT
                        }
                    )
                }
            }

            // Customer List Header
            item(key = "home_segmented_list_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val listTitle = when (selectedSegment) {
                        CustomerSegment.VIP -> "${strings.vipCustomers} (${vipList.size})"
                        CustomerSegment.REGULAR -> "${strings.regularCustomers} (${regularList.size})"
                        CustomerSegment.DORMANT -> "${strings.inactiveCustomers} (${dormantList.size})"
                        else -> "${strings.allCustomers} (${customerProfiles.size})"
                    }
                    Text(
                        text = listTitle,
                        fontSize = 16.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack
                    )
                    if (selectedSegment != null) {
                        Text(
                            text = "Show All",
                            fontSize = 13.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = AppleBlack,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedSegment = null }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Customers Card Container
            item(key = "home_customer_container") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(AppleCardBg)
                        .padding(18.dp)
                ) {
                    if (displayCustomers.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = strings.noCustomersYet,
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                            Text(
                                text = strings.addFirstSale,
                                fontSize = 12.sp,
                                fontFamily = AppleFontFamily,
                                color = AppleSystemGray
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            displayCustomers.forEachIndexed { index, cust ->
                                CustomerRow(cust)
                                if (index < displayCustomers.lastIndex) {
                                    HorizontalDivider(
                                        color = AppleSystemGray5,
                                        thickness = 0.8.dp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "home_bottom_spacer") {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Floating Action Buttons (New Offer & New Sale)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // New Offer / Campaign Button (Green FAB)
            FloatingActionButton(
                onClick = onOpenCreateCampaign,
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .testTag("create_campaign_fab"),
                shape = RoundedCornerShape(26.dp),
                containerColor = AppleGreen,
                contentColor = AppleWhite,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "New Offer",
                        tint = AppleWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "New Offer",
                        fontSize = 14.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleWhite
                    )
                }
            }

            // New Sale FAB
            FloatingActionButton(
                onClick = onOpenAddTransaction,
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .testTag("add_transaction_fab"),
                shape = RoundedCornerShape(26.dp),
                containerColor = AppleBlack,
                contentColor = AppleWhite,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = strings.newSaleBtn,
                        tint = AppleWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.newSaleBtn,
                        fontSize = 14.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerGroupTile(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) AppleBlack else AppleCardBg)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) AppleWhite else AppleBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) AppleWhite else AppleBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontFamily = AppleFontFamily,
                color = if (isSelected) AppleWhite.copy(alpha = 0.7f) else AppleSystemGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CustomerRow(customer: CustomerProfile) {
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale.US) }
    val lastSeen = remember(customer.lastVisitTimestamp) {
        dateFormat.format(Date(customer.lastVisitTimestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = customer.customerName,
                    fontSize = 15.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val badgeText = when (customer.segment) {
                    CustomerSegment.VIP -> "VIP"
                    CustomerSegment.REGULAR -> "Regular"
                    CustomerSegment.DORMANT -> "Inactive"
                    CustomerSegment.NEW_BUYER -> "New"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppleSystemGray6)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "+91 ${customer.phoneNumber}",
                    fontSize = 12.sp,
                    fontFamily = AppleFontFamily,
                    color = AppleSystemGray
                )
                Text(text = "•", fontSize = 8.sp, color = AppleSystemGray)
                Text(
                    text = if (customer.visitCount == 1) "1 visit" else "${customer.visitCount} visits",
                    fontSize = 11.sp,
                    fontFamily = AppleFontFamily,
                    color = AppleSystemGray
                )
            }

            if (customer.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = customer.categories.joinToString(" • "),
                    fontSize = 11.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = AppleSystemGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = IndianCurrencyFormatter.format(customer.totalSpend),
                fontSize = 15.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = AppleBlack
            )
            Text(
                text = lastSeen,
                fontSize = 11.sp,
                fontFamily = AppleFontFamily,
                color = AppleSystemGray
            )
        }
    }
}
