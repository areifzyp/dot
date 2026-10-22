package io.github.areifzyp.dot.music

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * MUS-1/2: membaca notifikasi media aktif dan mengekspor state untuk widget.
 *
 * Layanan ini TIDAK menyimpan riwayat lagu (kriteria penerimaan §6.6) — state
 * selalu dibaca segar dari notifikasi yang sedang terpasang. Aksi kontrol dikirim
 * dengan memicu PendingIntent bawaan notifikasi (play/pause/prev/next), sehingga
 * selalu sesuai aksi yang tersedia di pemutar tersebut.
 */
class MusicListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refreshWidgets()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refreshWidgets()
    }

    /** MUS-1: setiap perubahan notifikasi media → widget disegarkan segera. */
    private fun refreshWidgets() {
        val app = applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                MusicWidget().updateAll(app)
            } catch (_: Exception) {
                // widget belum terpasang / gagal sementara
            }
        }
    }

    private fun extractSnapshot(): PlaybackSnapshot? {
        controlTokens.clear()
        val notifications = try {
            activeNotifications ?: emptyArray()
        } catch (_: Exception) {
            return null
        }
        val candidate = notifications
            .filter { it.packageName !in IGNORED_PACKAGES }
            .lastOrNull { it.hasMediaActions() }
            ?: return null

        val extras = candidate.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val artist = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        if (title.isNullOrEmpty()) return null

        val actions = candidate.notification.actions ?: emptyArray()
        val playPause = actions.firstOrNull { it.isPlayPauseAction() }
        val previous = actions.firstOrNull { it.isPreviousAction() }
        val next = actions.firstOrNull { it.isNextAction() }

        return PlaybackSnapshot(
            packageName = candidate.packageName,
            title = title,
            artist = artist?.takeIf { it.isNotBlank() },
            isPlaying = playPause?.let { isPauseLabel(it.title?.toString()) } ?: false,
            playPauseToken = registerToken(playPause?.actionIntent),
            previousToken = registerToken(previous?.actionIntent),
            nextToken = registerToken(next?.actionIntent),
        )
    }

    companion object {
        /** Penanda "tidak ada aksi" pada [PlaybackSnapshot] (nol = token valid). */
        const val NO_TOKEN = 0

        @Volatile
        private var activeInstance: MusicListenerService? = null

        /** Token PendingIntent agar tombol widget bisa memicu aksi via ActionCallback. */
        private val controlTokens = java.util.concurrent.ConcurrentHashMap<Int, android.app.PendingIntent>()
        private val tokenSeq = java.util.concurrent.atomic.AtomicInteger(0)

        private fun registerToken(intent: android.app.PendingIntent?): Int {
            if (intent == null) return NO_TOKEN
            val token = tokenSeq.incrementAndGet()
            controlTokens[token] = intent
            return token
        }

        /** Kirim aksi media (play/pause/prev/next) yang dipegang token. */
        fun fireToken(token: Int) {
            if (token == NO_TOKEN) return
            try {
                controlTokens[token]?.send()
            } catch (_: android.app.PendingIntent.CanceledException) {
                // notifikasi sudah hilang; snapshot berikutnya menyegarkan token
            }
        }

        /** @return true bila akses notifikasi sudah diberikan di pengaturan sistem. */
        fun isAccessGranted(context: Context): Boolean {
            if (activeInstance != null) return true
            val cn = ComponentName(context, MusicListenerService::class.java)
            return try {
                val flat = android.provider.Settings.Secure.getString(
                    context.contentResolver,
                    "enabled_notification_listeners",
                ).orEmpty()
                flat.contains(cn.flattenToString()) || flat.contains(context.packageName)
            } catch (_: Exception) {
                false
            }
        }

        /**
         * Snapshot pemutaran media terbaru, atau null bila tidak ada pemutaran aktif
         * (MUS-4 → widget menampilkan keadaan kosong) ATAU izin belum diberikan.
         */
        fun currentPlayback(context: Context): PlaybackSnapshot? {
            val service = activeInstance ?: return null
            return try {
                service.extractSnapshot()
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        activeInstance = this
    }

    override fun onDestroy() {
        if (activeInstance === this) activeInstance = null
        super.onDestroy()
    }
}

/** Snapshot state pemutaran media untuk render widget. Token = 0 berarti aksi tak tersedia. */
data class PlaybackSnapshot(
    val packageName: String,
    val title: String,
    val artist: String?,
    val isPlaying: Boolean,
    val playPauseToken: Int,
    val previousToken: Int,
    val nextToken: Int,
)

private val IGNORED_PACKAGES = setOf("com.android.systemui", "android")

private fun StatusBarNotification.hasMediaActions(): Boolean {
    val actions = notification.actions ?: return false
    return actions.any { it.isPlayPauseAction() || it.isNextAction() || it.isPreviousAction() }
}

/** Aksi play/pause standar: icon play/pause atau title mengandung kata kunci. */
private fun Notification.Action.isPlayPauseAction(): Boolean =
    isPauseLabel(title?.toString()) ||
        title?.toString()?.contains("play", ignoreCase = true) == true ||
        title?.toString()?.contains("pause", ignoreCase = true) == true ||
        icon == android.R.drawable.ic_media_play ||
        icon == android.R.drawable.ic_media_pause

private fun Notification.Action.isPreviousAction(): Boolean =
    icon == android.R.drawable.ic_media_previous ||
        title?.toString()?.contains("previous", ignoreCase = true) == true

private fun Notification.Action.isNextAction(): Boolean =
    icon == android.R.drawable.ic_media_next ||
        title?.toString()?.contains("next", ignoreCase = true) == true

internal fun isPauseLabel(label: String?): Boolean =
    label?.contains("pause", ignoreCase = true) == true ||
        label?.contains("jeda", ignoreCase = true) == true