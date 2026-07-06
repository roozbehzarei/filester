package com.roozbehzarei.filester.presentation.screens.about

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme
import org.koin.compose.viewmodel.koinViewModel

private enum class AboutUrls(val link: String) {
    DONATE("https://filester.roozbehzarei.com/donate.html"), PRIVACY_POLICY(
        "https://filester.roozbehzarei.com/privacy-policy.html"
    )
}

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier, viewModel: AboutViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val isAppInDarkTheme = remember(uiState.themeMode, isSystemInDarkTheme) {
        when (uiState.themeMode) {
            Theme.Default -> isSystemInDarkTheme
            Theme.Light -> false
            Theme.Dark -> true
        }
    }
    val intent = remember(isAppInDarkTheme) {
        CustomTabsIntent.Builder().setColorScheme(
            if (isAppInDarkTheme) CustomTabsIntent.COLOR_SCHEME_DARK
            else CustomTabsIntent.COLOR_SCHEME_LIGHT
        ).build()
    }

    AboutContent(modifier = modifier, onDonateClick = {
        val uri = AboutUrls.DONATE.link.toUri()
        intent.launchUrl(
            context, uri
        )
    }, onPrivacyPolicyClick = {
        val uri = AboutUrls.PRIVACY_POLICY.link.toUri().buildUpon()
            .appendQueryParameter("standalone", "true").build()
        intent.launchUrl(context, uri)
    })

}

@Composable
private fun AboutContent(
    modifier: Modifier,
    onDonateClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {

    val screenWidth = LocalWindowInfo.current.containerSize.width.dp

    Column(
        modifier = modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.width(screenWidth * 0.2f),
            painter = painterResource(id = R.drawable.ic_filester),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
        Text(
            text = BuildConfig.VERSION_NAME, style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            AboutActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.FavoriteBorder,
                label = stringResource(R.string.about_button_donate),
                onClick = onDonateClick
            )
            AboutActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.PrivacyTip,
                label = stringResource(R.string.about_button_privacy_policy),
                onClick = onPrivacyPolicyClick
            )
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = stringResource(R.string.about_text_copyleft),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun AboutActionButton(
    modifier: Modifier = Modifier, icon: ImageVector, label: String, onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AboutContentPreview() {
    FilesterAppTheme {
        Surface {
            AboutContent(
                modifier = Modifier.fillMaxSize(),
                onDonateClick = {},
                onPrivacyPolicyClick = {})
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun AboutActionButtonPreview() {
    FilesterAppTheme {
        Surface {
            AboutActionButton(
                icon = Icons.Outlined.Code,
                label = LoremIpsum(2).values.joinToString(),
                onClick = {})
        }
    }
}