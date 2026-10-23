package io.github.areifzyp.dot.gallery

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.areifzyp.dot.R

/**
 * Galeri Widget — PRD §6.1 (GAL-1 katalog, GAL-2 pratinjau, GAL-3 pasang, GAL-5 "Segera hadir").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBack: (() -> Unit)? = null,
    onStyleClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
) {
    var selected by remember { mutableStateOf<WidgetEntry?>(null) }
    val detail = selected
    if (detail == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.gallery_title)) },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.gallery_back))
                            }
                        }
                    },
                    actions = {
                        if (onSettingsClick != null) {
                            TextButton(onClick = onSettingsClick) {
                                Text(stringResource(R.string.settings_title))
                            }
                        }
                        if (onStyleClick != null) {
                            TextButton(onClick = onStyleClick) { Text(stringResource(R.string.gallery_style)) }
                        }
                    },
                )
            },
        ) { inner ->
            CatalogList(
                contentPadding = inner,
                onSelect = { selected = it },
            )
        }
    } else {
        WidgetDetail(
            entry = detail,
            onBack = { selected = null },
        )
    }
}

@Composable
private fun CatalogList(
    contentPadding: PaddingValues,
    onSelect: (WidgetEntry) -> Unit,
) {
    val installed = rememberInstalledWidgets(refreshKey = WidgetEntry.ALL.size)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "installed") {
            InstalledWidgetsCard(installed = installed)
        }
        items(WidgetEntry.ALL, key = { it.kind.name }) { entry ->
            CatalogRow(entry = entry, onClick = { onSelect(entry) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogRow(entry: WidgetEntry, onClick: () -> Unit) {
    val name = stringResource(entry.nameRes)
    val desc = stringResource(entry.descRes)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Ikon = cuplikan dot-matrix mini spesifik per widget
            KindPreview(
                kind = entry.kind,
                size = SizeVariant.SMALL,
                palette = PreviewPalettes.dark,
                modifier = Modifier.weight(0.35f),
            )
            Column(modifier = Modifier.weight(0.65f)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                val sizeLabels = entry.sizes.map { stringResource(it.labelRes) }
                Text(
                    text = stringResource(R.string.catalog_sizes, sizeLabels.joinToString(", ")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!entry.available) {
                AssistChip(onClick = {}, label = { Text(stringResource(R.string.catalog_soon)) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetDetail(entry: WidgetEntry, onBack: () -> Unit) {
    val context = LocalContext.current
    var size by remember(entry) { mutableStateOf(entry.sizes.first()) }
    var showManualGuide by remember(entry) { mutableStateOf(false) }
    val name = stringResource(entry.nameRes)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.gallery_back))
                    }
                },
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // GAL-2: pratinjau terang + gelap untuk ukuran terpilih
            PreviewCard(entry = entry, size = size, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                entry.sizes.forEach { variant ->
                    val label = stringResource(variant.labelRes)
                    AssistChip(
                        onClick = { size = variant },
                        label = { Text(label) },
                        leadingIcon = {
                            if (variant == size) Text("●") else Text("○")
                        },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // GAL-3: tambah ke homescreen; fallback panduan manual
            Button(
                onClick = {
                    if (!WidgetPinRequest.request(context, entry.kind)) showManualGuide = true
                },
                enabled = entry.available,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(if (entry.available) R.string.gallery_add else R.string.catalog_soon))
            }
        }
    }

    if (showManualGuide) {
        AlertDialog(
            onDismissRequest = { showManualGuide = false },
            title = { Text(stringResource(R.string.guide_title)) },
            text = { Text(stringResource(R.string.guide_body)) },
            confirmButton = {
                TextButton(onClick = { showManualGuide = false }) {
                    Text(stringResource(R.string.guide_ok))
                }
            },
        )
    }
}

