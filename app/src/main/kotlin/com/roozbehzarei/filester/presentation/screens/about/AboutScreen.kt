package com.roozbehzarei.filester.presentation.screens.about

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme

private enum class AboutUrls(val link: String) {
    SOURCE_CODE("https://github.com/roozbehzarei/filester"), DONATE("https://roozbehzarei.com/donate"), PRIVACY_POLICY(
        "https://roozbehzarei.com/filester/privacy-policy"
    )
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val appNotFoundLabel = stringResource(R.string.toast_app_not_found)
    val launchUrl = remember(context) {
        { url: String ->
            val intent = Intent(
                Intent.ACTION_VIEW, url.toUri()
            )
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                Toast.makeText(
                    context, appNotFoundLabel, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    AboutContent(modifier = modifier, onSourceCodeClick = {
        launchUrl(AboutUrls.SOURCE_CODE.link)
    }, onDonateClick = {
        launchUrl(AboutUrls.DONATE.link)
    }, onPrivacyPolicyClick = {
        launchUrl(AboutUrls.PRIVACY_POLICY.link)
    })

}

@Composable
private fun AboutContent(
    modifier: Modifier,
    onSourceCodeClick: () -> Unit,
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
            text = BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            AboutActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Code,
                label = stringResource(R.string.about_button_source_code),
                onClick = onSourceCodeClick
            )
            AboutActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.AttachMoney,
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
                onSourceCodeClick = {},
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