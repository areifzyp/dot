package io.github.areifzyp.dot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import io.github.areifzyp.dot.gallery.GalleryScreen
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

/** Layar utama = Galeri Widget (PRD §6.1 GAL-1..3, GAL-5). */
@Composable
fun DotApp() {
    GalleryScreen()
}
