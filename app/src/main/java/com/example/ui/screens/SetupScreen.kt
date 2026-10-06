package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppStrings
import com.example.ui.LocalizationProvider
import com.example.ui.SetupFormState
import com.example.ui.SupportedLanguages
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleNearBlack
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray3
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite

@Composable
fun SetupScreen(
    state: SetupFormState,
    onLanguageChange: (String) -> Unit,
    onStoreNameChange: (String) -> Unit,
    onStoreLocationChange: (String) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit
) {
    val strings: AppStrings = LocalizationProvider.getStrings(state.language)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("setup_screen_container"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Navigation Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.step > 1) {
                    // Clearly named Back button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppleCardBg)
                            .clickable { onPrevStep() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("setup_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backBtn,
                            tint = AppleBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.backBtn,
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = AppleBlack
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(42.dp))
                }

                // Apple Step Indicator Capsules
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("setup_step_indicator")
                ) {
                    for (i in 1..3) {
                        val isDone = i <= state.step
                        Box(
                            modifier = Modifier
                                .width(if (i == state.step) 24.dp else 8.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isDone) AppleBlack else AppleSystemGray3)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppleCardBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "0${state.step} / 03",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleSystemGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content
            AnimatedContent(
                targetState = state.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "setup_step_animated"
            ) { step ->
                when (step) {
                    1 -> {
                        // Language Selection Step
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strings.selectLanguage,
                                fontSize = 28.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.6).sp,
                                color = AppleBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.selectLanguageSubtitle,
                                fontSize = 13.sp,
                                fontFamily = AppleFontFamily,
                                color = AppleSystemGray
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            // Apple Inset Grouped Language Table
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AppleCardBg)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    SupportedLanguages.forEachIndexed { index, lang ->
                                        val isSelected = state.language.equals(lang.code, ignoreCase = true)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onLanguageChange(lang.code) }
                                                .padding(horizontal = 20.dp, vertical = 15.dp)
                                                .testTag("lang_option_${lang.code}"),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = lang.nativeName,
                                                    fontSize = 16.sp,
                                                    fontFamily = AppleFontFamily,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = AppleBlack
                                                )
                                                if (lang.nativeName != lang.nameInEnglish) {
                                                    Text(
                                                        text = "(${lang.nameInEnglish})",
                                                        fontSize = 12.sp,
                                                        fontFamily = AppleFontFamily,
                                                        color = AppleSystemGray
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = AppleBlack,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        if (index < SupportedLanguages.lastIndex) {
                                            HorizontalDivider(
                                                color = AppleSystemGray5,
                                                thickness = 0.8.dp,
                                                modifier = Modifier.padding(start = 20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Store Name Step
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strings.storeNameTitle,
                                fontSize = 28.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.6).sp,
                                color = AppleBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.storeNameSubtitle,
                                fontSize = 13.sp,
                                fontFamily = AppleFontFamily,
                                color = AppleSystemGray
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AppleCardBg)
                                    .padding(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = strings.storeNameTitle.uppercase(),
                                        fontSize = 11.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = AppleSystemGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    AppleTextInput(
                                        value = state.storeName,
                                        onValueChange = onStoreNameChange,
                                        placeholder = strings.storeNameHint,
                                        testTag = "store_name_input",
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        onAction = onNextStep
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Store Location Step
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = strings.storeLocationTitle,
                                fontSize = 28.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.6).sp,
                                color = AppleBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.storeLocationSubtitle,
                                fontSize = 13.sp,
                                fontFamily = AppleFontFamily,
                                color = AppleSystemGray
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AppleCardBg)
                                    .padding(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = strings.storeLocationTitle.uppercase(),
                                        fontSize = 11.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = AppleSystemGray
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    AppleTextInput(
                                        value = state.storeLocation,
                                        onValueChange = onStoreLocationChange,
                                        placeholder = strings.storeLocationHint,
                                        testTag = "store_location_input",
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Done
                                        ),
                                        onAction = onNextStep
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        listOf("Mumbai", "Delhi", "Bengaluru", "Kolkata").forEach { city ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(AppleSystemGray6)
                                                    .clickable { onStoreLocationChange(city) }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = city,
                                                    fontSize = 12.sp,
                                                    fontFamily = AppleFontFamily,
                                                    fontWeight = FontWeight.Medium,
                                                    color = AppleBlack
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fixed currency reminder pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AppleCardBg)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "₹",
                                        fontSize = 16.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlack
                                    )
                                    Text(
                                        text = "Default Currency: Indian Rupee (₹)",
                                        fontSize = 13.sp,
                                        fontFamily = AppleFontFamily,
                                        color = AppleSystemGray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error Message
            if (!state.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error,
                    color = AppleBlack,
                    fontSize = 13.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.testTag("setup_error_text")
                )
            }
        }

        // Bottom Apple Pill Button (Explicitly named and crystal clear pure white text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            val buttonLabel = if (state.step == 3) strings.doneBtn else strings.nextBtn
            Button(
                onClick = onNextStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("setup_continue_button"),
                shape = RoundedCornerShape(29.dp),
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
                        text = buttonLabel,
                        fontSize = 17.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleWhite,
                        letterSpacing = (-0.2).sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = buttonLabel,
                        tint = AppleWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppleTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    testTag: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onAction: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppleSystemGray6)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = AppleSystemGray,
                fontSize = 15.sp,
                fontFamily = AppleFontFamily
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Medium,
                color = AppleBlack
            ),
            singleLine = true,
            cursorBrush = SolidColor(AppleBlack),
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(onAny = { onAction() }),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
