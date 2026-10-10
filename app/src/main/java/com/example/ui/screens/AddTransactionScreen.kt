package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PaymentMode
import com.example.ui.AddTxnFormState
import com.example.ui.AppStrings
import com.example.ui.CartItem
import com.example.ui.CustomerSegment
import com.example.ui.GarmentCatalogueCategories
import com.example.ui.IndianCurrencyFormatter
import com.example.ui.LocalizationProvider
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleCardBg
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.AppleSystemBg
import com.example.ui.theme.AppleSystemGray
import com.example.ui.theme.AppleSystemGray5
import com.example.ui.theme.AppleSystemGray6
import com.example.ui.theme.AppleWhite

@Composable
fun AddTransactionScreen(
    language: String,
    formState: AddTxnFormState,
    isOnline: Boolean = true,
    onCustomerNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onAddCartItem: (String) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    onUpdateCartItemCategory: (String, String) -> Unit,
    onUpdateCartItemQuantity: (String, Int) -> Unit,
    onUpdateCartItemPrice: (String, Double) -> Unit,
    onSubmitWithMode: (PaymentMode) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val strings: AppStrings = LocalizationProvider.getStrings(language)
    val focusManager = LocalFocusManager.current
    val phoneFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            phoneFocusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    val matchedProfile = formState.matchedCustomerProfile
    val isReturningCustomer = matchedProfile != null && matchedProfile.visitCount > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleSystemBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("add_transaction_screen_container")
    ) {
        // Sticky Header: Back, Title, Live Total Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
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
                text = "New Sale",
                fontSize = 17.sp,
                fontFamily = AppleFontFamily,
                fontWeight = FontWeight.Bold,
                color = AppleBlack
            )

            // Current Total Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppleBlack)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = IndianCurrencyFormatter.format(formState.totalAmount),
                    fontSize = 13.sp,
                    fontFamily = AppleFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AppleWhite
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 4.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Customer Section with Returning Customer Recognition Banner
            item(key = "customer_card") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppleCardBg)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CUSTOMER DETAILS",
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = AppleSystemGray
                            )

                            // Returning customer VIP / Regular badge
                            if (isReturningCustomer && matchedProfile != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppleBlack)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = AppleWhite,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = if (matchedProfile.segment == CustomerSegment.VIP) "VIP BUYER" else "REPEAT BUYER",
                                            fontSize = 10.sp,
                                            fontFamily = AppleFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = AppleWhite
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large, easily-tappable phone number box
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppleSystemGray6)
                                .padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = "+91",
                                fontSize = 16.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (formState.phoneNumber.isEmpty()) {
                                    Text(
                                        text = "Customer Mobile Number",
                                        color = AppleSystemGray,
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily
                                    )
                                }
                                BasicTextField(
                                    value = formState.phoneNumber,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(10)
                                        onPhoneNumberChange(digits)
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlack
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(AppleBlack),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(phoneFocusRequester)
                                        .testTag("add_txn_phone_input")
                                )
                            }
                        }

                        // Returning Customer Instant Recognition Card
                        if (isReturningCustomer && matchedProfile != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppleSystemGray6)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Returning Customer: ${matchedProfile.customerName}",
                                            fontSize = 13.sp,
                                            fontFamily = AppleFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = AppleBlack
                                        )
                                        Text(
                                            text = "Visited ${matchedProfile.visitCount} times • Total Spent ${IndianCurrencyFormatter.format(matchedProfile.totalSpend)}",
                                            fontSize = 11.sp,
                                            fontFamily = AppleFontFamily,
                                            color = AppleSystemGray
                                        )
                                    }
                                }
                            }
                        }

                        // Customer Name field (always accessible, clearly labeled)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppleSystemGray6)
                                .padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AppleSystemGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (formState.customerName.isEmpty()) {
                                    Text(
                                        text = "Customer Name",
                                        color = AppleSystemGray,
                                        fontSize = 14.sp,
                                        fontFamily = AppleFontFamily
                                    )
                                }
                                BasicTextField(
                                    value = formState.customerName,
                                    onValueChange = onCustomerNameChange,
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        fontFamily = AppleFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppleBlack
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(AppleBlack),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("add_txn_name_input")
                                )
                            }
                        }
                    }
                }
            }

            // 2. Product Items Card (Optimized for Small Screens)
            item(key = "products_card") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppleCardBg)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BILLED ITEMS (${formState.items.size})",
                                fontSize = 11.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = AppleSystemGray
                            )

                            // Quick Add Item Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppleSystemGray6)
                                    .clickable { onAddCartItem("Saree") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("add_product_row_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Item",
                                    tint = AppleBlack,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Item",
                                    fontSize = 13.sp,
                                    fontFamily = AppleFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large, easily-tappable product cards for small screens
                        formState.items.forEachIndexed { index, cartItem ->
                            TouchFriendlyProductCard(
                                index = index,
                                item = cartItem,
                                canDelete = formState.items.size > 1,
                                onCategorySelected = { onUpdateCartItemCategory(cartItem.id, it) },
                                onQuantityChanged = { onUpdateCartItemQuantity(cartItem.id, it) },
                                onPriceChanged = { onUpdateCartItemPrice(cartItem.id, it) },
                                onDelete = { onRemoveCartItem(cartItem.id) }
                            )
                            if (index < formState.items.lastIndex) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            // Error Display
            if (!formState.error.isNullOrBlank()) {
                item(key = "error_text") {
                    Text(
                        text = formState.error,
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleBlack,
                        modifier = Modifier.testTag("add_txn_error")
                    )
                }
            }

            item(key = "bottom_space") {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Sticky Bottom Checkout Actions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppleCardBg)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Payable",
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = AppleSystemGray
                    )
                    Text(
                        text = IndianCurrencyFormatter.format(formState.totalAmount),
                        fontSize = 24.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = AppleBlack
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // UPI / QR Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onSubmitWithMode(PaymentMode.UPI_QR)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("save_sale_upi_qr_btn"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppleGreen,
                            contentColor = AppleWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "UPI QR",
                                tint = AppleWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPI QR",
                                fontSize = 15.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleWhite
                            )
                        }
                    }

                    // Cash Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onSubmitWithMode(PaymentMode.CASH)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("save_sale_cash_btn"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppleBlack,
                            contentColor = AppleWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Cash",
                                tint = AppleWhite,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cash",
                                fontSize = 15.sp,
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
}

/**
 * Touch-friendly product card designed for small screens and quick 1-thumb entry.
 * Large dropdown target, big stepper buttons, and spacious price field.
 */
@Composable
private fun TouchFriendlyProductCard(
    index: Int,
    item: CartItem,
    canDelete: Boolean,
    onCategorySelected: (String) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onPriceChanged: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var priceText by remember(item.id, item.price) {
        mutableStateOf(if (item.price > 0.0) item.price.toInt().toString() else "")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppleSystemGray6)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Category Dropdown (Full width for easy tapping on any phone) & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppleCardBg)
                            .clickable { expandedDropdown = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.category.ifBlank { "Select Category" },
                            fontSize = 14.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = AppleBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (expandedDropdown) {
                        Dialog(
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(AppleCardBg)
                                    .padding(20.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Select Category",
                                            fontSize = 17.sp,
                                            fontFamily = AppleFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = AppleBlack
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(AppleSystemGray6)
                                                .clickable { expandedDropdown = false },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = AppleSystemGray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 380.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(GarmentCatalogueCategories.size) { catIdx ->
                                            val category = GarmentCatalogueCategories[catIdx]
                                            val isSelected = item.category == category
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSelected) AppleBlack else AppleSystemGray6)
                                                    .clickable {
                                                        onCategorySelected(category)
                                                        expandedDropdown = false
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = category,
                                                    fontSize = 15.sp,
                                                    fontFamily = AppleFontFamily,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) AppleWhite else AppleBlack
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = AppleWhite,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (canDelete) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove item",
                            tint = AppleSystemGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Quantity Controls (Big 42dp touch targets) + Big Price Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Touch-friendly Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppleCardBg)
                        .padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppleSystemGray6)
                            .clickable {
                                if (item.quantity > 1) onQuantityChanged(item.quantity - 1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 18.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                    }

                    Text(
                        text = "Qty: ${item.quantity}",
                        fontSize = 13.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppleSystemGray6)
                            .clickable {
                                onQuantityChanged(item.quantity + 1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 18.sp,
                            fontFamily = AppleFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AppleBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Price Field (Large 44dp height box, easy to tap and type)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppleCardBg)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = "₹",
                        fontSize = 16.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (priceText.isEmpty()) {
                            Text(
                                text = "Amount",
                                color = AppleSystemGray,
                                fontSize = 14.sp,
                                fontFamily = AppleFontFamily
                            )
                        }
                        BasicTextField(
                            value = priceText,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() || it == '.' }.take(7)
                                priceText = clean
                                val parsed = clean.toDoubleOrNull() ?: 0.0
                                onPriceChanged(parsed)
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = AppleFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AppleBlack
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(AppleBlack),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("price_input_$index")
                        )
                    }
                }
            }

            // Subtotal Line if quantity > 1
            if (item.quantity > 1 && item.price > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${item.quantity} × ${IndianCurrencyFormatter.format(item.price)} = ${IndianCurrencyFormatter.format(item.total)}",
                        fontSize = 12.sp,
                        fontFamily = AppleFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = AppleSystemGray
                    )
                }
            }
        }
    }
}
