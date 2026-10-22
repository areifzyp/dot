package io.github.areifzyp.dot.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.style.DotStyleSettings

private sealed interface SettingsSection {
    data object Widget : SettingsSection
    data object About : SettingsSection
    data object Help : SettingsSection
    data object Backup : SettingsSection
}

private val Sections = listOf(
    SettingsSection.Widget,
    SettingsSection.About,
    SettingsSection.Help,
    SettingsSection.Backup,
)

/**
 * Layar Pengaturan Aplikasi — PRD §6.8 SET-1..5.
 *
 * Navigasi flat (bukan nested NavHost): jumlah item kecil, tiap item membuka
 * sheet, browser intent, atau eksternal. Sesuai arsitektur navigasi
 * single-activity dengan [AppDest].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val versionName = remember { context.versionName() }
    val updateInterval = DotStyleSettings.getUpdateIntervalMinutes(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    }
                },
            )
        },
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(Sections, key = { it::class.java.name }) { section ->
                when (section) {
                    is SettingsSection.Widget -> {
                        SettingsItem(
                            title = stringResource(R.string.settings_widget_title),
                            subtitle = stringResource(R.string.settings_update_interval_summary),
                            trailing = {
                                IntervalSelector(
                                    intervalMinutes = updateInterval,
                                    onSelect = { newInterval ->
                                        DotStyleSettings.setUpdateIntervalMinutes(
                                            context,
                                            newInterval,
                                        )
                                    },
                                )
                            },
                        )
                    }

                    is SettingsSection.About -> {
                        SettingsItem(
                            title = stringResource(R.string.settings_about_title),
                            subtitle = stringResource(
                                R.string.settings_about_version,
                                versionName,
                            ),
                            trailing = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    TextButton(onClick = {
                                        openUri(
                                            context,
                                            "https://github.com/areifzyp/dot",
                                        )
                                    }) {
                                        Text(stringResource(R.string.settings_about_source))
                                    }
                                    TextButton(onClick = {
                                        openUri(
                                            context,
                                            "https://github.com/areifzyp/dot/blob/main/NOTICE",
                                        )
                                    }) {
                                        Text(stringResource(R.string.settings_about_license))
                                    }
                                }
                            },
                        )
                    }

                    is SettingsSection.Help -> {
                        SettingsItem(
                            title = stringResource(R.string.settings_help_title),
                            subtitle = stringResource(R.string.settings_help_faq),
                            trailing = {
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    TextButton(onClick = {
                                        openUri(
                                            context,
                                            "https://github.com/areifzyp/dot/issues",
                                        )
                                    }) {
                                        Text(stringResource(R.string.settings_help_issue))
                                    }
                                    TextButton(onClick = {
                                        copyAppLog(context)
                                    }) {
                                        Text(stringResource(R.string.settings_log_copy))
                                    }
                                }
                            },
                        )
                    }

                    is SettingsSection.Backup -> {
                        // Status saja, bukan sakelar: pencadangan dikendalikan sistem
                        // (Android Backup, allowBackup=true + backup_rules.xml), bukan
                        // oleh preferensi aplikasi — sakelar di sini akan bohong.
                        SettingsItem(
                            title = stringResource(R.string.settings_backup_title),
                            subtitle = stringResource(R.string.settings_backup_summary),
                            trailing = {
                                Text(stringResource(R.string.settings_backup_automatic))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle) },
        trailingContent = trailing,
    )
}

@Composable
private fun IntervalSelector(
    intervalMinutes: Int,
    onSelect: (Int) -> Unit,
) {
    val context = LocalContext.current
    val options = listOf(15, 30, 60)
    val current = options.indexOfFirst { it == intervalMinutes }.let { if (it < 0) 0 else it }
    val next = options[(current + 1) % options.size]
    // Label menampilkan nilai yang SEDANG aktif; mengetuk akan beralih ke berikutnya.
    val currentLabel = intervalLabel(context, options[current])
    val nextLabel = intervalLabel(context, next)
    AssistChip(
        onClick = {
            onSelect(next)
            Toast.makeText(
                context,
                context.getString(R.string.settings_update_interval_changed, nextLabel),
                Toast.LENGTH_SHORT,
            ).show()
        },
        label = { Text(currentLabel) },
    )
}

private fun intervalLabel(context: Context, minutes: Int): String = context.getString(
    when (minutes) {
        15 -> R.string.settings_update_interval_15
        30 -> R.string.settings_update_interval_30
        else -> R.string.settings_update_interval_60
    },
)

/** Buka tautan eksternal tanpa membuat aplikasi crash bila tidak ada browser. */
private fun openUri(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val opened = runCatching { context.startActivity(intent) }.isSuccess
    if (!opened) {
        Toast.makeText(context, R.string.settings_link_failed, Toast.LENGTH_SHORT).show()
    }
}

private fun copyAppLog(context: Context) {
    val log = buildString {
        append("Dot. v").append(context.versionName()).append("\n")
        append("Interval update: ")
            .append(DotStyleSettings.getUpdateIntervalMinutes(context))
            .append(" menit\n")
    }
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboard.setPrimaryClip(
        android.content.ClipData.newPlainText(
            context.getString(R.string.settings_log_copy),
            log,
        ),
    )
    Toast.makeText(context, R.string.settings_log_copied, Toast.LENGTH_SHORT).show()
}

private fun Context.versionName(): String =
    runCatching { packageManager.getPackageInfo(packageName, 0).versionName }
        .getOrElse { "0.0.0" }