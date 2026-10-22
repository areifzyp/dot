package io.github.areifzyp.dot.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.gallery.DotMatrixTimePreview
import io.github.areifzyp.dot.gallery.PreviewPalettes
import kotlinx.coroutines.launch

/**
 * Onboarding pertama kali (PRD §6.9 KON-2): tiga layar singkat tentang cara
 * memasang widget, tanpa login. Tidak muncul lagi setelah selesai atau dilewati.
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_welcome),
            body = stringResource(R.string.onboarding_body_welcome),
            art = { OnboardingArtClock() },
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_pin),
            body = stringResource(R.string.onboarding_body_pin),
            art = { OnboardingArtPin() },
        ),
        OnboardingPage(
            title = stringResource(R.string.onboarding_title_tune),
            body = stringResource(R.string.onboarding_body_tune),
            art = { OnboardingArtTune() },
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_wordmark),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { index ->
            val page = pages[index]
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                page.art()
                Spacer(Modifier.height(32.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = page.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        PageDots(count = pages.size, current = pagerState.currentPage)
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onFinish) {
                Text(stringResource(R.string.onboarding_skip))
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (isLast) {
                        onFinish()
                    } else {
                        // Lanjut ke halaman berikutnya; pager mengurus animasinya.
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    stringResource(
                        if (isLast) R.string.onboarding_start else R.string.onboarding_next,
                    ),
                )
            }
        }
    }
}

private class OnboardingPage(
    val title: String,
    val body: String,
    val art: @Composable () -> Unit,
)

/** Indikator halaman bergaya Nothing: deretan titik, yang aktif lebih besar. */
@Composable
private fun PageDots(count: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val active = index == current
            Box(
                modifier = Modifier
                    .size(if (active) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    ),
            )
        }
    }
}

@Composable
private fun OnboardingArtClock() {
    DotMatrixTimePreview(
        size = io.github.areifzyp.dot.gallery.SizeVariant.LARGE,
        palette = PreviewPalettes.dark,
    )
}

@Composable
private fun OnboardingArtPin() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        listOf(
            R.string.onboarding_pin_step_1,
            R.string.onboarding_pin_step_2,
            R.string.onboarding_pin_step_3,
        ).forEachIndexed { index, textRes ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(20.dp),
                )
                Text(
                    text = stringResource(textRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OnboardingArtTune() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            R.string.onboarding_tune_theme,
            R.string.onboarding_tune_layout,
            R.string.onboarding_tune_extra,
        ).forEach { textRes ->
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}
