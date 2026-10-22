package io.github.areifzyp.dot.weather

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.ui.theme.DotTheme
import io.github.areifzyp.dot.widget.appWidgetIdOrNull
import io.github.areifzyp.dot.widget.toGlanceAppWidgetId
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * Konfigurasi Widget Cuaca — PRD §6.3 CUA-1 (pilih kota) + satuan suhu.
 * Data dari Open-Meteo, tanpa API key (CUA-6).
 */
class WeatherConfigActivity : ComponentActivity() {

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
                WeatherConfigScreen(
                    initialCity = WeatherSettings.getCityName(this, widgetId),
                    initialUnit = WeatherSettings.getUnit(this, widgetId),
                    onSave = { city, unit -> onSave(city, unit) },
                )
            }
        }
    }

    private fun onSave(city: OpenMeteoClient.City, unit: TempUnit) {
        WeatherSettings.setCity(this, widgetId, city.displayName, city.lat, city.lon)
        WeatherSettings.setUnit(this, widgetId, unit)
        WeatherRefreshWorker.schedule(this)
        val handler = CoroutineExceptionHandler { _, _ -> /* biarkan worker yang retry */ }
        lifecycleScopeLaunch(handler, city, unit)
    }

    private fun lifecycleScopeLaunch(
        handler: CoroutineExceptionHandler,
        city: OpenMeteoClient.City,
        unit: TempUnit,
    ) {
        lifecycleScope.launch(handler) {
            try {
                val f = OpenMeteoClient.fetchForecast(city.lat, city.lon)
                WeatherSettings.saveForecast(this@WeatherConfigActivity, widgetId, f)
            } catch (_: Exception) {
                // tanpa koneksi: widget tetap kosong sampai worker berhasil
            }
            val manager = GlanceAppWidgetManager(this@WeatherConfigActivity)
            manager.getGlanceIds(WeatherWidget::class.java)
                .filter { it.appWidgetIdOrNull() == widgetId }
                .forEach { WeatherWidget().update(this@WeatherConfigActivity, widgetId.toGlanceAppWidgetId()) }
            setResult(
                RESULT_OK,
                android.content.Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
            )
            finish()
        }
    }
}

@Composable
private fun WeatherConfigScreen(
    initialCity: String,
    initialUnit: TempUnit,
    onSave: (OpenMeteoClient.City, TempUnit) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf(initialCity) }
    var results by remember { mutableStateOf<List<OpenMeteoClient.City>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<OpenMeteoClient.City?>(null) }
    var unit by remember { mutableStateOf(initialUnit) }

    fun search(q: String) {
        if (q.isBlank()) return
        loading = true
        scope.launch {
            results = OpenMeteoClient.searchCities(q)
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        if (initialCity.isNotBlank()) search(initialCity)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.weather_config_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(R.string.weather_city_label)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { search(query) }, enabled = !loading) {
                Text(stringResource(R.string.weather_search))
            }
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else if (results.isEmpty() && !loading) {
            Text(
                text = stringResource(R.string.weather_city_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        } else {
            results.forEach { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = city }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = selected == city, onClick = { selected = city })
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = city.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.weather_unit_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TempUnit.entries.forEach { u ->
                if (u == unit) {
                    Button(onClick = { unit = u }) { Text(u.name) }
                } else {
                    OutlinedButton(onClick = { unit = u }) { Text(u.name) }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { selected?.let { onSave(it, unit) } },
            enabled = selected != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.config_save))
        }
    }
}