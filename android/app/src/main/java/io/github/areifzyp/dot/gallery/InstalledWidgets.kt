package io.github.areifzyp.dot.gallery

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.widget.appWidgetIdOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Satu widget Dot. yang sudah terpasang di homescreen. */
data class InstalledWidget(val kind: WidgetKind, val appWidgetId: Int)

/**
 * Membaca daftar widget Dot. yang terpasang (PRD §6.1 GAL-4).
 * Tiap receiver diperiksa lewat GlanceAppWidgetManager; jumlahnya kecil (7 receiver)
 * sehingga aman dijalankan sekali per pembukaan galeri.
 */
@Composable
fun rememberInstalledWidgets(refreshKey: Any? = null): List<InstalledWidget> {
    val context = LocalContext.current
    val state by produceState(emptyList<InstalledWidget>(), context, refreshKey) {
        value = withContext(Dispatchers.IO) {
            val manager = GlanceAppWidgetManager(context)
            WidgetKind.values().flatMap { kind ->
                val widget = WidgetPinRequest.glanceWidgetFor(kind) ?: return@flatMap emptyList()
                runCatching {
                    manager.getGlanceIds(widget::class.java)
                        .mapNotNull { glanceId ->
                            glanceId.appWidgetIdOrNull()?.let { InstalledWidget(kind, it) }
                        }
                }.getOrDefault(emptyList())
            }
        }
    }
    return state
}

/**
 * GAL-4: daftar widget terpasang + jalan pintas mengubah konfigurasinya.
 * Tampil sebagai kartu ringkas di atas katalog.
 */
@Composable
fun InstalledWidgetsCard(
    installed: List<InstalledWidget>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.gallery_installed_title),
                    style = MaterialTheme.typography.titleSmall,
                )
                if (installed.isNotEmpty()) {
                    Text(
                        text = pluralStringResource(
                            R.plurals.gallery_installed_count,
                            installed.size,
                            installed.size,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (installed.isEmpty()) {
                Text(
                    text = stringResource(R.string.gallery_installed_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            } else {
                installed.forEach { item ->
                    val name = stringResource(WidgetEntry.ALL.first { it.kind == item.kind }.nameRes)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = name, style = MaterialTheme.typography.bodyMedium)
                        val configClass = WidgetPinRequest.configActivityFor(item.kind)
                        if (configClass != null) {
                            AssistChip(
                                onClick = {
                                    context.startActivity(
                                        Intent(context, configClass).apply {
                                            putExtra(
                                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                item.appWidgetId,
                                            )
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        },
                                    )
                                },
                                label = { Text(stringResource(R.string.gallery_configure)) },
                            )
                        }
                    }
                }
            }
        }
    }
}
