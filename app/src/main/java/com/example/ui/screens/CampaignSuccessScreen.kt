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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.CampaignSuccessDetails
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
fun CampaignSuccessScreen(
    details: CampaignSuccessDetails,
    onDone: () -> Unit,
    onNewOffer: () -> Unit
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
    val vibrantGreen = AppleGreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGreen)
            .statusBarsPadding()
            .testTag("campaign_success_screen")
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
                text = "Offer Broadcast Launched",
                fontSize = 22.sp,
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
                text = "${details.recipientCount} Customers",
                fontSize = 38.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp,
                color = AppleWhite,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Target Group: ${details.targetAudience.uppercase()}",
                fontSize = 13.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = AppleWhite.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }

        // Bottom White Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(AppleWhite)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CAMPAIGN TITLE",
                            fontSize = 10.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = AppleSystemGray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = details.title,
                            fontSize = 16.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                    }

                    if (details.imageUri != null) {
                        AsyncImage(
                            model = details.imageUri,
                            contentDescription = "Attached photo",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AppleSystemGray5, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MESSAGE DELIVERED",
                    fontSize = 10.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = AppleSystemGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details.messageText,
                    fontSize = 14.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = AppleBlack,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNewOffer,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("success_another_offer_btn"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppleBlack
                        ),
                        border = null
                    ) {
                        Text(
                            text = "+ Another Offer",
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                    }

                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("success_offer_done_btn"),
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
