package io.github.areifzyp.dot.widget

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import io.github.areifzyp.dot.MainActivity

/**
 * KON-4: aksi ketuk widget. Intent divalidasi lebih dulu; bila aplikasi tujuan
 * tidak ada (mis. ROM tanpa aplikasi jam/kalender), ketukan jatuh ke aplikasi Dot
 * supaya tidak pernah menjadi PendingIntent mati.
 */
object WidgetIntents {

    fun clock(context: Context): Intent =
        resolve(context, Intent(AlarmClock.ACTION_SHOW_ALARMS))

    fun calendar(context: Context): Intent =
        resolve(
            context,
            Intent(Intent.ACTION_VIEW)
                .setData(CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()),
        )

    /** PRD KON-4: ketuk Status HP membuka pengaturan baterai sistem. */
    fun battery(context: Context): Intent =
        resolve(context, Intent(Intent.ACTION_POWER_USAGE_SUMMARY))

    /** PRD KON-4: ketuk Widget Musik membuka pemutar yang sedang aktif. */
    fun app(context: Context, packageName: String): Intent =
        context.packageManager.getLaunchIntentForPackage(packageName)
            ?: main(context)

    private fun resolve(context: Context, intent: Intent): Intent =
        if (intent.resolveActivity(context.packageManager) != null) intent else main(context)

    private fun main(context: Context): Intent =
        Intent(context, MainActivity::class.java)
}
