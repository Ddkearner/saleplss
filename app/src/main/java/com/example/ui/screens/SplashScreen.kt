package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlack
import com.example.ui.theme.AppleFontFamily
import com.example.ui.theme.AppleWhite
import kotlinx.coroutines.delay

/**
 * Pristine Apple-style splash screen.
 * Pure stark white background, only lowercase "salesplus" in refined Apple typography.
 * Zero logo, zero spinners, zero extra elements.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(900L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleWhite)
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "salesplus",
            color = AppleBlack,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = AppleFontFamily,
            letterSpacing = (-1.2).sp,
            modifier = Modifier.testTag("splash_brand_text")
        )
    }
}
