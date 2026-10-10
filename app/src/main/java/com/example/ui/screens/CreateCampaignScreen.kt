package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

val QuickOfferTitlePills = listOf(
    "New Stock",
    "Special Discount",
    "Festival Offer",
    "Wedding Collection",
    "Clearance Sale"
)

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

    val focusManager = LocalFocusManager.current

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

    LaunchedEffect(Unit) {
        if (formState.title.isEmpty()) {
            onTitleChange("New Stock")
        }
    }

    // Flexible, single-screen layout with zero scroll required
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("create_campaign_screen_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Sleek Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppleCardBg)
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("campaign_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppleBlack,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back",
                    fontSize = 13.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleBlack
                )
            }

            Text(
                text = "New Offer",
                fontSize = 17.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = AppleBlack
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppleBlack)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$targetCount Audience",
                    fontSize = 11.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleWhite
                )
            }
        }

        // 2. Offer Title Section: 1-Click Select Pills + Quick Custom Typing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AppleCardBg)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "OFFER TITLE",
                    fontSize = 10.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = AppleSystemGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 1-Click Suggestion Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickOfferTitlePills.forEach { titleOption ->
                        val isSelected = formState.title.equals(titleOption, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AppleBlack else AppleSystemGray6)
                                .clickable { onTitleChange(titleOption) }
                                .padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = titleOption,
                                fontSize = 12.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AppleWhite else AppleBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Direct Custom Title Input (If retailer wants their own title)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppleSystemGray6)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (formState.title.isEmpty()) {
                        Text(
                            text = "Or type custom title...",
                            color = AppleSystemGray,
                            fontSize = 13.sp,
                            fontFamily = AppleFontFamily
                        )
                    }
                    BasicTextField(
                        value = formState.title,
                        onValueChange = onTitleChange,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = AppleBlack
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(AppleBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("campaign_title_input")
                    )
                }
            }
        }

        // 3. Gemini-Style Clean Prompt Box (Expands flexibly via weight(1f))
        // No auto-write / auto-fill! User freely types their prompt/message.
        // Product photo option on the right with chip preview when attached.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(AppleCardBg)
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Attached Product Photo Pill Preview (if photo chosen)
                if (formState.imageUri != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppleSystemGray6)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        AsyncImage(
                            model = formState.imageUri,
                            contentDescription = "Attached photo preview",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Product Photo Attached",
                            fontSize = 12.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = AppleBlack
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(AppleSystemGray.copy(alpha = 0.2f))
                                .clickable { onImageChange(null) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = AppleBlack,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Clean Prompt Input Field (takes available height)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (formState.messageText.isEmpty()) {
                        Text(
                            text = "Type offer message or prompt here...",
                            color = AppleSystemGray,
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily
                        )
                    }
                    BasicTextField(
                        value = formState.messageText,
                        onValueChange = onMessageChange,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = AppleBlack,
                            lineHeight = 20.sp
                        ),
                        cursorBrush = SolidColor(AppleBlack),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Default
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("campaign_message_input")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Row inside Prompt Box: Image Option on Right (Gemini style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppleSystemGray6)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("pick_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add Product Image",
                            tint = AppleBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (formState.imageUri == null) "Add Photo" else "Change Photo",
                            fontSize = 12.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = AppleBlack
                        )
                    }
                }
            }
        }

        // 4. Audience Selection: 4 Clean Segment Tiles in One Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AppleCardBg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SEND TO",
                    fontSize = 10.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = AppleSystemGray
                )

                Spacer(modifier = Modifier.height(8.dp))

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

        // Error message if validation fails
        if (!formState.error.isNullOrBlank()) {
            Text(
                text = formState.error,
                fontSize = 12.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = AppleBlack,
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .testTag("campaign_error_text")
            )
        }

        // 5. Send / Start Campaign Action Button
        Button(
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("run_campaign_button"),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppleBlack,
                contentColor = AppleWhite
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = AppleWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Offer to $targetCount Customers",
                    fontSize = 14.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleWhite
                )
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
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AppleBlack else AppleSystemGray6)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
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
                fontWeight = FontWeight.Bold,
                color = if (isSelected) AppleWhite.copy(alpha = 0.85f) else AppleSystemGray
            )
        }
    }
}
