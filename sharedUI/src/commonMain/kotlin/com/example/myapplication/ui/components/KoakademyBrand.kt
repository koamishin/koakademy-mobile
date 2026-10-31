package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import myapplication.sharedui.generated.resources.Res
import myapplication.sharedui.generated.resources.app_name
import myapplication.sharedui.generated.resources.koakademy_mark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * The KoAkademy "K" mark. The asset is a black alpha mask, so it is tinted to the
 * theme's ink colour and stays legible in both light and dark mode.
 */
@Composable
fun KoakademyMark(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val description = stringResource(Res.string.app_name)
    Image(
        painter = painterResource(Res.drawable.koakademy_mark),
        contentDescription = description,
        modifier = modifier
            .size(size)
            .semantics { contentDescription = description },
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(tint),
    )
}

/** Wordmark used next to the mark on onboarding, login and the portal header. */
@Composable
fun KoakademyWordmark(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = stringResource(Res.string.app_name),
        modifier = modifier,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = color,
    )
}

/** Mark and wordmark side by side, used as the header brand lockup. */
@Composable
fun KoakademyLockup(
    modifier: Modifier = Modifier,
    markSize: Dp = 24.dp,
    wordmarkStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KoakademyMark(size = markSize)
        KoakademyWordmark(style = wordmarkStyle)
    }
}
