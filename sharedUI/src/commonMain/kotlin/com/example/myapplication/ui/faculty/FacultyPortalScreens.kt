package com.example.myapplication.ui.faculty

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.portal.FacultyMessage
import com.example.myapplication.portal.FacultyPortalData
import com.example.myapplication.portal.FacultySection
import com.example.myapplication.portal.GradingItem
import com.example.myapplication.portal.GradingStatus
import com.example.myapplication.portal.PortalAccount
import com.example.myapplication.portal.RosterStanding
import com.example.myapplication.portal.greetingName
import com.example.myapplication.ui.PlatformBackHandler
import com.example.myapplication.ui.portal.DayTimetable
import com.example.myapplication.ui.portal.DocumentLabel
import com.example.myapplication.ui.portal.LedgerEmpty
import com.example.myapplication.ui.portal.LedgerTabsRow
import com.example.myapplication.ui.portal.Metric
import com.example.myapplication.ui.portal.MonogramTile
import com.example.myapplication.ui.portal.PageMasthead
import com.example.myapplication.ui.portal.PortalPage
import com.example.myapplication.ui.portal.PortalTab
import com.example.myapplication.ui.portal.PostmarkStamp
import com.example.myapplication.ui.portal.PreviewNote
import com.example.myapplication.ui.portal.ProgressBar
import com.example.myapplication.ui.portal.RegisterColumns
import com.example.myapplication.ui.portal.RubberStamp
import com.example.myapplication.ui.portal.RuledSheet
import com.example.myapplication.ui.portal.SectionIndex
import com.example.myapplication.ui.portal.TermProgress
import com.example.myapplication.ui.portal.TimetableEntry
import kotlin.math.roundToInt

/** Faculty overview: the teaching day to scale, the load register, the queue and deadlines. */
@Composable
fun FacultyHomeScreen(
    account: PortalAccount,
    data: FacultyPortalData,
    innerPadding: PaddingValues,
    onOpenTab: (PortalTab) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val teachingToday = data.sessionsFor(data.today)
    val unread = data.messages.firstOrNull { it.unread }
    val awaiting = data.gradingQueue.count { it.status == GradingStatus.Awaiting }
    val drafts = data.gradingQueue.count { it.status == GradingStatus.Draft }
    val returned = data.gradingQueue.count { it.status == GradingStatus.Returned }

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Faculty portal · ${data.department.removePrefix("Department of ")}",
            title = account.greetingName()?.let { "Good day,\n$it." } ?: "Good day.",
            subtitle = account.affiliation,
            meta = "${data.today.short} 29 OCT",
        )

        Spacer(Modifier.height(20.dp))
        TermProgress(current = data.termWeek, total = data.termWeeks)

        Spacer(Modifier.height(28.dp))
        SectionIndex(
            number = "01",
            title = "Teaching day · ${data.today.long}",
            trailing = "Classes",
            onTrailingClick = { onOpenTab(PortalTab.FacultyClasses) },
        )
        Spacer(Modifier.height(14.dp))
        DayTimetable(
            entries = teachingToday.mapIndexed { index, (section, session) ->
                TimetableEntry(
                    startMinutes = session.startMinutes,
                    endMinutes = session.startMinutes + durationMinutes(session.start, session.end),
                    start = session.start,
                    end = session.end,
                    code = section.code,
                    title = section.title,
                    meta = "${session.room} · ${section.enrolled} enrolled",
                    highlight = index == 0,
                )
            },
            hourHeight = 68.dp,
            emptyNote = "No sessions scheduled today. A reading or consultation day.",
            freeLabel = "Open · consultation hours",
        )

        Spacer(Modifier.height(28.dp))
        SectionIndex(number = "02", title = "Load register")
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = colors.outlineVariant)
        Spacer(Modifier.height(12.dp))
        RegisterColumns(
            items = listOf(
                Metric("Sections", data.sections.size.toString(), "This term"),
                Metric("Students", data.studentCount.toString(), "Enrolled"),
                Metric("To grade", data.pendingGrading.toString(), "Submissions"),
            ),
            accentIndex = 2,
        )

        Spacer(Modifier.height(28.dp))
        SectionIndex(
            number = "03",
            title = "Grading queue",
            trailing = "Open board",
            onTrailingClick = { onOpenTab(PortalTab.FacultyGrading) },
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { onOpenTab(PortalTab.FacultyGrading) },
        ) {
            listOf(
                Triple("Awaiting", awaiting, colors.secondary),
                Triple("Drafts", drafts, colors.onSurface),
                Triple("Returned", returned, colors.onSurfaceVariant),
            ).forEachIndexed { index, (label, count, tone) ->
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(54.dp)
                            .align(Alignment.CenterVertically)
                            .background(colors.outlineVariant),
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = if (index == 0) 0.dp else 12.dp)) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 32.sp,
                            lineHeight = 36.sp,
                        ),
                        color = tone,
                    )
                    Text(
                        text = label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = colors.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        SectionIndex(number = "04", title = "Deadlines", trailing = "NOVEMBER")
        Spacer(Modifier.height(6.dp))
        data.deadlines.forEachIndexed { index, deadline ->
            val date = deadline.due.substringBefore(" · ")
            val day = date.substringAfter(' ', "")
            val month = date.substringBefore(' ', date)
            val time = deadline.due.substringAfter(" · ", "")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.width(44.dp)) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.onSurface,
                    )
                    Text(
                        text = month.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            letterSpacing = 1.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(
                        text = deadline.label,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                    )
                    if (time.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.secondary,
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colors.outline,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (index < data.deadlines.lastIndex) {
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
            }
        }

        if (unread != null) {
            Spacer(Modifier.height(28.dp))
            SectionIndex(
                number = "05",
                title = "Inbox",
                trailing = "${data.unreadMessages} unread",
                onTrailingClick = { onOpenTab(PortalTab.FacultyInbox) },
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onOpenTab(PortalTab.FacultyInbox) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PostmarkStamp(
                    day = unread.sent.substringAfter(' '),
                    month = unread.sent.substringBefore(' '),
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = unread.from,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            letterSpacing = 0.8.sp,
                        ),
                        color = colors.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = unread.subject,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = unread.preview,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(Modifier.height(26.dp))
        PreviewNote(
            "Preview data — this build isn't connected to your institution's server yet.",
        )
    }
}

/** Assigned sections as a register table, with grading progress printed in place. */
@Composable
fun FacultyClassesScreen(data: FacultyPortalData, innerPadding: PaddingValues) {
    var openCode by rememberSaveable { mutableStateOf<String?>(null) }
    val openSection = data.sections.firstOrNull { it.code == openCode }

    if (openSection != null) {
        SectionDetailScreen(
            section = openSection,
            innerPadding = innerPadding,
            onBack = { openCode = null },
        )
        return
    }

    val colors = MaterialTheme.colorScheme
    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Teaching · Assigned load",
            title = "Classes",
            subtitle = "${data.studentCount} students across ${data.sections.size} sections",
            meta = "1ST SEMESTER",
        )

        Spacer(Modifier.height(22.dp))
        SectionIndex(number = "01", title = "Section register", trailing = "${data.sections.size} SECTIONS")
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(
                text = "SECTION",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "GRADED",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(54.dp),
            )
            Text(
                text = "AVG",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(42.dp),
            )
        }
        HorizontalDivider(color = colors.outline)

        data.sections.forEachIndexed { index, section ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openCode = section.code }
                    .padding(vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = section.code,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                            color = colors.secondary,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onSurface,
                        )
                    }
                    Text(
                        text = "${section.graded}/${section.enrolled}",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(54.dp),
                    )
                    Text(
                        text = section.average,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(42.dp),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${section.schedule} · ${section.room} · ${section.yearLevel} · " +
                        "${section.atRisk} at risk",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                ProgressBar(fraction = section.gradedFraction)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DocumentLabel("Open roster", modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = colors.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            if (index < data.sections.lastIndex) {
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(22.dp))
        Text(
            text = "Sections are assigned by your department chair. Load changes go through the " +
                "registrar before the add-drop deadline.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionDetailScreen(
    section: FacultySection,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    PlatformBackHandler(onBack = onBack)

    PortalPage(innerPadding) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back to classes",
                    tint = colors.onSurface,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = "ALL CLASSES",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                color = colors.secondary,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(onClick = onBack)
                    .padding(vertical = 6.dp),
            )
        }

        PageMasthead(
            folio = "${section.code} · ${section.yearLevel}",
            title = section.title,
            subtitle = "${section.program} · ${section.schedule} · ${section.room}",
            meta = section.average,
        )

        Spacer(Modifier.height(20.dp))
        RegisterColumns(
            items = listOf(
                Metric("Enrolled", section.enrolled.toString(), "Students"),
                Metric("Graded", "${section.graded}", "of ${section.enrolled}"),
                Metric("At risk", section.atRisk.toString(), "Attendance"),
            ),
            accentIndex = 2,
        )

        Spacer(Modifier.height(22.dp))
        StandingDistribution(section = section)

        Spacer(Modifier.height(26.dp))
        SectionIndex(number = "01", title = "Roster", trailing = "${section.roster.size} SHOWN")
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(
                text = "STUDENT",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "ATT.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(42.dp),
            )
            Text(
                text = "RATING",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(52.dp),
            )
        }
        HorizontalDivider(color = colors.outline)

        section.roster.forEachIndexed { index, student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(30.dp)
                        .background(
                            when (student.standing) {
                                RosterStanding.Good -> colors.tertiary.copy(alpha = 0.6f)
                                RosterStanding.Watch -> colors.secondary.copy(alpha = 0.6f)
                                RosterStanding.AtRisk -> colors.error.copy(alpha = 0.7f)
                            },
                        ),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${student.id} · ${student.standing.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (student.standing == RosterStanding.AtRisk) {
                            colors.error
                        } else {
                            colors.onSurfaceVariant
                        },
                        maxLines = 1,
                    )
                }
                Text(
                    text = "${student.attendancePercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(42.dp),
                )
                Text(
                    text = student.rating,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(52.dp),
                )
            }
            if (index < section.roster.lastIndex) {
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Showing ${section.roster.size} of ${section.enrolled} students. The complete " +
                "roster and grade sheets are in the web panel.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

/** Standing split printed as a single measured bar instead of three separate chips. */
@Composable
private fun StandingDistribution(section: FacultySection) {
    val colors = MaterialTheme.colorScheme
    val good = section.roster.count { it.standing == RosterStanding.Good }
    val watch = section.roster.count { it.standing == RosterStanding.Watch }
    val risk = section.roster.count { it.standing == RosterStanding.AtRisk }
    val total = (good + watch + risk).coerceAtLeast(1)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DocumentLabel("Standing")
            Spacer(Modifier.weight(1f))
            Text(
                text = "$good on track · $watch watch · $risk at risk",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth().height(10.dp)) {
            listOf(
                good to colors.tertiary,
                watch to colors.secondary,
                risk to colors.error,
            ).forEach { (count, color) ->
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .weight(count.toFloat() / total)
                            .fillMaxHeight()
                            .background(color.copy(alpha = 0.8f)),
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row {
            Text(
                text = "${section.graded} of ${section.enrolled} ratings encoded",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/** The grading queue as a deck of submissions you page through, plus the full list. */
@Composable
fun FacultyGradingScreen(data: FacultyPortalData, innerPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme
    val filters = listOf("All", "Awaiting", "Draft", "Returned")
    var filter by rememberSaveable { mutableStateOf("All") }
    val filtered = data.gradingQueue.filter { item ->
        when (filter) {
            "Awaiting" -> item.status == GradingStatus.Awaiting
            "Draft" -> item.status == GradingStatus.Draft
            "Returned" -> item.status == GradingStatus.Returned
            else -> true
        }
    }
    var index by rememberSaveable { mutableIntStateOf(0) }

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Grading · Submission queue",
            title = "Submissions",
            subtitle = "${data.gradingQueue.size} in the queue · ${data.pendingGrading} still open",
            meta = "DUE NOV 20",
        )

        Spacer(Modifier.height(20.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraSmall,
            color = colors.surfaceContainerHigh.copy(alpha = 0.6f),
        ) {
            LedgerTabsRow(
                options = filters,
                selected = filter,
                onSelect = { selected ->
                    filter = selected
                    index = 0
                },
            )
        }

        Spacer(Modifier.height(22.dp))
        SectionIndex(
            number = "01",
            title = "Top of the stack",
            trailing = if (filtered.isEmpty()) null else "${filtered.size} SHEETS",
        )
        Spacer(Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            LedgerEmpty(text = "Nothing in this column. Pick another tab.")
        } else {
            val safeIndex = index.coerceIn(0, filtered.lastIndex)
            SubmissionDeck(
                items = filtered,
                index = safeIndex,
                onPrevious = { index = (safeIndex - 1 + filtered.size) % filtered.size },
                onNext = { index = (safeIndex + 1) % filtered.size },
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionIndex(number = "02", title = "Whole queue")
        Spacer(Modifier.height(4.dp))
        filtered.forEachIndexed { rowIndex, item ->
            QueueRow(item = item)
            if (rowIndex < filtered.lastIndex) {
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Final grades close on Nov 20 at 5:00 PM. Late encoding requires a signed " +
                "request from your department chair.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))
        PreviewNote(
            "Grade entry opens in the KoAkademy web panel. Ratings encoded there will appear " +
                "on this board once the app is connected.",
        )
    }
}

@Composable
private fun SubmissionDeck(
    items: List<GradingItem>,
    index: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val item = items[index]

    Box(modifier = Modifier.fillMaxWidth()) {
        if (items.size > 2) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 16.dp)
                    .height(258.dp)
                    .rotate(-0.8f),
                shape = CutCornerShape(topEnd = 18.dp),
                color = colors.surfaceContainer,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.7f)),
            ) {}
        }
        if (items.size > 1) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 7.dp, end = 7.dp, top = 8.dp)
                    .height(258.dp)
                    .rotate(0.6f),
                shape = CutCornerShape(topEnd = 18.dp),
                color = colors.surfaceContainerLow,
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.8f)),
            ) {}
        }

        RuledSheet(modifier = Modifier.height(258.dp)) {
            Column(modifier = Modifier.fillMaxSize().padding(top = 14.dp, bottom = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.sectionCode,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = colors.secondary,
                        modifier = Modifier.weight(1f),
                    )
                    RubberStamp(text = item.status.label)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.activity,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MonogramTile(name = item.student, size = 30.dp)
                    Spacer(Modifier.width(9.dp))
                    Column {
                        Text(
                            text = item.student,
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                        )
                        Text(
                            text = "${item.submitted} · ${item.status.label}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = colors.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DocumentLabel(text = "File")
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = item.attachment,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                        ),
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = item.note,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        fontStyle = FontStyle.Italic,
                    ),
                    color = colors.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.weight(1f))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.8f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PREV",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                        color = if (items.size > 1) colors.secondary else colors.outline,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable(enabled = items.size > 1, onClick = onPrevious)
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${index + 1} / ${items.size}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            letterSpacing = 1.4.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "NEXT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                        color = if (items.size > 1) colors.secondary else colors.outline,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable(enabled = items.size > 1, onClick = onNext)
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    Text(
        text = "Page through the stack to preview each submission. Ratings are entered in the " +
            "web panel.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun QueueRow(item: GradingItem) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(
                    when (item.status) {
                        GradingStatus.Awaiting -> colors.secondary
                        GradingStatus.Draft -> colors.outline
                        GradingStatus.Returned -> colors.tertiary
                    },
                ),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.activity,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${item.sectionCode} · ${item.student} · ${item.submitted.replace("Submitted ", "")}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = item.status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
            color = if (item.status == GradingStatus.Awaiting) colors.secondary else colors.onSurfaceVariant,
        )
    }
}

/** Institutional correspondence, set as letters with postmarks and a letter sheet. */
@Composable
fun FacultyInboxScreen(data: FacultyPortalData, innerPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme
    var openMessage by remember { mutableStateOf<FacultyMessage?>(null) }

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Inbox · Institutional correspondence",
            title = "Letters",
            subtitle = "${data.messages.size} this term · ${data.unreadMessages} unread",
            meta = "OCTOBER",
        )

        Spacer(Modifier.height(20.dp))
        SectionIndex(
            number = "01",
            title = "Received",
            trailing = "UNREAD FIRST",
        )
        Spacer(Modifier.height(4.dp))

        data.messages.sortedByDescending { it.unread }.forEachIndexed { index, message ->
            LetterRow(message = message, onClick = { openMessage = message })
            if (index < data.messages.lastIndex) {
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            text = "Messages mirror your institutional inbox. Replies are sent from the web panel.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }

    openMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { openMessage = null },
            shape = MaterialTheme.shapes.small,
            containerColor = colors.surfaceContainerLowest,
            titleContentColor = colors.onSurface,
            textContentColor = colors.onSurfaceVariant,
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "KOAKADEMY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                            ),
                            color = colors.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = message.sent.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            color = colors.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider(color = colors.onSurface)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = message.subject,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "${message.from} · ${message.fromRole}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            letterSpacing = 0.8.sp,
                        ),
                        color = colors.secondary,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = message.body,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            lineHeight = 26.sp,
                        ),
                    )
                    Spacer(Modifier.height(18.dp))
                    HorizontalDivider(color = colors.outlineVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Office of the ${message.fromRole}",
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                        color = colors.onSurfaceVariant,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { openMessage = null }) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
private fun LetterRow(message: FacultyMessage, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.Top,
    ) {
        PostmarkStamp(
            day = message.sent.substringAfter(' '),
            month = message.sent.substringBefore(' '),
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.from.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                    color = if (message.unread) colors.secondary else colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (message.unread) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(colors.secondary),
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = message.subject,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = message.preview,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Minutes between two "8:00 AM" style times, used to scale the teaching day. */
private fun durationMinutes(start: String, end: String): Int {
    fun toMinutes(value: String): Int {
        val clock = value.substringBefore(' ')
        val meridiem = value.substringAfter(' ', "")
        val hours = clock.substringBefore(':').toIntOrNull() ?: 0
        val minutes = clock.substringAfter(':', "0").toIntOrNull() ?: 0
        val normalized = hours % 12 + if (meridiem.equals("PM", ignoreCase = true)) 12 else 0
        return normalized * 60 + minutes
    }
    return (toMinutes(end) - toMinutes(start)).coerceAtLeast(30)
}
