package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.ComprehensiveReport
import com.example.ui.DayActivity
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportScreen(
    report: ComprehensiveReport,
    weeklyActivity: List<DayActivity>,
    campaigns: List<CampaignRecord>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .testTag("report_screen_container"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item(key = "report_header") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Reports",
                    fontSize = 28.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.6).sp,
                    color = AppleBlack
                )
            }
        }

        // Card 1: Repeat Customers
        item(key = "report_retention_hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(AppleCardBg)
                    .padding(22.dp)
                    .testTag("report_hero_repeat_card")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "REPEAT CUSTOMERS",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AppleSystemGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val rateInt = report.repeatRatePercentage.toInt()
                    Text(
                        text = "$rateInt%",
                        fontSize = 38.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1.0).sp,
                        color = AppleBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${report.repeatCustomersCount} repeat of ${report.totalUniqueCustomers} total customers",
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        color = AppleSystemGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Minimal visual progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppleSystemGray6)
                    ) {
                        val fraction = if (report.totalUniqueCustomers > 0) {
                            (report.repeatRatePercentage / 100.0).toFloat().coerceIn(0f, 1f)
                        } else 0f

                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AppleBlack)
                            )
                        }
                    }
                }
            }
        }

        // Card 2: 4 Clean Metrics: Today, This Week, Total Sales, Average Sale
        item(key = "report_sales_matrix") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SimpleMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "TODAY",
                        value = IndianCurrencyFormatter.format(report.todaySales),
                        subLabel = "${report.todayCount} sales",
                        testTag = "report_today_sales"
                    )
                    SimpleMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "THIS WEEK",
                        value = IndianCurrencyFormatter.format(report.weekSales),
                        subLabel = "${report.weekCount} sales",
                        testTag = "report_week_sales"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SimpleMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "TOTAL SALES",
                        value = IndianCurrencyFormatter.format(report.totalSales),
                        subLabel = "${report.totalCount} sales",
                        testTag = "report_total_sales"
                    )
                    SimpleMetricCard(
                        modifier = Modifier.weight(1f),
                        label = "AVERAGE SALE",
                        value = IndianCurrencyFormatter.format(report.averageTicket),
                        subLabel = "Per bill",
                        testTag = "report_avg_ticket"
                    )
                }
            }
        }

        // Card 3: Customers Breakdown
        item(key = "report_rfm_breakdown") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CUSTOMERS",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SimpleCounterColumn("VIP", "${report.vipCustomersCount}", "₹5,000+")
                        SimpleCounterColumn("Regular", "${report.regularCustomersCount}", "2+ visits")
                        SimpleCounterColumn("Inactive", "${report.dormantCustomersCount}", "45+ days")
                    }
                }
            }
        }

        // Card 4: Category Breakdown
        item(key = "report_category_breakdown") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CATEGORIES",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val totalCategorySales = report.categoryBreakdowns.sumOf { it.totalAmount }
                    val safeTotal = if (totalCategorySales <= 0.0) 1.0 else totalCategorySales

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        report.categoryBreakdowns.forEach { breakdown ->
                            val pct = if (totalCategorySales > 0.0) {
                                ((breakdown.totalAmount / safeTotal) * 100.0).coerceIn(0.0, 100.0)
                            } else 0.0

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = breakdown.categoryName,
                                        fontSize = 14.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppleBlack
                                    )
                                    Text(
                                        text = "${IndianCurrencyFormatter.format(breakdown.totalAmount)} (${breakdown.count})",
                                        fontSize = 13.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlack
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(AppleSystemGray6)
                                    ) {
                                    if (pct > 0.0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((pct / 100.0).toFloat())
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(AppleBlack)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Card 5: Last 7 Days Activity Chart
        item(key = "report_weekly_activity") {
            val maxAmount = weeklyActivity.maxOfOrNull { it.amount } ?: 0.0
            val safeMax = if (maxAmount <= 0.0) 1.0 else maxAmount
            val totalWeek = weeklyActivity.sumOf { it.amount }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LAST 7 DAYS",
                            fontSize = 10.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = AppleSystemGray
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppleSystemGray6)
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${IndianCurrencyFormatter.format(totalWeek)} week",
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(86.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyActivity.forEach { day ->
                            val heightDp = if (maxAmount <= 0.0 || day.amount <= 0.0) {
                                3.dp
                            } else {
                                val fraction = (day.amount / safeMax).toFloat().coerceIn(0.05f, 1f)
                                (66 * fraction).dp
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .height(heightDp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(if (day.isToday) AppleBlack else AppleSystemGray5)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = day.dayLabel.take(1),
                                    fontSize = 11.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (day.isToday) AppleBlack else AppleSystemGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 6: Message Campaigns Run
        item(key = "report_campaigns_card") {
            val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(20.dp)
                    .testTag("report_campaigns_container")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CAMPAIGNS RUN",
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
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${campaigns.size} total",
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (campaigns.isEmpty()) {
                        Text(
                            text = "No campaigns run yet. Tap 'New Offer' on Home to send offers to customers.",
                            fontSize = 13.sp,
                            fontFamily = AppleFontFamily,
                            color = AppleSystemGray
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            campaigns.forEach { campaign ->
                                val dateStr = dateFormat.format(Date(campaign.timestamp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AppleSystemGray6)
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = campaign.title,
                                                fontSize = 14.sp,
                                                fontFamily = AppleFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = AppleBlack,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(AppleBlack)
                                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "${campaign.targetAudience} (${campaign.recipientCount})",
                                                    fontSize = 10.sp,
                                                    fontFamily = AppleFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppleWhite
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = campaign.messageText,
                                            fontSize = 12.sp,
                                            fontFamily = AppleFontFamily,
                                            color = AppleSystemGray,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (campaign.imageUri != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            AsyncImage(
                                                model = campaign.imageUri,
                                                contentDescription = "Campaign Photo",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(130.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = dateStr,
                                            fontSize = 10.sp,
                                            fontFamily = AppleFontFamily,
                                            color = AppleSystemGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "report_bottom_spacer") {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SimpleMetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subLabel: String,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(AppleCardBg)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = AppleSystemGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp,
                color = AppleBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subLabel,
                fontSize = 11.sp,
                fontFamily = AppleFontFamily,
                color = AppleSystemGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SimpleCounterColumn(title: String, count: String, note: String) {
    Column {
        Text(
            text = count,
            fontSize = 24.sp,
            fontFamily = AppleFontFamily,
            fontWeight = FontWeight.Bold,
            color = AppleBlack
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontFamily = AppleFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = AppleBlack
        )
        Text(
            text = note,
            fontSize = 11.sp,
            fontFamily = AppleFontFamily,
            color = AppleSystemGray
        )
    }
}
