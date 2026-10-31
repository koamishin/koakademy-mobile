package com.example.myapplication.ui.student

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.portal.Announcement
import com.example.myapplication.portal.ClassDay
import com.example.myapplication.portal.Course
import com.example.myapplication.portal.Payment
import com.example.myapplication.portal.PortalAccount
import com.example.myapplication.portal.StudentPortalData
import com.example.myapplication.portal.greetingName
import com.example.myapplication.ui.components.KoakademyMark
import com.example.myapplication.ui.portal.AcademicSeal
import com.example.myapplication.ui.portal.BarcodeStrip
import com.example.myapplication.ui.portal.DayCell
import com.example.myapplication.ui.portal.DayTabs
import com.example.myapplication.ui.portal.DayTimetable
import com.example.myapplication.ui.portal.DocumentLabel
import com.example.myapplication.ui.portal.Metric
import com.example.myapplication.ui.portal.PageMasthead
import com.example.myapplication.ui.portal.PerforationLine
import com.example.myapplication.ui.portal.PortalPage
import com.example.myapplication.ui.portal.PortalTab
import com.example.myapplication.ui.portal.PreviewNote
import com.example.myapplication.ui.portal.RatingComparison
import com.example.myapplication.ui.portal.RatingRow
import com.example.myapplication.ui.portal.ReceiptPanel
import com.example.myapplication.ui.portal.ReceiptRow
import com.example.myapplication.ui.portal.RegisterColumns
import com.example.myapplication.ui.portal.RubberStamp
import com.example.myapplication.ui.portal.SectionIndex
import com.example.myapplication.ui.portal.SplitRule
import com.example.myapplication.ui.portal.TermProgress
import com.example.myapplication.ui.portal.TimetableEntry
import kotlin.math.roundToInt

/**
 * Student overview as a front page: the day's timetable ruled to scale, the term register,
 * and the notices column. The day tabs drive the timetable in place.
 */
@Composable
fun StudentHomeScreen(
    account: PortalAccount,
    data: StudentPortalData,
    innerPadding: PaddingValues,
    onOpenTab: (PortalTab) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var selectedDayName by rememberSaveable { mutableStateOf(data.today.name) }
    val selectedDay = ClassDay.entries.firstOrNull { it.name == selectedDayName } ?: data.today
    val sessions = data.sessionsFor(selectedDay)

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Student portal · ${data.term.substringBefore(" · ")}",
            title = account.greetingName()?.let { "Welcome back,\n$it." } ?: "Welcome back.",
            subtitle = "${data.program} · ${data.yearLevel} · ${data.section}",
            meta = "${data.today.short} ${data.dayDates[data.today]} OCT",
        )

        Spacer(Modifier.height(18.dp))
        DayTabs(
            days = ClassDay.entries.map { day ->
                DayCell(
                    label = day.short,
                    date = data.dayDates[day] ?: "",
                    selected = day == selectedDay,
                )
            },
            onSelect = { index -> selectedDayName = ClassDay.entries[index].name },
        )

        Spacer(Modifier.height(26.dp))
        SectionIndex(
            number = "01",
            title = if (selectedDay == data.today) "Today · ${selectedDay.long}" else selectedDay.long,
            trailing = "Full schedule",
            onTrailingClick = { onOpenTab(PortalTab.StudentSchedule) },
        )
        Spacer(Modifier.height(14.dp))
        DayTimetable(
            entries = sessions.mapIndexed { index, (course, session) ->
                TimetableEntry(
                    startMinutes = session.startMinutes,
                    endMinutes = session.startMinutes + durationMinutes(session.start, session.end),
                    start = session.start,
                    end = session.end,
                    code = course.code,
                    title = course.title,
                    meta = "${session.room} · ${course.instructor}",
                    highlight = index == 0,
                )
            },
            hourHeight = 62.dp,
            compact = true,
            emptyNote = "No classes today. A good day for review or consultations.",
            freeLabel = "Open · no class",
        )

        Spacer(Modifier.height(26.dp))
        HorizontalDivider(color = colors.outlineVariant)
        Spacer(Modifier.height(6.dp))
        SectionIndex(number = "02", title = "This term")
        Spacer(Modifier.height(10.dp))
        RegisterColumns(
            items = listOf(
                Metric("Units", data.units.toString(), "${data.courses.size} courses"),
                Metric("Average", formatRating(data.gpa), "1.00 is highest"),
                Metric("Balance", data.tuitionBalance.removeSuffix(".00"), data.tuitionDue),
            ),
            accentIndex = 2,
        )
        Spacer(Modifier.height(22.dp))
        TermProgress(current = data.termWeek, total = data.termWeeks)

        Spacer(Modifier.height(28.dp))
        SectionIndex(
            number = "03",
            title = "Notices",
            trailing = "Registrar",
            onTrailingClick = { onOpenTab(PortalTab.StudentGrades) },
        )
        Spacer(Modifier.height(16.dp))
        data.announcements.take(2).forEachIndexed { index, announcement ->
            MarginalNotice(announcement = announcement, index = index + 1)
            if (index == 0) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.7f))
                Spacer(Modifier.height(14.dp))
            }
        }

        Spacer(Modifier.height(26.dp))
        PreviewNote(
            "Preview data — this build isn't connected to your institution's server yet.",
        )
    }
}

/**
 * A notice set in the margin of the register: number, source, serif headline and body,
 * marked by a terracotta rule down the left.
 */
@Composable
private fun MarginalNotice(announcement: Announcement, index: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(colors.secondary.copy(alpha = 0.45f)),
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = index.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                    ),
                    color = colors.secondary,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = announcement.source.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = colors.secondary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = announcement.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** The timetable at full size, with the day tabs switching the whole page in place. */
@Composable
fun StudentScheduleScreen(data: StudentPortalData, innerPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme
    var selectedDayName by rememberSaveable { mutableStateOf(data.today.name) }
    val selectedDay = ClassDay.entries.firstOrNull { it.name == selectedDayName } ?: data.today
    val sessions = data.sessionsFor(selectedDay)

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Academics · Timetable",
            title = "Schedule",
            subtitle = data.term,
            meta = "WEEK ${data.termWeek} OF ${data.termWeeks}",
        )

        Spacer(Modifier.height(18.dp))
        DayTabs(
            days = ClassDay.entries.map { day ->
                DayCell(
                    label = day.short,
                    date = data.dayDates[day] ?: "",
                    selected = day == selectedDay,
                )
            },
            onSelect = { index -> selectedDayName = ClassDay.entries[index].name },
        )

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedDay.long,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (sessions.isEmpty()) {
                    "NO CLASSES"
                } else {
                    "${sessions.size} SESSION${if (sessions.size == 1) "" else "S"}"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(14.dp))

        DayTimetable(
            entries = sessions.mapIndexed { index, (course, session) ->
                TimetableEntry(
                    startMinutes = session.startMinutes,
                    endMinutes = session.startMinutes + durationMinutes(session.start, session.end),
                    start = session.start,
                    end = session.end,
                    code = "${course.code} · ${course.units} units",
                    title = course.title,
                    meta = "${session.room} · ${course.instructor}",
                    highlight = index == 0,
                )
            },
            hourHeight = 74.dp,
            emptyNote = "No classes scheduled. Use the day for review, consultations or " +
                "organization work.",
        )

        Spacer(Modifier.height(30.dp))
        SectionIndex(number = "01", title = "Term register")
        Spacer(Modifier.height(10.dp))
        RegisterColumns(
            items = listOf(
                Metric("Units", data.units.toString(), data.program),
                Metric("Section", data.section, data.yearLevel),
                Metric("Week", "${data.termWeek}/${data.termWeeks}", "First semester"),
            ),
        )

        Spacer(Modifier.height(24.dp))
        SectionIndex(number = "02", title = "Class list")
        Spacer(Modifier.height(2.dp))
        data.courses.forEachIndexed { index, course ->
            CourseRow(course = course, index = index + 1)
            HorizontalDivider(
                color = colors.outlineVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = "Attendance is recorded by your instructor during each session. Blocks above " +
                "are drawn to scale, so a 90-minute class takes half again the space of a 60-minute one.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun CourseRow(course: Course, index: Int) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = index.toString().padStart(2, '0'),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 10.sp,
            ),
            color = colors.outline,
            modifier = Modifier.width(22.dp).padding(top = 3.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${course.code} · ${course.units} units · ${course.instructor}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = course.finalGrade,
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
            )
            Text(
                text = "${course.attendancePercent}%",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/** The transcript: one shared rating axis, then the course record as a printed table. */
@Composable
fun StudentGradesScreen(data: StudentPortalData, innerPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Academics · Record of ratings",
            title = "Transcript",
            subtitle = "${data.program} · ${data.yearLevel} · ${data.section}",
            meta = "FIRST SEMESTER",
        )

        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                DocumentLabel("General average")
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatRating(data.gpa),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 64.sp,
                            lineHeight = 66.sp,
                        ),
                        color = colors.onSurface,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "/ 5.00",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 10.dp),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${data.units} units · ${data.courses.size} courses · no failures",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
            AcademicSeal(
                caption = "General average",
                monogram = formatRating(data.gpa),
                subCaption = "Dean's list",
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionIndex(number = "01", title = "Ratings by course", trailing = "1.00 – 5.00")
        Spacer(Modifier.height(14.dp))
        RatingComparison(
            rows = data.courses.map { course ->
                RatingRow(
                    code = course.code,
                    midterm = course.midtermRating.toDoubleOrNull() ?: 0.0,
                    final = course.finalRating.toDoubleOrNull() ?: 0.0,
                )
            },
            average = data.gpa,
        )

        Spacer(Modifier.height(28.dp))
        SectionIndex(number = "02", title = "Course record")
        Spacer(Modifier.height(12.dp))
        GradeTableHeader()
        data.courses.forEach { course ->
            GradeTableRow(course = course)
            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Ratings follow the institutional 1.00–5.00 scale, where 1.00 is the highest. " +
                "Corrections must be filed with the registrar within five days of posting.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun GradeTableHeader() {
    val colors = MaterialTheme.colorScheme
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(
            text = "COURSE",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        listOf("UNITS" to 34.dp, "MID" to 34.dp, "FINAL" to 42.dp, "ATT." to 38.dp).forEach { (label, width) ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(width),
            )
        }
    }
    HorizontalDivider(color = colors.outline)
}

@Composable
private fun GradeTableRow(course: Course) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.code,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                color = colors.secondary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = course.units.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(34.dp),
        )
        Text(
            text = course.midtermRating,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(34.dp),
        )
        Text(
            text = course.finalGrade,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.width(42.dp),
        )
        Text(
            text = "${course.attendancePercent}%",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(38.dp),
        )
    }
}

/** Tuition as a printed receipt from the bursar, followed by the assessment ledger. */
@Composable
fun StudentFinanceScreen(data: StudentPortalData, innerPadding: PaddingValues) {
    val colors = MaterialTheme.colorScheme
    val name = "Ada Santos"

    PortalPage(innerPadding) {
        Spacer(Modifier.height(10.dp))
        PageMasthead(
            folio = "Finance · Office of the bursar",
            title = "Statement",
            subtitle = data.term,
            meta = data.tuitionDue.uppercase(),
        )

        Spacer(Modifier.height(22.dp))
        ReceiptPanel {
            Column(modifier = Modifier.fillMaxWidth().padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KoakademyMark(size = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "KOAKADEMY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 11.sp,
                                letterSpacing = 2.4.sp,
                            ),
                            color = colors.onSurface,
                        )
                        Text(
                            text = "Office of the bursar",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                            ),
                            color = colors.onSurfaceVariant,
                        )
                    }
                    RubberStamp(
                        text = "${(data.paidFraction * 100).roundToInt()}% paid",
                    )
                }

                Spacer(Modifier.height(9.dp))
                PerforationLine()
                Spacer(Modifier.height(11.dp))

                Text(
                    text = "STATEMENT OF ACCOUNT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 2.sp,
                    ),
                    color = colors.onSurfaceVariant,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = data.tuitionBalance,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 38.sp,
                        lineHeight = 42.sp,
                        letterSpacing = (-1).sp,
                    ),
                    color = colors.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${data.tuitionDue.uppercase()} · PAYABLE AT THE CASHIER OR ONLINE",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                    ),
                    color = colors.secondary,
                )

                Spacer(Modifier.height(18.dp))
                DashedRule()
                Spacer(Modifier.height(10.dp))

                ReceiptRow(label = "Student", value = name)
                ReceiptRow(label = "Account", value = "${data.section} · ${data.yearLevel}")
                ReceiptRow(label = "Term", value = "1ST SEM · A.Y. 2026–27")
                ReceiptRow(label = "Assessment", value = formatMoney(data.tuitionTotal))
                ReceiptRow(label = "Payments", value = formatMoney(data.tuitionPaid))
                Spacer(Modifier.height(4.dp))
                PerforationLine()
                Spacer(Modifier.height(6.dp))
                ReceiptRow(
                    label = "Balance due",
                    value = data.tuitionBalance,
                    emphasize = true,
                    valueColor = colors.secondary,
                )

                Spacer(Modifier.height(18.dp))
                ReceiptProgress(fraction = data.paidFraction)
                Spacer(Modifier.height(6.dp))
                Row {
                    Text(
                        text = "${(data.paidFraction * 100).roundToInt()}% SETTLED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            letterSpacing = 1.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${formatMoney(data.tuitionTotal - data.tuitionPaid)} OUTSTANDING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            letterSpacing = 1.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(20.dp))
                BarcodeStrip()
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "REF ${data.payments.firstOrNull()?.reference ?: "OR-2026-09114"} · " +
                        "PRESENT THIS SLIP FOR CORRECTIONS",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp,
                    ),
                    color = colors.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        Spacer(Modifier.height(30.dp))
        SectionIndex(number = "01", title = "Assessment", trailing = "1ST SEMESTER")
        Spacer(Modifier.height(12.dp))
        data.fees.forEach { fee ->
            ReceiptRow(label = fee.label, value = fee.amount)
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = colors.outlineVariant)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "TOTAL ASSESSMENT",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatMoney(data.tuitionTotal),
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                color = colors.onSurface,
            )
        }

        Spacer(Modifier.height(30.dp))
        SectionIndex(number = "02", title = "Payments posted", trailing = "${data.payments.size} RECEIPTS")
        Spacer(Modifier.height(14.dp))
        data.payments.forEach { payment -> PaymentEntry(payment = payment) }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Payments post within one banking day. Bring your reference number to the " +
                "accounting office for corrections or refunds.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun DashedRule() {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = colors.outlineVariant,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
            ),
        )
    }
}

@Composable
private fun ReceiptProgress(fraction: Float) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) {
        val centerY = size.height / 2f
        drawLine(
            color = colors.outlineVariant,
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 5.dp.toPx(),
        )
        repeat(11) { tick ->
            val x = size.width * (tick / 10f)
            drawLine(
                color = colors.surfaceContainerLowest,
                start = Offset(x, centerY - 5.dp.toPx()),
                end = Offset(x, centerY + 5.dp.toPx()),
                strokeWidth = 1.5.dp.toPx(),
            )
        }
        val reached = size.width * fraction.coerceIn(0f, 1f)
        drawLine(
            color = colors.secondary,
            start = Offset(0f, centerY),
            end = Offset(reached, centerY),
            strokeWidth = 5.dp.toPx(),
        )
    }
}

@Composable
private fun PaymentEntry(payment: Payment) {
    val colors = MaterialTheme.colorScheme
    val dateParts = payment.date.split(", ").firstOrNull()?.split(" ") ?: emptyList()
    val month = dateParts.getOrNull(0)?.uppercase() ?: ""
    val day = dateParts.getOrNull(1) ?: ""
    val shape = MaterialTheme.shapes.extraSmall

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(bottom = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .width(44.dp)
                .clip(shape)
                .background(colors.surfaceContainerHigh)
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                ),
                color = colors.onSurface,
            )
            Text(
                text = month,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 7.sp,
                    letterSpacing = 1.sp,
                ),
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = payment.method,
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = payment.reference,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = colors.onSurfaceVariant,
            )
        }
        Text(
            text = payment.amount,
            style = MaterialTheme.typography.titleSmall.copy(fontFamily = FontFamily.Monospace),
            color = colors.onSurface,
        )
    }
    HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.6f))
}

/** Minutes between two "8:00 AM" style times, used to scale timetable blocks. */
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

/** 1.51, 1.50 — ratings are always shown with two decimals. */
private fun formatRating(value: Double): String {
    val rounded = (value * 100).roundToInt()
    val whole = rounded / 100
    val cents = (rounded % 100).toString().padStart(2, '0')
    return "$whole.$cents"
}

/** ₱30,150.00 — grouped pesos without a platform formatter. */
private fun formatMoney(value: Double): String {
    val whole = value.toLong()
    val cents = ((value - whole) * 100).roundToInt().toString().padStart(2, '0')
    val grouped = whole.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return "₱$grouped.$cents"
}
