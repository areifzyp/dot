package io.github.areifzyp.dot.music

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.areifzyp.dot.MainActivity
import io.github.areifzyp.dot.widget.WidgetIntents
import androidx.glance.LocalContext
import io.github.areifzyp.dot.ui.theme.DotBlack
import io.github.areifzyp.dot.ui.theme.DotDarkSecondary
import io.github.areifzyp.dot.ui.theme.DotGraySecondaryLight
import io.github.areifzyp.dot.ui.theme.DotWhite
import io.github.areifzyp.dot.widget.DayNightColor

/**
 * Widget Musik — PRD §6.6 (MUS-1, MUS-2, MUS-4).
 *
 * Tiga keadaan:
 * 1. Izin akses notifikasi belum diberikan → ajakan membuka pengaturan (MUS-2 kriteria).
 * 2. Ada pemutaran aktif → judul + artis + tombol prev/play-pause/next.
 * 3. Tidak ada pemutaran → keadaan kosong ajakan membuka pemutar (MUS-4).
 */
class MusicWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val granted = MusicListenerService.isAccessGranted(context)
        val snapshot = if (granted) MusicListenerService.currentPlayback(context) else null
        provideContent {
            when {
                !granted -> PermissionContent(context)
                snapshot == null -> EmptyContent()
                else -> PlaybackContent(snapshot)
            }
        }
    }
}

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget()
}

@androidx.compose.runtime.Composable
private fun PlaybackContent(snapshot: PlaybackSnapshot) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            // PRD KON-4: buka pemutar yang sedang aktif, bukan aplikasi Dot.
            .clickable(
                actionStartActivity(
                    WidgetIntents.app(LocalContext.current, snapshot.packageName),
                ),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Judul & artis dipotong elipsis oleh Text Glance (maxLines=1) — tidak menutupi tombol
        Text(
            text = ellipsize(snapshot.title, 40),
            style = TextStyle(
                color = DayNightColor(DotBlack, DotWhite),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            ),
        )
        if (snapshot.artist != null) {
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = ellipsize(snapshot.artist, 40),
                style = TextStyle(
                    color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                    fontSize = 12.sp,
                ),
            )
        }
        Spacer(GlanceModifier.height(8.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(GlanceModifier.width(4.dp))
            ControlButton(label = "⏮", token = snapshot.previousToken)
            Spacer(GlanceModifier.width(4.dp))
            ControlButton(
                label = if (snapshot.isPlaying) "⏸" else "▶",
                token = snapshot.playPauseToken,
            )
            Spacer(GlanceModifier.width(4.dp))
            ControlButton(label = "⏭", token = snapshot.nextToken)
        }
    }
}

@androidx.compose.runtime.Composable
private fun ControlButton(label: String, token: Int) {
    val modifier = GlanceModifier
        .padding(4.dp)
        .width(36.dp)
        .height(36.dp)
    if (token != MusicListenerService.NO_TOKEN) {
        Text(
            text = label,
            modifier = modifier.clickable(
                actionRunCallback<MusicControlCallback>(
                    actionParametersOf(MusicControlCallback.TOKEN to token),
                ),
            ),
            style = TextStyle(
                color = DayNightColor(DotBlack, DotWhite),
                fontSize = 18.sp,
                textAlign = androidx.glance.text.TextAlign.Center,
            ),
        )
    } else {
        Text(
            text = "·",
            modifier = modifier,
            style = TextStyle(
                color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                fontSize = 18.sp,
                textAlign = androidx.glance.text.TextAlign.Center,
            ),
        )
    }
}

@androidx.compose.runtime.Composable
private fun EmptyContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable(actionStartActivity(Intent(LocalContext.current, MainActivity::class.java))),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Tidak ada musik diputar",
            style = TextStyle(
                color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                fontSize = 13.sp,
            ),
        )
    }
}

@androidx.compose.runtime.Composable
private fun PermissionContent(context: Context) {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable(androidx.glance.appwidget.action.actionStartActivity(intent)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Ketuk untuk izinkan akses notifikasi",
            style = TextStyle(
                color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                fontSize = 13.sp,
            ),
        )
    }
}

/** Potong teks dengan elipsis (Glance 1.1.0 Text tidak mengekspos maxLines sebagai named param). */
internal fun ellipsize(text: String, max: Int): String =
    if (text.length <= max) text else text.take(max - 1).trimEnd() + "…"

/**
 * MUS-3: tombol widget mengirim token aksi media ke [MusicListenerService],
 * yang memicu PendingIntent asli dari notifikasi pemutar.
 */
class MusicControlCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters,
    ) {
        val token = parameters[TOKEN] ?: return
        MusicListenerService.fireToken(token)
    }

    companion object {
        val TOKEN = androidx.glance.action.ActionParameters.Key<Int>("dot_music_control_token")
    }
}