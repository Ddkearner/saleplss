package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentMode
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.PaymentSuccessDetails
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
fun PaymentSuccessScreen(
    details: PaymentSuccessDetails,
    onDone: () -> Unit,
    onNewSale: () -> Unit
) {
    BackHandler {
        onDone()
    }

    val scaleAnim = remember { Animatable(0.2f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.US) }
    val formattedDate = remember(details.timestamp) { dateFormat.format(Date(details.timestamp)) }

    val isUpi = details.paymentMode == PaymentMode.UPI_QR
    val vibrantGreen = AppleGreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGreen)
            .statusBarsPadding()
            .testTag("payment_success_screen")
    ) {
        // Top Green Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Success Circle Check
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .scale(scaleAnim.value)
                    .clip(CircleShape)
                    .background(AppleWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = vibrantGreen,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isUpi) "Payment Received via UPI QR" else "Paid in Cash",
                fontSize = 20.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = AppleWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formattedDate,
                fontSize = 12.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Medium,
                color = AppleWhite.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = IndianCurrencyFormatter.format(details.totalAmount),
                fontSize = 42.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.0).sp,
                color = AppleWhite,
                textAlign = TextAlign.Center
            )
        }

        // Bottom White Card - Extends all the way to the physical device edge with clean white navigation bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(AppleWhite)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Customer & Payment Mode Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CUSTOMER",
                            fontSize = 10.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AppleSystemGray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = details.customerName,
                            fontSize = 16.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                        Text(
                            text = "+91 ${details.phoneNumber}",
                            fontSize = 12.sp,
                            fontFamily = AppleFontFamily,
                            color = AppleSystemGray
                        )
                    }

                    // Mode Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUpi) AppleSystemBg else AppleSystemGray6)
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUpi) Icons.Default.QrCode2 else Icons.Default.Payments,
                                contentDescription = null,
                                tint = AppleBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isUpi) "UPI QR" else "CASH",
                                fontSize = 12.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = AppleSystemGray5, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Items summary
                Text(
                    text = "ITEMS BILLED",
                    fontSize = 10.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AppleSystemGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details.itemsSummary,
                    fontSize = 14.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleBlack
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AppleSystemGray5, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Transaction Reference & Offline Sync Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val refText = if (details.invoiceNumber.isNotBlank()) details.invoiceNumber else "Ref #TXN-${details.transactionId}"
                    Text(
                        text = refText,
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = AppleSystemGray
                    )
                    
                    if (details.isOfflineCreated || details.syncStatus == com.example.data.SyncStatus.PENDING) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppleSystemGray6)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "● Saved Offline (Room)",
                                fontSize = 10.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                        }
                    } else {
                        Text(
                            text = "● Cloud Synced",
                            fontSize = 11.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = vibrantGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // + New Sale Button
                    OutlinedButton(
                        onClick = onNewSale,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("success_new_sale_btn"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppleBlack
                        ),
                        border = null
                    ) {
                        Text(
                            text = "+ New Sale",
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                    }

                    // Done / Return Home Button
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("success_done_btn"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppleBlack,
                            contentColor = AppleWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                    ) {
                        Text(
                            text = "Done",
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
}
