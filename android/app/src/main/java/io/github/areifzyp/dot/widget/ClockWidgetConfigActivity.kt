package io.github.areifzyp.dot.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.lifecycle.lifecycleScope
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.ui.theme.DotTheme
import kotlinx.coroutines.launch

/**
 * Layar konfigurasi Widget Jam (PRD §5 JAM-2: Format Waktu 12/24 jam).
 * Dipanggil sistem saat widget ditambahkan (android:configure di widget info).
 */
class ClockWidgetConfigActivity : ComponentActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            DotTheme {
                ConfigScreen(
                    initial = DotSettings.getTimeFormat(this, widgetId),
                    onSave = ::saveAndFinish,
                )
            }
        }
    }

    private fun saveAndFinish(format: TimeFormat) {
        DotSettings.setTimeFormat(this, widgetId, format)
        lifecycleScope.launch {
            ClockWidget().update(this@ClockWidgetConfigActivity, AppWidgetId(widgetId))
            setResult(
                RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
            )
            finish()
        }
    }
}

@androidx.compose.runtime.Composable
private fun ConfigScreen(initial: TimeFormat, onSave: (TimeFormat) -> Unit) {
    var selected by remember { mutableStateOf(initial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.config_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(16.dp))

        TimeFormat.entries.forEach { format ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == format, onClick = { selected = format })
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(labelRes(format)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onSave(selected) }) {
            Text(text = stringResource(R.string.config_save))
        }
    }
}

private fun labelRes(format: TimeFormat): Int = when (format) {
    TimeFormat.SYSTEM -> R.string.config_option_system
    TimeFormat.H12 -> R.string.config_option_12
    TimeFormat.H24 -> R.string.config_option_24
}