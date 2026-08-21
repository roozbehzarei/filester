// The Font() overload taking variationSettings is @ExperimentalTextApi in compose-ui 1.11.
// It is required: the stable overload passes empty settings, which would render every
// weight at the variable font's default 400.
@file:OptIn(ExperimentalTextApi::class)

package com.roozbehzarei.filester.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import com.roozbehzarei.filester.R

val bodyFontFamily =
    FontFamily(
        Font(
            R.font.roboto_serif,
            weight = FontWeight.Normal,
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
        ),
        Font(
            R.font.roboto_serif,
            weight = FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
        ),
    )

val displayFontFamily =
    FontFamily(
        Font(
            R.font.roboto_serif,
            weight = FontWeight.Normal,
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
        ),
        Font(
            R.font.roboto_serif,
            weight = FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
        ),
    )

/**
 * Persian face.
 *
 * Roboto Serif carries no Arabic-script glyphs, and Compose resolves a [FontFamily] by weight
 * and style alone — a second font added to the same family would never be consulted for the
 * codepoints the first one is missing. Persian therefore swaps the whole family rather than
 * falling back per glyph. Vazirmatn covers Latin and Turkish too, so a file name in Latin script
 * inside a Persian UI stays in one face instead of splitting across two.
 */
val persianFontFamily =
    FontFamily(
        Font(
            R.font.vazirmatn,
            weight = FontWeight.Normal,
            variationSettings = FontVariation.Settings(FontVariation.weight(400)),
        ),
        Font(
            R.font.vazirmatn,
            weight = FontWeight.Medium,
            variationSettings = FontVariation.Settings(FontVariation.weight(500)),
        ),
    )

private const val PERSIAN_LANGUAGE = "fa"

// Default Material 3 typography values
val baseline = Typography()

/**
 * App typography, resolved against the current app locale so Persian renders in
 * [persianFontFamily] rather than falling back to whatever Arabic-script face the device ships.
 */
@Composable
fun appTypography(): Typography {
    val language = Locale.current.language
    return remember(language) {
        val body = if (language == PERSIAN_LANGUAGE) persianFontFamily else bodyFontFamily
        val display = if (language == PERSIAN_LANGUAGE) persianFontFamily else displayFontFamily
        Typography(
            displayLarge = baseline.displayLarge.copy(fontFamily = display),
            displayMedium = baseline.displayMedium.copy(fontFamily = display),
            displaySmall = baseline.displaySmall.copy(fontFamily = display),
            headlineLarge = baseline.headlineLarge.copy(fontFamily = display),
            headlineMedium = baseline.headlineMedium.copy(fontFamily = display),
            headlineSmall = baseline.headlineSmall.copy(fontFamily = display),
            titleLarge = baseline.titleLarge.copy(fontFamily = display),
            titleMedium = baseline.titleMedium.copy(fontFamily = display),
            titleSmall = baseline.titleSmall.copy(fontFamily = display),
            bodyLarge = baseline.bodyLarge.copy(fontFamily = body),
            bodyMedium = baseline.bodyMedium.copy(fontFamily = body),
            bodySmall = baseline.bodySmall.copy(fontFamily = body),
            labelLarge = baseline.labelLarge.copy(fontFamily = body),
            labelMedium = baseline.labelMedium.copy(fontFamily = body),
            labelSmall = baseline.labelSmall.copy(fontFamily = body),
        )
    }
}
