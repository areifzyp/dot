package io.github.areifzyp.dot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.areifzyp.dot.gallery.GalleryScreen
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

private enum class AppDest { GALLERY, STYLE }

/** Layar utama: Galeri Widget (PRD §6.1) + Gaya Nothing (PRD §6.8), tab sederhana. */
@Composable
fun DotApp() {
    var dest by remember { mutableStateOf(AppDest.GALLERY) }
    when (dest) {
        AppDest.GALLERY -> GalleryScreen(onStyleClick = { dest = AppDest.STYLE })
        AppDest.STYLE -> StyleScreen(onBack = { dest = AppDest.GALLERY })
    }
}