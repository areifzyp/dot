package io.github.areifzyp.dot.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.RemoteViews
import io.github.areifzyp.dot.style.DotStyleSettings
import io.github.areifzyp.dot.R

/**
 * Widget Teks — PRD §6.11 (TXT-1..4).
 * Menampilkan teks buatan pengguna dengan font Doto.
 * Update dipicu langsung dari TextWidgetConfigActivity (TXT-4).
 */
class TextWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, awm: AppWidgetManager, ids: IntArray) {
        ids.forEach { id -> updateWidget(context, awm, id) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { TextWidgetSettings.onWidgetDeleted(context, it) }
    }

    companion object {
        fun updateWidget(context: Context, awm: AppWidgetManager, widgetId: Int) {
            val text = TextWidgetSettings.getText(context, widgetId)
            val align = TextWidgetSettings.getAlign(context, widgetId)
            val size = TextWidgetSettings.getSize(context, widgetId)

            val views = RemoteViews(context.packageName, R.layout.widget_text)

            // TXT-2 + §8.3: ikuti tema aplikasi (bukan hanya uiMode sistem).
            views.setInt(R.id.text_widget_root, "setBackgroundColor", widgetBackgroundArgb(context))
            val fg = widgetForegroundArgb(context)
            views.setTextColor(R.id.text_widget_content, fg)
            views.setTextColor(R.id.text_widget_content_sans, fg)

            // GAY-2: pilih varian font sesuai pilihan Gaya Nothing.
            val useDoto = DotStyleSettings.getFont(context) == DotStyleSettings.FontChoice.DOTO
            val activeId = if (useDoto) R.id.text_widget_content else R.id.text_widget_content_sans
            val idleId = if (useDoto) R.id.text_widget_content_sans else R.id.text_widget_content
            views.setViewVisibility(activeId, android.view.View.VISIBLE)
            views.setViewVisibility(idleId, android.view.View.GONE)

            views.setTextViewText(activeId, text.ifEmpty { "Dot." })
            views.setInt(activeId, "setGravity", gravityFor(align))
            views.setFloat(activeId, "setTextSize", spFor(size))

            awm.updateAppWidget(widgetId, views)
        }

        fun updateAll(context: Context) {
            val awm = AppWidgetManager.getInstance(context)
            val ids = awm.getAppWidgetIds(
                android.content.ComponentName(context, TextWidgetProvider::class.java)
            )
            ids.forEach { updateWidget(context, awm, it) }
        }

        private fun gravityFor(align: TextAlign): Int = when (align) {
            TextAlign.LEFT -> Gravity.START or Gravity.CENTER_VERTICAL
            TextAlign.CENTER -> Gravity.CENTER
            TextAlign.RIGHT -> Gravity.END or Gravity.CENTER_VERTICAL
        }

        private fun spFor(size: TextSize): Float = when (size) {
            TextSize.SMALL -> 14f
            TextSize.MEDIUM -> 18f
            TextSize.LARGE -> 24f
        }
    }
}