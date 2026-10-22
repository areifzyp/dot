package io.github.areifzyp.dot.style

import android.os.Build
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.launch
import io.github.areifzyp.dot.gallery.SizeVariant

/**
 * GAY-1: layar Gaya Nothing — tema (GAY-1b), font (GAY-2), aksen (GAY-3).
 * Setiap perubahan langsung disimpan lalu memicu update semua widget (GAY-4).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var theme by remember { mutableStateOf(DotStyleSettings.getTheme(context)) }
    var font by remember { mutableStateOf(DotStyleSettings.getFont(context)) }
    var accent by remember { mutableStateOf(DotStyleSettings.getAccent(context)) }

    fun apply(change: () -> Unit) {
        change()
        scope.launch { StyleRefresher.refreshAll(context) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gaya Nothing") })
        },
    ) { inner ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                StyleGroup(title = "Tema (GAY-1b)") {
                    DotStyleSettings.ThemeMode.entries.forEach { mode ->
                        OptionRow(
                            label = when (mode) {
                                DotStyleSettings.ThemeMode.SYSTEM -> "Ikuti Sistem"
                                DotStyleSettings.ThemeMode.LIGHT -> "Terang"
                                DotStyleSettings.ThemeMode.DARK -> "Gelap"
                            },
                            selected = theme == mode,
                            onClick = {
                                DotStyleSettings.setTheme(context, mode)
                                apply { theme = mode }
                            },
                        )
                    }
                }
            }
            item {
                StyleGroup(title = "Font (GAY-2)") {
                    OptionRow(
                        label = "Doto (default)",
                        selected = font == DotStyleSettings.FontChoice.DOTO,
                        onClick = {
                            DotStyleSettings.setFont(context, DotStyleSettings.FontChoice.DOTO)
                            apply { font = DotStyleSettings.FontChoice.DOTO }
                        },
                    )
                    OptionRow(
                        label = "Sans-serif sistem",
                        selected = font == DotStyleSettings.FontChoice.SYSTEM_SANS,
                        onClick = {
                            DotStyleSettings.setFont(context, DotStyleSettings.FontChoice.SYSTEM_SANS)
                            apply { font = DotStyleSettings.FontChoice.SYSTEM_SANS }
                        },
                    )
                }
            }
            item {
                StyleGroup(title = "Aksen (GAY-3)") {
                    DotStyleSettings.availableAccents().forEach { choice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    DotStyleSettings.setAccent(context, choice)
                                    apply { accent = choice }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AccentSwatch(choice)
                            Text(
                                text = when (choice) {
                                    DotStyleSettings.AccentChoice.NOTHING_RED -> "Merah Nothing (default)"
                                    DotStyleSettings.AccentChoice.PRESET_ORANGE -> "Oranye (preset)"
                                    DotStyleSettings.AccentChoice.SYSTEM_MATERIAL -> "Warna Sistem (Material You)"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            if (choice == accent) Text("●") else Text("○")
                        }
                    }
                    if (Build.VERSION.SDK_INT < 31) {
                        Text(
                            text = "Warna Sistem hanya tersedia di Android 12 ke atas.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleGroup(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(if (selected) "●" else "○")
    }
}

@Composable
private fun AccentSwatch(choice: DotStyleSettings.AccentChoice) {
    val color = when (choice) {
        DotStyleSettings.AccentChoice.NOTHING_RED -> io.github.areifzyp.dot.ui.theme.DotAccentRed
        DotStyleSettings.AccentChoice.PRESET_ORANGE -> io.github.areifzyp.dot.ui.theme.DotAccentOrange
        DotStyleSettings.AccentChoice.SYSTEM_MATERIAL -> MaterialTheme.colorScheme.primary
    }
    Spacer(
        Modifier
            .size(20.dp)
            .background(color, CircleShape),
    )
}