package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AddTxnFormState
import com.example.ui.AppStrings
import com.example.ui.DefaultGarmentCategories
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.LocalizationProvider
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(
    language: String,
    formState: AddTxnFormState,
    onCustomerNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onAddQuickAmount: (Double) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val strings: AppStrings = LocalizationProvider.getStrings(language)
    val quickAmounts = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0)
    val currentAmountNum = formState.amount.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("add_transaction_screen_container"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Back button, Title, Currency badge
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
                        .testTag("add_txn_back_button")
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

                Text(
                    text = strings.newSaleBtn,
                    fontSize = 17.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleBlack
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppleCardBg)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "₹ INR",
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Mobile Number & Name Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.mobileNumberLabel.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppleSystemGray6)
                            .padding(horizontal = 16.dp, vertical = 13.dp)
                            .testTag("customer_phone_input")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "+91 ",
                                fontSize = 16.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                if (formState.phoneNumber.isEmpty()) {
                                    Text(
                                        text = strings.mobileNumberHint,
                                        color = AppleSystemGray,
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily
                                    )
                                }
                                BasicTextField(
                                    value = formState.phoneNumber,
                                    onValueChange = onPhoneNumberChange,
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppleBlack
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(AppleBlack),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = strings.customerNameLabel.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppleSystemGray6)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("customer_name_input")
                    ) {
                        if (formState.customerName.isEmpty()) {
                            Text(
                                text = strings.customerNameHint,
                                color = AppleSystemGray,
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily
                            )
                        }
                        BasicTextField(
                            value = formState.customerName,
                            onValueChange = onCustomerNameChange,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Medium,
                                color = AppleBlack
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(AppleBlack),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Bill Amount Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.amountLabel.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = AppleSystemGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹ ",
                            fontSize = 32.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            if (formState.amount.isEmpty()) {
                                Text(
                                    text = "0",
                                    color = AppleSystemGray,
                                    fontSize = 32.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            BasicTextField(
                                value = formState.amount,
                                onValueChange = onAmountChange,
                                textStyle = TextStyle(
                                    fontSize = 32.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlack
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(AppleBlack),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("transaction_amount_input")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Denomination Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickAmounts.forEach { amountVal ->
                            val label = "+₹${amountVal.toInt()}"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppleSystemGray6)
                                    .clickable { onAddQuickAmount(amountVal) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("quick_amount_$label"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppleBlack
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Category Selection Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppleCardBg)
                    .padding(18.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.categoryLabel.uppercase(),
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
                        DefaultGarmentCategories.forEach { category ->
                            val isSelected = formState.selectedCategories.contains(category)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) AppleBlack else AppleSystemGray6)
                                    .clickable { onToggleCategory(category) }
                                    .padding(horizontal = 16.dp, vertical = 11.dp)
                                    .testTag("category_tile_$category"),
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
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Text(
                                        text = category,
                                        fontSize = 14.sp,
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

            if (!formState.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = formState.error,
                    fontSize = 13.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleBlack,
                    modifier = Modifier.testTag("add_txn_error")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Save Button
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
                    .testTag("record_transaction_button"),
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
                        text = strings.saveSaleBtn,
                        fontSize = 16.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleWhite
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(16.dp)
                            .background(AppleWhite.copy(alpha = 0.35f))
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = IndianCurrencyFormatter.format(currentAmountNum),
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
