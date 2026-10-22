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
import androidx.compose.material3.OutlinedTextField
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
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.ui.theme.DotTheme

/**
 * Layar konfigurasi Widget Teks (PRD §6.11 TXT-1, TXT-6, TXT-7).
 * Dipanggil sistem saat widget ditambahkan (android:configure).
 */
class TextWidgetConfigActivity : ComponentActivity() {

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
                TextConfigScreen(
                    initialText = TextWidgetSettings.getText(this, widgetId),
                    initialAlign = TextWidgetSettings.getAlign(this, widgetId),
                    initialSize = TextWidgetSettings.getSize(this, widgetId),
                    onSave = ::saveAndFinish,
                )
            }
        }
    }

    private fun saveAndFinish(text: String, align: TextAlign, size: TextSize) {
        TextWidgetSettings.setText(this, widgetId, text)
        TextWidgetSettings.setAlign(this, widgetId, align)
        TextWidgetSettings.setSize(this, widgetId, size)

        val awm = AppWidgetManager.getInstance(this)
        TextWidgetProvider.updateWidget(this, awm, widgetId)

        setResult(
            RESULT_OK,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
        )
        finish()
    }
}

@androidx.compose.runtime.Composable
private fun TextConfigScreen(
    initialText: String,
    initialAlign: TextAlign,
    initialSize: TextSize,
    onSave: (String, TextAlign, TextSize) -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    var align by remember { mutableStateOf(initialAlign) }
    var size by remember { mutableStateOf(initialSize) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.text_config_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // TXT-1: editor teks, maks 100 karakter
        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= 100) text = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.text_config_hint)) },
            maxLines = 3,
            supportingText = { Text("${text.length}/100") },
        )

        // TXT-6: perataan
        Text(
            text = stringResource(R.string.text_config_align),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextAlign.entries.forEach { a ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = align == a, onClick = { align = a })
                    Text(
                        text = stringResource(alignLabel(a)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        // TXT-7: ukuran huruf
        Text(
            text = stringResource(R.string.text_config_size),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextSize.entries.forEach { s ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = size == s, onClick = { size = s })
                    Text(
                        text = stringResource(sizeLabel(s)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onSave(text, align, size) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.config_save))
        }
    }
}

private fun alignLabel(a: TextAlign): Int = when (a) {
    TextAlign.LEFT -> R.string.text_align_left
    TextAlign.CENTER -> R.string.text_align_center
    TextAlign.RIGHT -> R.string.text_align_right
}

private fun sizeLabel(s: TextSize): Int = when (s) {
    TextSize.SMALL -> R.string.size_small
    TextSize.MEDIUM -> R.string.size_medium
    TextSize.LARGE -> R.string.size_large
}