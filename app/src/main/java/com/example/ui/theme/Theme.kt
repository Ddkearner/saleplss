package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppleLightColorScheme = lightColorScheme(
    primary = AppleBlack,
    onPrimary = AppleWhite,
    secondary = AppleNearBlack,
    onSecondary = AppleWhite,
    tertiary = AppleSystemGray,
    onTertiary = AppleWhite,
    background = AppleSystemBg,
    onBackground = AppleBlack,
    surface = AppleCardBg,
    onSurface = AppleBlack,
    surfaceVariant = AppleSystemGray6,
    onSurfaceVariant = AppleSystemGray,
    outline = AppleSystemGray5,
    outlineVariant = AppleSystemGray5
)

private val AppleDarkColorScheme = darkColorScheme(
    primary = AppleWhite,
    onPrimary = AppleBlack,
    secondary = AppleSystemGray5,
    onSecondary = AppleBlack,
    tertiary = AppleSystemGray,
    onTertiary = AppleBlack,
    background = AppleDarkBg,
    onBackground = AppleWhite,
    surface = AppleDarkCard,
    onSurface = AppleWhite,
    surfaceVariant = AppleDarkInput,
    onSurfaceVariant = AppleSystemGray,
    outline = AppleDarkBorder,
    outlineVariant = AppleDarkBorder
)

@Composable
fun SalesplusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AppleDarkColorScheme else AppleLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SalesplusTheme(darkTheme = darkTheme, content = content)
}
