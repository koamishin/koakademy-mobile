package com.example.myapplication.ui.portal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Editorial, ledger-like building blocks: oversized date stamps, ruled section indexes,
 * index cards with a cut corner, perforated statements and leader rows. These are the
 * layouts that give the portal its own character instead of a stack of identical cards.
 */

@Composable
fun DateStamp(
    day: String,
    month: String,
    weekday: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Text(
            text = weekday.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.secondary,
        )
        Text(
            text = day,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 44.sp,
                lineHeight = 46.sp,
            ),
            color = colors.onBackground,
        )
        Text(
            text = month.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
fun SectionIndex(
    number: String,
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = number.padStart(2, '0'),
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                letterSpacing = 0.8.sp,
            ),
            color = colors.secondary,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = colors.outlineVariant.copy(alpha = 0.8f),
        )
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelMedium,
                color = colors.secondary,
                modifier = if (onTrailingClick != null) {
                    Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = onTrailingClick)
                } else {
                    Modifier
                },
            )
        }
    }
}

data class DayCell(val label: String, val date: String, val selected: Boolean)

/** The week as a ledger strip; the selected day is an ink block. */
@Composable
fun WeekRail(
    days: List<DayCell>,
    modifier: Modifier = Modifier,
    onSelect: ((Int) -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, colors.outlineVariant, MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEachIndexed { index, day ->
            if (index > 0) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = colors.outlineVariant,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (day.selected) colors.primary else Color.Transparent)
                    .then(
                        if (onSelect != null) {
                            Modifier.clickable { onSelect(index) }
                        } else {
                            Modifier
                        },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                Text(
                    text = day.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = if (day.selected) {
                        colors.onPrimary.copy(alpha = 0.65f)
                    } else {
                        colors.onSurfaceVariant
                    },
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = day.date,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    color = if (day.selected) colors.onPrimary else colors.onSurface,
                )
            }
        }
    }
}

/** Metric row without a card: opens and closes with hairlines, like a register. */
@Composable
fun LedgerMetrics(items: List<Metric>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = colors.outlineVariant)
        Row(modifier = Modifier.padding(vertical = 14.dp)) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    VerticalDivider(
                        modifier = Modifier.height(42.dp),
                        color = colors.outlineVariant,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (index == 0) 0.dp else 14.dp, end = 12.dp),
                ) {
                    Text(
                        text = item.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurface,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        HorizontalDivider(color = colors.outlineVariant)
    }
}

/** A ruled timeline row: time gutter, continuous rule, dot and content. */
@Composable
fun TimelineEntry(
    start: String,
    end: String,
    code: String,
    title: String,
    meta: String,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier.width(66.dp).padding(top = 2.dp),
        ) {
            Text(
                text = start,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
            )
            Text(
                text = end,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Box(
            modifier = Modifier
                .width(20.dp)
                .fillMaxHeight(),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2f
                val dotY = 9.dp.toPx()
                drawLine(
                    color = colors.outlineVariant,
                    start = Offset(centerX, if (isFirst) dotY else 0f),
                    end = Offset(centerX, if (isLast) dotY else size.height),
                    strokeWidth = 1.dp.toPx(),
                )
                drawCircle(
                    color = if (highlight) colors.secondary else colors.outline,
                    radius = 3.5.dp.toPx(),
                    center = Offset(centerX, dotY),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 22.dp),
        ) {
            Text(
                text = code.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.secondary,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Card with a cut top-right corner, used for class and correspondence index cards. */
@Composable
fun IndexCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CutCornerShape(topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.85f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), content = content)
    }
}

/** Ink statement card with cut corners; pair with [Perforation] for the tear line. */
@Composable
fun StatementCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = CutCornerShape(topEnd = 28.dp, bottomStart = 28.dp),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Column(content = content)
    }
}

/** Dashed tear line with semicircular notches, drawn in page colour. */
@Composable
fun Perforation(
    lineColor: Color,
    notchColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(20.dp)) {
        val centerY = size.height / 2f
        drawLine(
            color = lineColor,
            start = Offset(12.dp.toPx(), centerY),
            end = Offset(size.width - 12.dp.toPx(), centerY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(3.dp.toPx(), 5.dp.toPx()),
            ),
        )
        drawCircle(notchColor, 8.dp.toPx(), Offset(0f, centerY))
        drawCircle(notchColor, 8.dp.toPx(), Offset(size.width, centerY))
    }
}

/** Rubber-stamp style badge for honours or term labels. */
@Composable
fun StampBadge(text: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.rotate(-4f),
        shape = MaterialTheme.shapes.extraSmall,
        color = Color.Transparent,
        border = BorderStroke(1.5.dp, colors.secondary.copy(alpha = 0.65f)),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.8.sp),
            color = colors.secondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun MonogramTile(name: String, size: Dp = 40.dp, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val initials = name.split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.small,
        color = colors.surfaceContainerHigh,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                ),
                color = colors.secondary,
            )
        }
    }
}

/** Label and amount joined by a dotted leader, as on a printed statement. */
@Composable
fun LeaderRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            maxLines = 1,
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .height(12.dp),
        ) {
            drawLine(
                color = colors.outlineVariant,
                start = Offset(0f, size.height - 2.dp.toPx()),
                end = Offset(size.width, size.height - 2.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(1.5.dp.toPx(), 3.5.dp.toPx()),
                ),
            )
        }
        Text(
            text = value,
            style = if (emphasize) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = colors.onSurface,
        )
    }
}

/** Term progress as a ticked rule, one tick per teaching week. */
@Composable
fun TermProgress(current: Int, total: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "WEEK $current OF $total",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${total - current} weeks to finals",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(9.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) {
            val centerY = size.height / 2f
            drawLine(
                color = colors.outlineVariant,
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 1.5.dp.toPx(),
            )
            val reached = size.width * (current.toFloat() / total)
            drawLine(
                color = colors.secondary,
                start = Offset(0f, centerY),
                end = Offset(reached, centerY),
                strokeWidth = 1.5.dp.toPx(),
            )
            repeat(total + 1) { index ->
                val x = size.width * (index.toFloat() / total)
                drawLine(
                    color = if (index <= current) colors.secondary else colors.outlineVariant,
                    start = Offset(x, centerY - 3.dp.toPx()),
                    end = Offset(x, centerY + 3.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            drawCircle(colors.secondary, radius = 4.dp.toPx(), center = Offset(reached, centerY))
        }
    }
}

/** Horizontally scrolling kanban column used by the faculty grading board. */
@Composable
fun BoardColumn(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Surface(
                shape = CircleShape,
                color = colors.surfaceContainerHigh,
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurface,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(colors.surfaceContainerLow.copy(alpha = 0.55f))
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.7f), MaterialTheme.shapes.medium)
                .padding(10.dp),
            content = content,
        )
    }
}
