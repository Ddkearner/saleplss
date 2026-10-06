package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.CreateCampaignFormState
import com.example.ui.CustomerProfile
import com.example.ui.CustomerSegment
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite

data class OfferTemplate(
    val title: String,
    val defaultMessage: String
)

val QuickOfferTemplates = listOf(
    OfferTemplate(
        title = "New Stock",
        defaultMessage = "Dukaan par naya collection aa gaya hai. Aapke pasand ka shandar stock aaya hai, zaroor visit karein!"
    ),
    OfferTemplate(
        title = "Special Discount",
        defaultMessage = "Hamari dukaan par special discount offer shuru ho gaya hai. Zaroor aayiye aur offer ka labh uthaiye!"
    ),
    OfferTemplate(
        title = "Party Wear",
        defaultMessage = "Naye party-wear aur wedding collection ka naya stock khula hai. Zaroor aayiye!"
    ),
    OfferTemplate(
        title = "Custom",
        defaultMessage = ""
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateCampaignScreen(
    formState: CreateCampaignFormState,
    customerProfiles: List<CustomerProfile>,
    storeName: String,
    onTitleChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onAudienceChange: (String) -> Unit,
    onImageChange: (String?) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImageChange(uri.toString())
        }
    }

    val vipCount = remember(customerProfiles) { customerProfiles.count { it.segment == CustomerSegment.VIP } }
    val regularCount = remember(customerProfiles) { customerProfiles.count { it.segment == CustomerSegment.REGULAR } }
    val dormantCount = remember(customerProfiles) { customerProfiles.count { it.segment == CustomerSegment.DORMANT } }
    val totalCount = customerProfiles.size

    val targetCount = when (formState.targetAudience) {
        "VIP" -> vipCount
        "REGULAR" -> regularCount
        "INACTIVE" -> dormantCount
        else -> totalCount
    }

    var selectedTemplateIndex by remember { mutableStateOf(0) }

    // Auto-fill default template if empty initially
    LaunchedEffect(Unit) {
        if (formState.title.isEmpty() && formState.messageText.isEmpty()) {
            val initial = QuickOfferTemplates[0]
            onTitleChange(initial.title)
            onMessageChange(initial.defaultMessage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("create_campaign_screen_container"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            // Minimal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppleCardBg)
                        .clickable { onBack() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("campaign_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppleBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Back",
                        fontSize = 14.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleBlack
                    )
                }

                Text(
                    text = "Send Offer",
                    fontSize = 17.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlack
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppleCardBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Offer",
                        tint = AppleBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 1: 1-Tap Offer Selection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "1. CHOOSE OFFER",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickOfferTemplates.forEachIndexed { index, template ->
                            val isSelected = selectedTemplateIndex == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) AppleBlack else AppleSystemGray6)
                                    .clickable {
                                        selectedTemplateIndex = index
                                        onTitleChange(template.title)
                                        if (template.defaultMessage.isNotEmpty()) {
                                            onMessageChange(template.defaultMessage)
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = AppleWhite,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = template.title,
                                        fontSize = 13.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) AppleWhite else AppleBlack
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step 2: Target Audience Pills
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "2. SEND TO",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AudiencePill(
                            modifier = Modifier.weight(1f),
                            label = "All",
                            count = totalCount,
                            isSelected = formState.targetAudience == "ALL",
                            onClick = { onAudienceChange("ALL") }
                        )
                        AudiencePill(
                            modifier = Modifier.weight(1f),
                            label = "VIP",
                            count = vipCount,
                            isSelected = formState.targetAudience == "VIP",
                            onClick = { onAudienceChange("VIP") }
                        )
                        AudiencePill(
                            modifier = Modifier.weight(1f),
                            label = "Regular",
                            count = regularCount,
                            isSelected = formState.targetAudience == "REGULAR",
                            onClick = { onAudienceChange("REGULAR") }
                        )
                        AudiencePill(
                            modifier = Modifier.weight(1f),
                            label = "Inactive",
                            count = dormantCount,
                            isSelected = formState.targetAudience == "INACTIVE",
                            onClick = { onAudienceChange("INACTIVE") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step 3: Add Photo (Optional)
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
                            text = "3. ADD PHOTO (OPTIONAL)",
                            fontSize = 11.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = AppleSystemGray
                        )
                        if (formState.imageUri != null) {
                            Text(
                                text = "Remove",
                                fontSize = 12.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = AppleBlack,
                                modifier = Modifier.clickable { onImageChange(null) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (formState.imageUri == null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSystemGray6)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .padding(16.dp)
                                .testTag("pick_image_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppleCardBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add Photo",
                                    tint = AppleBlack,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Choose Photo from Gallery",
                                    fontSize = 14.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlack
                                )
                                Text(
                                    text = "Attach photo of saree, suit, or offer banner",
                                    fontSize = 12.sp,
                                    fontFamily = AppleFontFamily,
                                    color = AppleSystemGray
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppleSystemGray6)
                        ) {
                            AsyncImage(
                                model = formState.imageUri,
                                contentDescription = "Attached Offer Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = { onImageChange(null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(AppleBlack.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Photo",
                                    tint = AppleWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step 4: Message Content Box
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
                            text = "4. MESSAGE",
                            fontSize = 11.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = AppleSystemGray
                        )
                        Text(
                            text = "Tap to edit",
                            fontSize = 11.sp,
                            fontFamily = AppleFontFamily,
                            color = AppleSystemGray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppleSystemGray6)
                            .padding(14.dp)
                            .testTag("campaign_message_input")
                    ) {
                        if (formState.messageText.isEmpty()) {
                            Text(
                                text = "Type your offer or announcement here...",
                                color = AppleSystemGray,
                                fontSize = 13.sp,
                                fontFamily = AppleFontFamily
                            )
                        }
                        BasicTextField(
                            value = formState.messageText,
                            onValueChange = {
                                onMessageChange(it)
                                if (formState.title.isEmpty() || formState.title == "Custom") {
                                    onTitleChange(it.take(24))
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Normal,
                                color = AppleBlack,
                                lineHeight = 20.sp
                            ),
                            cursorBrush = SolidColor(AppleBlack),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Clean customer message preview with photo indicator
                    val previewName = customerProfiles.firstOrNull()?.customerName?.ifBlank { "Customer" } ?: "Sharma ji"
                    Text(
                        text = "Customer receives: \"Namaste $previewName, ${formState.messageText.ifBlank { "Dukaan par naya stock aaya hai..." }}\"" + if (formState.imageUri != null) " [📷 Photo attached]" else "",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        color = AppleSystemGray,
                        lineHeight = 16.sp
                    )
                }
            }

            if (!formState.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = formState.error,
                    fontSize = 13.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleBlack,
                    modifier = Modifier.testTag("campaign_error_text")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("run_campaign_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppleBlack,
                    contentColor = AppleWhite
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send to $targetCount Customers",
                        fontSize = 16.sp,
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
private fun AudiencePill(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) AppleBlack else AppleSystemGray6)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontFamily = AppleFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) AppleWhite else AppleBlack
            )
            Text(
                text = "$count",
                fontSize = 11.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) AppleWhite.copy(alpha = 0.8f) else AppleSystemGray
            )
        }
    }
}
