package io.github.areifzyp.dot.widget

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.ui.theme.DotTheme
import kotlinx.coroutines.launch

/**
 * Konfigurasi Widget Kalender (PRD §6.4 KAL-3).
 * Izin READ_CALENDAR hanya diminta saat user memilih mode agenda
 * (kriteria penerimaan PRD §6.4).
 */
class CalendarWidgetConfigActivity : ComponentActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var pendingMode = CalendarMode.DATE_ONLY

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Apapun hasilnya, simpan mode; tanpa izin widget menampilkan keterangan.
            saveAndFinish(pendingMode)
        }

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
                CalendarConfigScreen(
                    initial = CalendarWidgetSettings.getMode(this, widgetId),
                    onSave = ::onModeSelected,
                )
            }
        }
    }

    private fun onModeSelected(mode: CalendarMode) {
        pendingMode = mode
        val needsPermission = mode == CalendarMode.DATE_AGENDA &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) !=
                PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.READ_CALENDAR)
        } else {
            saveAndFinish(mode)
        }
    }

    private fun saveAndFinish(mode: CalendarMode) {
        CalendarWidgetSettings.setMode(this, widgetId, mode)
        lifecycleScope.launch {
            CalendarWidget().update(this@CalendarWidgetConfigActivity, widgetId.toGlanceAppWidgetId())
            setResult(
                RESULT_OK,
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
            )
            finish()
        }
    }
}

@androidx.compose.runtime.Composable
private fun CalendarConfigScreen(initial: CalendarMode, onSave: (CalendarMode) -> Unit) {
    var selected by remember { mutableStateOf(initial) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.calendar_config_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))

        CalendarMode.entries.forEach { mode ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == mode, onClick = { selected = mode })
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(modeLabel(mode)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = { onSave(selected) }) {
            Text(stringResource(R.string.config_save))
        }
    }
}

private fun modeLabel(mode: CalendarMode): Int = when (mode) {
    CalendarMode.DATE_ONLY -> R.string.calendar_mode_date
    CalendarMode.DATE_AGENDA -> R.string.calendar_mode_agenda
}