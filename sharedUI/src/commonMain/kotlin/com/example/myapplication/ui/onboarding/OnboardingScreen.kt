package com.example.myapplication.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.KoakademyLockup
import com.example.myapplication.ui.portal.DocumentLabel
import com.example.myapplication.ui.portal.RubberStamp
import kotlinx.coroutines.launch
import kotlin.math.abs

/** The specimen document printed on each prospectus page. */
private enum class Specimen { ClassDay, Enrollment, Statement }

private data class OnboardingPage(
    val section: String,
    val title: String,
    val body: String,
    val specimen: Specimen,
)

/**
 * First start as a short printed prospectus: one section per page, one specimen document,
 * no further annotation. The portal itself is where the detail lives.
 */
private val pages = listOf(
    OnboardingPage(
        section = "Academics",
        title = "Your class day, kept like a timetable.",
        body = "Hours and rooms ruled to scale.",
        specimen = Specimen.ClassDay,
    ),
    OnboardingPage(
        section = "Registrar",
        title = "Enrollment without the queue.",
        body = "Requirements stamped as they clear.",
        specimen = Specimen.Enrollment,
    ),
    OnboardingPage(
        section = "Bursar & grades",
        title = "Grades and tuition, in one place.",
        body = "Posted ratings and the balance outstanding.",
        specimen = Specimen.Statement,
    ),
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        ProspectusHead(
            showSkip = !isLastPage,
            onSkip = onFinished,
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = 1,
        ) { index ->
            val offset = (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
            ProspectusPage(page = pages[index], offset = offset)
        }

        ProgressRail(current = pagerState.currentPage, count = pages.size)

        InkButton(
            label = if (isLastPage) "Enter the portal" else "Continue",
            onClick = {
                if (isLastPage) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
        )
    }
}

/** Running head: brand lockup and the skip affordance, nothing else. */
@Composable
private fun ProspectusHead(showSkip: Boolean, onSkip: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KoakademyLockup(
                markSize = 18.dp,
                wordmarkStyle = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 2.4.sp,
                ),
                modifier = Modifier.weight(1f),
            )
            if (showSkip) {
                Text(
                    text = "SKIP",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                    color = colors.secondary,
                    modifier = Modifier
                        .clickable(onClick = onSkip)
                        .padding(vertical = 6.dp),
                )
            }
        }
        HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
    }
}

@Composable
private fun ProspectusPage(page: OnboardingPage, offset: Float) {
    val colors = MaterialTheme.colorScheme
    // Centre the page on tall screens, but keep it scrollable when the viewport is short or
    // the reader has a large font scale.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val viewportHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = (1f - abs(offset) * 0.6f).coerceIn(0.2f, 1f)
                }
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = viewportHeight)
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = page.section.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                    color = colors.secondary,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onBackground,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = page.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(30.dp))
                SpecimenSheet(specimen = page.specimen)
            }
        }
    }
}

/** The specimen documents: one printed artefact per section, kept to its essentials. */
@Composable
private fun SpecimenSheet(specimen: Specimen) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topEnd = 22.dp),
        color = colors.surfaceContainerLowest,
        border = BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            when (specimen) {
                Specimen.ClassDay -> ClassDaySpecimen()
                Specimen.Enrollment -> EnrollmentSpecimen()
                Specimen.Statement -> StatementSpecimen()
            }
        }
    }
}

@Composable
private fun ClassDaySpecimen() {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        DocumentLabel(text = "Class day")
        Spacer(Modifier.weight(1f))
        Text(
            text = "MON 12 OCT",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                letterSpacing = 0.6.sp,
            ),
            color = colors.outline,
        )
    }
    Spacer(Modifier.height(12.dp))
    SheetHairline()
    ClassRow(start = "07:30", code = "CS 201", room = "Data Structures · Room 204", active = true)
    ClassRow(start = "09:15", code = "MATH 12", room = "Discrete Mathematics · Room 118", active = false)
}

@Composable
private fun ClassRow(start: String, code: String, room: String, active: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = start,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
            ),
            color = if (active) colors.secondary else colors.outline,
            modifier = Modifier
                .width(48.dp)
                .padding(vertical = 14.dp),
        )
        Box(
            Modifier
                .width(if (active) 2.dp else 1.dp)
                .fillMaxHeight()
                .background(if (active) colors.secondary else colors.outlineVariant),
        )
        Spacer(Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 14.dp),
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp),
                color = colors.onSurface,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = room,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EnrollmentSpecimen() {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        DocumentLabel(text = "Office of the registrar")
        Spacer(Modifier.weight(1f))
        RubberStamp(text = "Enrolled")
    }
    Spacer(Modifier.height(16.dp))
    Text(
        text = "First semester · A.Y. 2026–27",
        style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
        color = colors.onSurface,
    )
    Text(
        text = "BSCS · 3rd year · Section A",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
        color = colors.onSurfaceVariant,
    )
    Spacer(Modifier.height(16.dp))
    SheetHairline()
    RequirementRow(label = "Form 137", status = "Received", cleared = true)
    SheetHairline()
    RequirementRow(label = "Medical clearance", status = "For review", cleared = false)
}

@Composable
private fun RequirementRow(label: String, status: String, cleared: Boolean) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                letterSpacing = 1.1.sp,
            ),
            color = if (cleared) colors.onSurfaceVariant else colors.secondary,
        )
    }
}

@Composable
private fun StatementSpecimen() {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        DocumentLabel(text = "Statement of account")
        Spacer(Modifier.weight(1f))
        Text(
            text = "1ST SEM",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                letterSpacing = 0.6.sp,
            ),
            color = colors.outline,
        )
    }
    Spacer(Modifier.height(14.dp))
    DocumentLabel(text = "Balance due")
    Spacer(Modifier.height(2.dp))
    Text(
        text = "₱24,860.00",
        style = MaterialTheme.typography.displaySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 26.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.8).sp,
        ),
        color = colors.onSurface,
    )
    Spacer(Modifier.height(14.dp))
    SheetHairline()
    SpecimenRow(label = "Assessment", value = "₱41,200.00")
    SheetHairline()
    SpecimenRow(label = "Payments", value = "₱16,340.00")
    SheetHairline()
    Spacer(Modifier.height(14.dp))
    Box(
        Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(colors.outlineVariant),
    ) {
        Box(
            Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight()
                .background(colors.secondary),
        )
    }
}

@Composable
private fun SpecimenRow(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
            ),
            color = colors.onSurface,
        )
    }
}

/** Position, stated once: one rule per section, no labels to re-read. */
@Composable
private fun ProgressRail(current: Int, count: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (index == current) 2.dp else 1.dp)
                    .background(if (index <= current) colors.secondary else colors.outlineVariant),
            )
        }
    }
}

/** Flat ink action block; the only filled surface on the page, so it reads as the next step. */
@Composable
private fun InkButton(label: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 28.dp)
            .height(54.dp)
            .background(colors.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = colors.onPrimary,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "→",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                color = colors.onPrimary,
            )
        }
    }
}

@Composable
private fun SheetHairline() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
    )
}
