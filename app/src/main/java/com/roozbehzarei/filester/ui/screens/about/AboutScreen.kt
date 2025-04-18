package com.roozbehzarei.filester.ui.screens.about

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R

enum class Urls(val link: String) {
    SOURCE_CODE("https://github.com/roozbehzarei/filester"), DONATE("https://roozbehzarei.com/donate"), PRIVACY_POLICY(
        "https://roozbehzarei.com/filester/privacy-policy"
    )
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
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
            stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val isLaunched = launchUrl(context, Urls.SOURCE_CODE.link)
                    if (isLaunched.not()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_app_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Code, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.link_website),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val isLaunched = launchUrl(context, Urls.DONATE.link)
                    if (isLaunched.not()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_app_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AttachMoney, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.link_donate),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val isLaunched = launchUrl(context, Urls.PRIVACY_POLICY.link)
                    if (isLaunched.not()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_app_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.PrivacyTip, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.link_privacy_policy),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = stringResource(R.string.app_copyleft),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen()
}


/**
 * Open passed [url] in web browser
 */
fun launchUrl(context: Context, url: String): Boolean {
    val intent = Intent(
        Intent.ACTION_VIEW, url.toUri()
    )
    try {
        context.startActivity(intent)
        return true
    } catch (_: Exception) {
        return false
    }
}