package io.github.areifzyp.dot

import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.areifzyp.dot.gallery.GalleryScreen
import io.github.areifzyp.dot.onboarding.OnboardingScreen
import io.github.areifzyp.dot.onboarding.OnboardingState
import io.github.areifzyp.dot.settings.SettingsScreen
import io.github.areifzyp.dot.style.StyleScreen
import io.github.areifzyp.dot.ui.theme.DotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DotTheme {
                DotApp()
            }
        }
    }
}

private enum class AppDest { GALLERY, STYLE, SETTINGS }

/**
 * Layar utama: Galeri Widget (PRD §6.1) + Gaya Nothing (PRD §6.8) +
 * Pengaturan Aplikasi (PRD §6.9), navigasi sederhana via enum.
 */
@Composable
fun DotApp() {
    val context = LocalContext.current
    val onboarding = remember { OnboardingState(context) }
    // Onboarding (KON-2) hanya untuk pembukaan pertama; setelah selesai/dilewati
    // flag disimpan dan layar ini tidak muncul lagi.
    var showOnboarding by remember { mutableStateOf(!onboarding.isDone) }
    var dest by remember { mutableStateOf(AppDest.GALLERY) }

    if (showOnboarding) {
        OnboardingScreen(
            onFinish = {
                onboarding.markDone()
                showOnboarding = false
            },
        )
        return
    }

    when (dest) {
        AppDest.GALLERY -> GalleryScreen(
            onStyleClick = { dest = AppDest.STYLE },
            onSettingsClick = { dest = AppDest.SETTINGS },
        )
        AppDest.STYLE -> StyleScreen(onBack = { dest = AppDest.GALLERY })
        AppDest.SETTINGS -> SettingsScreen(onBack = { dest = AppDest.GALLERY })
    }
}