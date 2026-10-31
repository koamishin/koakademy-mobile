package com.example.myapplication.ui.portal

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * The KoAkademy brand layer. Behind the paper palette sits a newspaper idea: a running
 * head, a split rule under every masthead, numbered index tabs instead of a stock
 * navigation bar, and documents that behave like printed artefacts (a timetable ruled to
 * scale, a transcript plotted on one shared axis, a perforated receipt).
 */

/** The brand's split rule: a terracotta stub followed by an ink rule. */
@Composable
fun SplitRule(modifier: Modifier = Modifier, height: Dp = 2.dp) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = modifier.fillMaxWidth().height(height)) {
        Box(Modifier.width(38.dp).fillMaxHeight().background(colors.secondary))
        Box(Modifier.weight(1f).fillMaxHeight().background(colors.primary))
    }
}

/**
 * Page masthead: folio line, serif headline, supporting line and the brand split rule.
 * Replaces the generic page title so every section opens like a printed page.
 */
@Composable
fun PageMasthead(
    folio: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    meta: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.5.dp)
                    .background(colors.secondary),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = folio.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                color = colors.secondary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (meta != null) {
                Text(
                    text = meta.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.1.sp,
                    ),
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onBackground,
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) {
                Spacer(Modifier.width(14.dp))
                trailing()
            }
        }
        if (subtitle != null) {
            Spacer(Modifier.height(7.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(16.dp))
        SplitRule()
    }
}

/**
 * Bottom navigation as numbered index tabs. The active destination is set in ink with a
 * terracotta edge, so the bar reads as a set of filing tabs rather than floating pills.
 */
@Composable
fun LedgerTabs(
    tabs: List<PortalTab>,
    selected: PortalTab,
    onSelect: (PortalTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(colors.outline))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .selectableGroup(),
        ) {
            tabs.forEachIndexed { index, tab ->
                val active = tab == selected
                val contentColor = if (active) colors.onPrimary else colors.onSurfaceVariant
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = active,
                            role = Role.Tab,
                            onClick = { onSelect(tab) },
                        )
                        .background(if (active) colors.primary else Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (active) colors.secondary else Color.Transparent),
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = (index + 1).toString().padStart(2, '0'),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            letterSpacing = 1.2.sp,
                        ),
                        color = if (active) {
                            colors.onPrimary.copy(alpha = 0.55f)
                        } else {
                            colors.outline
                        },
                    )
                    Spacer(Modifier.height(2.dp))
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            letterSpacing = 0.2.sp,
                        ),
                        color = contentColor,
                    )
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

/**
 * Week selector as filing tabs on the edge of the sheet. The chosen day rises out of the
 * baseline, takes the paper fill and a terracotta top edge, so the week reads as a set of
 * dividers in a binder rather than a row of buttons.
 */
@Composable
fun DayTabs(
    days: List<DayCell>,
    modifier: Modifier = Modifier,
    onSelect: ((Int) -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val fullHeight = 66.dp

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.outlineVariant),
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(fullHeight),
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEachIndexed { index, day ->
                val tabHeight by animateDpAsState(
                    targetValue = if (day.selected) fullHeight else fullHeight - 9.dp,
                    label = "day-tab-height",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(tabHeight)
                        .then(
                            if (onSelect != null) {
                                Modifier.clickable { onSelect(index) }
                            } else {
                                Modifier
                            },
                        )
                        .background(if (day.selected) colors.surface else Color.Transparent)
                        .drawBehind {
                            if (day.selected) {
                                val hairline = 1.dp.toPx()
                                drawLine(
                                    color = colors.outlineVariant,
                                    start = Offset(hairline / 2f, 0f),
                                    end = Offset(hairline / 2f, size.height),
                                    strokeWidth = hairline,
                                )
                                drawLine(
                                    color = colors.outlineVariant,
                                    start = Offset(size.width - hairline / 2f, 0f),
                                    end = Offset(size.width - hairline / 2f, size.height),
                                    strokeWidth = hairline,
                                )
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (day.selected) 2.dp else 0.dp)
                            .background(if (day.selected) colors.secondary else Color.Transparent),
                    )
                    Spacer(Modifier.height(if (day.selected) 9.dp else 6.dp))
                    Text(
                        text = day.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            letterSpacing = 1.2.sp,
                        ),
                        color = if (day.selected) colors.secondary else colors.outline,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = day.date,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                        ),
                        color = if (day.selected) {
                            colors.onSurface
                        } else {
                            colors.onSurfaceVariant.copy(alpha = 0.65f)
                        },
                    )
                }
            }
        }
    }
}

/**
 * Tear line across a receipt: two punched half-holes at the edges and a dashed rule between
 * them, so the paper reads as perforated instead of merely divided.
 */
@Composable
fun PerforationLine(
    modifier: Modifier = Modifier,
    cutColor: Color = MaterialTheme.colorScheme.background,
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier.fillMaxWidth().height(14.dp)) {
        val radius = 5.5.dp.toPx()
        val midY = size.height / 2f
        drawCircle(color = cutColor, radius = radius, center = Offset(0f, midY))
        drawCircle(color = cutColor, radius = radius, center = Offset(size.width, midY))
        drawLine(
            color = colors.outlineVariant,
            start = Offset(radius + 3.dp.toPx(), midY),
            end = Offset(size.width - radius - 3.dp.toPx(), midY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(3.dp.toPx(), 3.dp.toPx()),
            ),
        )
    }
}

data class TimetableEntry(
    val startMinutes: Int,
    val endMinutes: Int,
    val start: String,
    val end: String,
    val code: String,
    val title: String,
    val meta: String,
    val highlight: Boolean = false,
)

/**
 * A real timetable: one continuous time axis, each class block placed by its start time and
 * sized in proportion to its length. Reads like a printed schedule instead of a list.
 */
@Composable
fun DayTimetable(
    entries: List<TimetableEntry>,
    modifier: Modifier = Modifier,
    hourHeight: Dp = 68.dp,
    emptyNote: String = "No classes scheduled.",
    compact: Boolean = false,
    freeLabel: String? = null,
) {
    val colors = MaterialTheme.colorScheme

    if (entries.isEmpty()) {
        Column(modifier = modifier.fillMaxWidth()) {
            HorizontalDivider(color = colors.outlineVariant)
            Text(
                text = emptyNote,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 18.dp),
            )
            HorizontalDivider(color = colors.outlineVariant)
        }
        return
    }

    val startHour = entries.minOf { it.startMinutes } / 60
    val endHour = ceil(entries.maxOf { it.endMinutes } / 60.0).toInt().coerceAtLeast(startHour + 1)
    val hours = endHour - startHour
    val gutter = 44.dp
    val totalHeight = hourHeight * hours

    Box(modifier = modifier.fillMaxWidth().height(totalHeight)) {
        Canvas(
            modifier = Modifier.fillMaxSize().padding(start = gutter),
        ) {
            val gridColor = colors.outlineVariant.copy(alpha = 0.85f)
            repeat(hours + 1) { index ->
                val y = (hourHeight * index).toPx()
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            drawLine(
                color = colors.outline,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = 1.dp.toPx(),
            )
        }

        // The longest empty stretch is labelled in the margin, so free time reads as
        // deliberate rather than as a hole in the page.
        if (freeLabel != null) {
            val sorted = entries.sortedBy { it.startMinutes }
            var cursor = startHour * 60
            var bestStart = cursor
            var bestLength = 0
            sorted.forEach { entry ->
                if (entry.startMinutes - cursor > bestLength) {
                    bestLength = entry.startMinutes - cursor
                    bestStart = cursor
                }
                cursor = maxOf(cursor, entry.endMinutes)
            }
            if (endHour * 60 - cursor > bestLength) {
                bestLength = endHour * 60 - cursor
                bestStart = cursor
            }
            if (bestLength >= 60) {
                Text(
                    text = freeLabel,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                    ),
                    color = colors.onSurfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier
                        .offset(
                            x = gutter + 12.dp,
                            y = hourHeight * ((bestStart - startHour * 60) / 60f) + 9.dp,
                        )
                        .padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        repeat(hours + 1) { index ->
            val hour = startHour + index
            Text(
                text = clockLabel(hour),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 0.6.sp,
                ),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .offset(y = hourHeight * index + 3.dp)
                    .width(gutter - 8.dp),
            )
        }

        entries.forEach { entry ->
            val top = hourHeight * ((entry.startMinutes - startHour * 60) / 60f)
            val blockHeight = (hourHeight * ((entry.endMinutes - entry.startMinutes) / 60f) - 6.dp)
                .coerceAtLeast(if (compact) 48.dp else 56.dp)
            TimetableBlock(
                entry = entry,
                compact = compact,
                // Short classes lose the detail line instead of being clipped mid-glyph.
                dense = blockHeight < 78.dp,
                modifier = Modifier
                    .padding(start = gutter + 8.dp, end = 0.dp)
                    .offset(y = top + 3.dp)
                    .fillMaxWidth()
                    .height(blockHeight),
            )
        }
    }
}

@Composable
private fun TimetableBlock(
    entry: TimetableEntry,
    compact: Boolean,
    dense: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = CutCornerShape(topEnd = 16.dp)

    if (entry.highlight) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = colors.primary,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 13.dp,
                        vertical = if (dense) 8.dp else 10.dp,
                    ),
                verticalArrangement = if (dense) Arrangement.Center else Arrangement.Top,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.code.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = colors.onPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${entry.start} – ${entry.end}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            letterSpacing = 0.6.sp,
                        ),
                        color = colors.onPrimary.copy(alpha = 0.6f),
                    )
                }
                Spacer(Modifier.height(if (dense) 3.dp else 5.dp))
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onPrimary,
                    maxLines = if (compact || dense) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!dense) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = entry.meta,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.onPrimary.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = colors.surface,
            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.9f)),
        ) {
            Row(modifier = Modifier.fillMaxSize().height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(colors.secondary.copy(alpha = 0.55f)),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 12.dp,
                            vertical = if (dense) 7.dp else 9.dp,
                        ),
                    verticalArrangement = if (dense) Arrangement.Center else Arrangement.Top,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.code.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = colors.secondary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${entry.start} – ${entry.end}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                letterSpacing = 0.6.sp,
                            ),
                            color = colors.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(if (dense) 2.dp else 4.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        maxLines = if (compact || dense) 1 else 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!dense) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = entry.meta,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

private fun clockLabel(hour: Int): String = when {
    hour == 0 -> "12 AM"
    hour < 12 -> "$hour AM"
    hour == 12 -> "12 PM"
    else -> "${hour - 12} PM"
}

data class RatingRow(
    val code: String,
    val midterm: Double,
    val final: Double,
)

/**
 * Every course plotted on one shared 1.00–5.00 axis, midterm joined to final. Comparing
 * sections at a glance is the point: this is a transcript, not a stack of grade cards.
 */
@Composable
fun RatingComparison(
    rows: List<RatingRow>,
    average: Double,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 62.dp,
    valueWidth: Dp = 34.dp,
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Spacer(Modifier.width(labelWidth))
            Row(modifier = Modifier.weight(1f)) {
                listOf("1.00", "2.00", "3.00", "4.00", "5.00").forEach { label ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                letterSpacing = 0.3.sp,
                            ),
                            color = colors.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(4.dp)
                                .background(colors.outlineVariant),
                        )
                    }
                }
            }
            Spacer(Modifier.width(valueWidth))
        }
        Spacer(Modifier.height(2.dp))
        HorizontalDivider(color = colors.outlineVariant)

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = row.code,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                    ),
                    color = colors.onSurface,
                    modifier = Modifier.width(labelWidth),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Canvas(modifier = Modifier.weight(1f).height(40.dp)) {
                    val w = size.width
                    val h = size.height
                    fun xOf(rating: Double): Float =
                        (w * (0.2 * ((rating - 1.0).coerceIn(0.0, 4.0)) + 0.1)).toFloat()

                    repeat(5) { index ->
                        val x = w * (0.2f * index + 0.1f)
                        drawLine(
                            color = colors.outlineVariant.copy(alpha = 0.45f),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f,
                        )
                    }
                    if (average in 1.0..5.0) {
                        drawLine(
                            color = colors.secondary.copy(alpha = 0.35f),
                            start = Offset(xOf(average), 0f),
                            end = Offset(xOf(average), h),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(2.dp.toPx(), 3.dp.toPx()),
                            ),
                        )
                    }
                    val midX = xOf(row.midterm)
                    val finalX = xOf(row.final)
                    drawLine(
                        color = colors.secondary.copy(alpha = 0.55f),
                        start = Offset(midX, h / 2f),
                        end = Offset(finalX, h / 2f),
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawCircle(
                        color = colors.outline,
                        radius = 3.dp.toPx(),
                        center = Offset(midX, h / 2f),
                        style = Stroke(width = 1.2.dp.toPx()),
                    )
                    drawCircle(
                        color = colors.secondary,
                        radius = 3.4.dp.toPx(),
                        center = Offset(finalX, h / 2f),
                    )
                }
                Text(
                    text = formatDecimal(row.final),
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                    color = colors.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(valueWidth),
                )
            }
            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.55f))
        }

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(10.dp)) {
                drawCircle(
                    color = colors.outline,
                    radius = 3.dp.toPx(),
                    center = Offset(size.width / 2f, size.height / 2f),
                    style = Stroke(width = 1.2.dp.toPx()),
                )
            }
            Spacer(Modifier.width(5.dp))
            Text(
                text = "MIDTERM",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(6.dp).clip(CircleShape).background(colors.secondary),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "FINAL",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, letterSpacing = 0.8.sp),
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "dashed = average",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/** A shape whose bottom edge is torn into paper teeth. */
private class ReceiptEdgeShape(
    private val toothWidth: Dp,
    private val toothDepth: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val tooth = with(density) { toothWidth.toPx() }
        val depth = with(density) { toothDepth.toPx() }
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - depth)
            var x = size.width
            while (x > 0f) {
                val next = (x - tooth).coerceAtLeast(0f)
                lineTo((x + next) / 2f, size.height)
                lineTo(next, size.height - depth)
                x = next
            }
            close()
        }
        return Outline.Generic(path)
    }
}

/** Printed receipt with a torn bottom edge, a barcode and a stamp. */
@Composable
fun ReceiptPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val shape = ReceiptEdgeShape(toothWidth = 10.dp, toothDepth = 5.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = shape, clip = false),
        shape = shape,
        color = colors.surfaceContainerLowest,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

/** Dotted leader inside a receipt. */
@Composable
fun ReceiptRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
            ),
            color = colors.onSurfaceVariant,
            maxLines = 1,
        )
        Canvas(modifier = Modifier.weight(1f).padding(horizontal = 6.dp).height(10.dp)) {
            drawLine(
                color = colors.outlineVariant,
                start = Offset(0f, size.height - 1.dp.toPx()),
                end = Offset(size.width, size.height - 1.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(1.dp.toPx(), 3.dp.toPx()),
                ),
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = if (emphasize) 13.sp else 11.5.sp,
            ),
            color = valueColor,
        )
    }
}

/**
 * A dense scanning strip. The pattern is fixed so it reads as one identifier across the
 * receipt and the identity card, with the same quiet zones a real barcode needs.
 */
private val barcodePattern: List<Int> = List(94) { index ->
    when ((index * 37 + 11 + (index / 7) * 3) % 9) {
        0, 1, 2 -> 1
        3, 4, 5 -> 2
        6, 7 -> 3
        else -> 4
    }
}

@Composable
fun BarcodeStrip(modifier: Modifier = Modifier, height: Dp = 30.dp) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val quietZone = 6.dp.toPx()
        val usable = size.width - quietZone * 2
        val unit = usable / barcodePattern.sum()
        var x = quietZone
        barcodePattern.forEachIndexed { index, weight ->
            val barWidth = unit * weight
            if (index % 2 == 0) {
                drawRect(
                    color = colors.onSurface.copy(alpha = 0.82f),
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, size.height),
                )
            }
            x += barWidth
        }
    }
}

/** Rotated rubber stamp used for honours, paid states and receipt totals. */
@Composable
fun RubberStamp(
    text: String,
    modifier: Modifier = Modifier,
    tone: Color = MaterialTheme.colorScheme.secondary,
) {
    Surface(
        modifier = modifier.rotate(-7f),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(1.5.dp, tone.copy(alpha = 0.7f)),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                letterSpacing = 1.6.sp,
            ),
            color = tone,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

/**
 * Academic seal for honours and verified documents: two printed rings around a monogram.
 * Reserved for moments that deserve it, so it keeps its weight.
 */
@Composable
fun AcademicSeal(
    caption: String,
    monogram: String,
    subCaption: String,
    modifier: Modifier = Modifier,
    size: Dp = 82.dp,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier.size(size).rotate(-5f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = colors.secondary.copy(alpha = 0.55f),
                radius = this.size.minDimension / 2f - 0.5.dp.toPx(),
                style = Stroke(width = 1.4.dp.toPx()),
            )
            drawCircle(
                color = colors.secondary.copy(alpha = 0.35f),
                radius = this.size.minDimension / 2f - 4.dp.toPx(),
                style = Stroke(width = 0.8.dp.toPx()),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = caption.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 6.5.sp,
                    letterSpacing = 1.sp,
                ),
                color = colors.secondary.copy(alpha = 0.85f),
            )
            Text(
                text = monogram,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 21.sp,
                    lineHeight = 23.sp,
                ),
                color = colors.secondary,
            )
            Text(
                text = subCaption.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 6.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = colors.secondary.copy(alpha = 0.85f),
            )
        }
    }
}

/** Small square date stamp, like the postmark on correspondence. */
@Composable
fun PostmarkStamp(day: String, month: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .width(46.dp)
            .border(1.dp, colors.outlineVariant, MaterialTheme.shapes.extraSmall)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = colors.onSurface,
        )
        Text(
            text = month.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 7.5.sp,
                letterSpacing = 1.sp,
            ),
            color = colors.onSurfaceVariant,
        )
    }
}

/** Ruled margin used on submission sheets: content hangs off a terracotta rule. */
@Composable
fun RuledSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CutCornerShape(topEnd = 18.dp),
        color = colors.surfaceContainerLowest,
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.9f)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val marginX = 30.dp.toPx()
                drawLine(
                    color = colors.secondary.copy(alpha = 0.45f),
                    start = Offset(marginX, 0f),
                    end = Offset(marginX, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
                var y = 26.dp.toPx()
                while (y < size.height) {
                    drawLine(
                        color = colors.outlineVariant.copy(alpha = 0.45f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                    )
                    y += 26.dp.toPx()
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 40.dp, end = 16.dp),
                content = content,
            )
        }
    }
}

private fun formatDecimal(value: Double): String {
    val rounded = (value * 100).roundToInt()
    val whole = rounded / 100
    val cents = (rounded % 100).toString().padStart(2, '0')
    return "$whole.$cents"
}

/** Small helper for the section kickers used above document titles. */
@Composable
fun DocumentLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 1.5.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/** A horizontal set of state filters, drawn as filing tabs. */
@Composable
fun LedgerTabsRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
    ) {
        options.forEach { option ->
            val active = option == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .selectable(selected = active, role = Role.Tab, onClick = { onSelect(option) })
                    .background(if (active) colors.primary else Color.Transparent)
                    .padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = option.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                    ),
                    color = if (active) colors.onPrimary else colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}

/** Empty state for a section with nothing in it yet. */
@Composable
fun LedgerEmpty(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = colors.outlineVariant)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 20.dp),
        )
        HorizontalDivider(color = colors.outlineVariant)
    }
}

/** Trio of figures printed like a register column, without a card. */
@Composable
fun RegisterColumns(
    items: List<Metric>,
    modifier: Modifier = Modifier,
    accentIndex: Int? = null,
) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(46.dp)
                        .align(Alignment.CenterVertically)
                        .background(colors.outlineVariant),
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = if (index == 0) 0.dp else 12.dp)) {
                Text(
                    text = item.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        letterSpacing = 1.1.sp,
                    ),
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = item.value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (accentIndex == index) colors.secondary else colors.onSurface,
                    maxLines = 1,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.caption,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** Spacer used between printed sections; kept here so screens share one rhythm. */
@Composable
fun SectionGap(height: Dp = 26.dp) {
    Spacer(Modifier.height(height))
}
