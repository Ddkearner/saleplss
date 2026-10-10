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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentMode
import com.example.data.SyncStatus
import com.example.data.TransactionRecord
import com.example.ui.CustomerProfile
import com.example.ui.CustomerSegment
import com.example.ui.HistorySubTab
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    transactions: List<TransactionRecord>,
    customerProfiles: List<CustomerProfile>,
    historySubTab: HistorySubTab,
    searchQuery: String,
    isOnline: Boolean = true,
    pendingSyncCount: Int = 0,
    onTriggerSync: () -> Unit = {},
    onSubTabChange: (HistorySubTab) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    val filteredTransactions = remember(transactions, searchQuery) {
        if (searchQuery.isBlank()) transactions else {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            transactions.filter {
                it.customerName.lowercase(Locale.ROOT).contains(q) ||
                        it.phoneNumber.lowercase(Locale.ROOT).contains(q) ||
                        it.categories.lowercase(Locale.ROOT).contains(q)
            }
        }
    }

    val filteredCustomers = remember(customerProfiles, searchQuery) {
        if (searchQuery.isBlank()) customerProfiles else {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            customerProfiles.filter {
                it.customerName.lowercase(Locale.ROOT).contains(q) ||
                        it.phoneNumber.lowercase(Locale.ROOT).contains(q) ||
                        it.categories.any { cat -> cat.lowercase(Locale.ROOT).contains(q) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .testTag("history_screen_container"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item(key = "history_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "History",
                    fontSize = 28.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.6).sp,
                    color = AppleBlack
                )
            }
        }

        // Switchable 2 Tabs: Sales vs Customers
        item(key = "history_switchable_tabs") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(AppleCardBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isRecent = historySubTab == HistorySubTab.RECENT_SALES
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isRecent) AppleBlack else AppleCardBg)
                        .clickable { onSubTabChange(HistorySubTab.RECENT_SALES) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sales (${transactions.size})",
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecent) AppleWhite else AppleBlack
                    )
                }

                val isCustomers = historySubTab == HistorySubTab.ALL_CUSTOMERS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isCustomers) AppleBlack else AppleCardBg)
                        .clickable { onSubTabChange(HistorySubTab.ALL_CUSTOMERS) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Customers (${customerProfiles.size})",
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (isCustomers) AppleWhite else AppleBlack
                    )
                }
            }
        }

        // Search Bar
        item(key = "history_search") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppleCardBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("history_search_box"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AppleSystemGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search name or phone number...",
                                color = AppleSystemGray,
                                fontSize = 13.sp,
                                fontFamily = AppleFontFamily
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = AppleBlack
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(AppleBlack),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("history_search_input")
                        )
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = AppleSystemGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        when (historySubTab) {
            HistorySubTab.RECENT_SALES -> {
                item(key = "history_sales_card") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AppleCardBg)
                            .padding(18.dp)
                    ) {
                        if (filteredTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "No sales found",
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlack
                                    )
                                    Text(
                                        text = "Sales you record will appear here",
                                        fontSize = 12.sp,
                                        fontFamily = AppleFontFamily,
                                        color = AppleSystemGray
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                filteredTransactions.forEachIndexed { index, txn ->
                                    SaleRow(txn)
                                    if (index < filteredTransactions.lastIndex) {
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
            }

            HistorySubTab.ALL_CUSTOMERS -> {
                item(key = "history_customers_card") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AppleCardBg)
                            .padding(18.dp)
                    ) {
                        if (filteredCustomers.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "No customers found",
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlack
                                    )
                                    Text(
                                        text = "Customers from recorded sales will appear here",
                                        fontSize = 12.sp,
                                        fontFamily = AppleFontFamily,
                                        color = AppleSystemGray
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                filteredCustomers.forEachIndexed { index, cust ->
                                    CustomerDirectoryRow(cust)
                                    if (index < filteredCustomers.lastIndex) {
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
            }
        }

        item(key = "history_bottom_spacer") {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SaleRow(txn: TransactionRecord) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }
    val formattedDate = remember(txn.timestamp) { dateFormat.format(Date(txn.timestamp)) }

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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = txn.customerName,
                    fontSize = 15.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (txn.invoiceNumber.isNotBlank()) {
                    Text(
                        text = "#${txn.invoiceNumber}",
                        fontSize = 10.sp,
                        fontFamily = AppleFontFamily,
                        color = AppleSystemGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "+91 ${txn.phoneNumber}",
                fontSize = 12.sp,
                fontFamily = AppleFontFamily,
                color = AppleSystemGray
            )

            if (txn.categories.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = txn.categories,
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
                text = IndianCurrencyFormatter.format(txn.amount),
                fontSize = 16.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = AppleBlack
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isUpi = txn.paymentMode == PaymentMode.UPI_QR
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppleSystemGray6)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (isUpi) "UPI QR" else "CASH",
                        fontSize = 9.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack
                    )
                }
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    fontFamily = AppleFontFamily,
                    color = AppleSystemGray
                )
            }
        }
    }
}

@Composable
private fun CustomerDirectoryRow(customer: CustomerProfile) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.US) }
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
                text = "Last: $lastSeen",
                fontSize = 11.sp,
                fontFamily = AppleFontFamily,
                color = AppleSystemGray
            )
        }
    }
}
