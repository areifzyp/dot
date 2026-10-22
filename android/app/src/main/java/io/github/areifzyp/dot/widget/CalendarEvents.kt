package io.github.areifzyp.dot.widget

import android.content.Context
import android.provider.CalendarContract

/**
 * KAL-2: agenda terdekat dari CalendarContract.Instances (24 jam ke depan).
 * Hanya boleh dipanggil bila READ_CALENDAR sudah di-grant.
 */
object CalendarEvents {

    data class Event(val title: String, val startMillis: Long)

    fun upcoming(context: Context, fromMillis: Long, limit: Int = 3): List<Event> {
        val end = fromMillis + 24L * 60 * 60 * 1000
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(fromMillis.toString())
            .appendPath(end.toString())
            .build()
        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
        )
        val selection =
            "${CalendarContract.Instances.STATUS} != ${CalendarContract.Instances.STATUS_CANCELED}"
        return try {
            context.contentResolver.query(
                uri, projection, selection, null,
                CalendarContract.Instances.BEGIN + " ASC",
            )?.use { c ->
                val titleIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
                val beginIdx = c.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
                val out = mutableListOf<Event>()
                while (c.moveToNext() && out.size < limit) {
                    val title = c.getString(titleIdx) ?: ""
                    out += Event(title.ifEmpty { "(tanpa judul)" }, c.getLong(beginIdx))
                }
                out
            } ?: emptyList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }
}