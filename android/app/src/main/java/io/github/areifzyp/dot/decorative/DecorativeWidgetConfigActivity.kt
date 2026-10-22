package io.github.areifzyp.dot.decorative

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.areifzyp.dot.ui.theme.DotTheme

/**
 * DEK-2: pilih pola saat konfigurasi; dapat diubah ulang lewat ketukan widget.
 */
class DecorativeWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        val widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setContent {
            DotTheme {
                PatternPicker(
                    initial = if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        DecorSettings.load(this, widgetId)
                    } else {
                        DecorPattern.FULL
                    },
                    onPick = { pattern ->
                        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                            DecorSettings.save(this, widgetId, pattern)
                            val awm = AppWidgetManager.getInstance(this)
                            val intent = android.content.Intent(
                            this,
                            DecorativeWidgetReceiver::class.java,
                        ).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
                        sendBroadcast(intent)
                        }
                        finish()
                    },
                )
            }
        }
    }
}

@Composable
private fun PatternPicker(initial: DecorPattern, onPick: (DecorPattern) -> Unit) {
    var selected by remember { mutableStateOf(initial) }
    Scaffold { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(DecorPattern.selectable, key = { it.name }) { pattern ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = pattern },
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = if (pattern == selected) 4.dp else 1.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        PatternMiniGrid(pattern)
                        Column {
                            Text(pattern.displayName(), style = MaterialTheme.typography.titleSmall)
                            Text(
                                "${pattern.rows}×${pattern.cols} · ${pattern.litCount()} titik",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = { onPick(selected) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Simpan") }
            }
        }
    }
}

@Composable
private fun PatternMiniGrid(pattern: DecorPattern) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        pattern.grid().forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                row.forEach { on ->
                    Spacer(
                        Modifier
                            .size(5.dp)
                            .background(
                                if (on) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.outlineVariant,
                            ),
                    )
                }
            }
        }
    }
}

internal fun DecorPattern.displayName(): String = when (this) {
    DecorPattern.FULL -> "Titik Penuh"
    DecorPattern.HORIZONTAL_LINE -> "Garis Horizontal"
    DecorPattern.VERTICAL_LINE -> "Garis Vertikal"
    DecorPattern.DIAGONAL -> "Diagonal"
    DecorPattern.FRAME -> "Kotak (bingkai)"
    DecorPattern.CROSS -> "Silang"
    DecorPattern.RANDOM -> "Acak Deterministik"
    DecorPattern.CIRCLE -> "Lingkaran titik"
}