package io.github.areifzyp.dot.onboarding

import android.content.Context
import android.content.SharedPreferences

/**
 * Penanda onboarding (PRD §6.9 KON-2): hanya muncul saat aplikasi pertama kali
 * dibuka, maksimal tiga layar, tanpa login.
 */
class OnboardingState(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val isDone: Boolean get() = prefs.getBoolean(KEY_DONE, false)

    fun markDone() {
        prefs.edit().putBoolean(KEY_DONE, true).apply()
    }

    /** Dipakai saat pengujian atau bila pengguna ingin melihat panduan lagi. */
    fun reset() {
        prefs.edit().remove(KEY_DONE).apply()
    }

    private companion object {
        const val PREFS = "dot_onboarding"
        const val KEY_DONE = "done"
    }
}
