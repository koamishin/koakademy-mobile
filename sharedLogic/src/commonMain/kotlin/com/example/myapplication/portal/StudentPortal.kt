package com.example.myapplication.portal

import kotlin.math.round

/** Teaching days used by the schedule views. */
enum class ClassDay(val short: String, val long: String) {
    Mon("Mon", "Monday"),
    Tue("Tue", "Tuesday"),
    Wed("Wed", "Wednesday"),
    Thu("Thu", "Thursday"),
    Fri("Fri", "Friday"),
}

data class ClassSession(
    val day: ClassDay,
    val start: String,
    val end: String,
    val room: String,
) {
    /** Minutes since midnight, so sessions sort 8:00 AM before 10:00 AM. */
    val startMinutes: Int get() = parseClock(start)
}

private fun parseClock(value: String): Int {
    val clock = value.substringBefore(' ')
    val meridiem = value.substringAfter(' ', "")
    val hours = clock.substringBefore(':').toIntOrNull() ?: 0
    val minutes = clock.substringAfter(':', "0").toIntOrNull() ?: 0
    val normalized = hours % 12 + if (meridiem.equals("PM", ignoreCase = true)) 12 else 0
    return normalized * 60 + minutes
}

data class Course(
    val code: String,
    val title: String,
    val units: Int,
    val instructor: String,
    val sessions: List<ClassSession>,
    val midtermRating: String,
    val finalRating: String,
    val finalGrade: String,
    val attendancePercent: Int,
)

data class Announcement(
    val source: String,
    val title: String,
    val body: String,
    val date: String,
)

data class FeeItem(val label: String, val amount: String)

data class Payment(val date: String, val method: String, val reference: String, val amount: String)

/**
 * Sample student term used until the app is connected to an institution's KoAkademy server.
 * Ratings follow the Philippine 1.00–5.00 scale, where lower is better.
 */
data class StudentPortalData(
    val term: String,
    val program: String,
    val yearLevel: String,
    val section: String,
    val today: ClassDay,
    val courses: List<Course>,
    val tuitionTotal: Double,
    val tuitionPaid: Double,
    val tuitionBalance: String,
    val tuitionDue: String,
    val fees: List<FeeItem>,
    val payments: List<Payment>,
    val announcements: List<Announcement>,
    val termWeek: Int = 12,
    val termWeeks: Int = 18,
    val dayDates: Map<ClassDay, String> = mapOf(
        ClassDay.Mon to "26",
        ClassDay.Tue to "27",
        ClassDay.Wed to "28",
        ClassDay.Thu to "29",
        ClassDay.Fri to "30",
    ),
    val holdNote: String? = null,
) {
    val units: Int get() = courses.sumOf { it.units }
    val paidFraction: Float get() = (tuitionPaid / tuitionTotal).toFloat()
    val gpa: Double
        get() {
            val weighted = courses.sumOf { it.units * it.finalGrade.toDouble() }
            return round(weighted / units * 100) / 100
        }

    fun sessionsFor(day: ClassDay): List<Pair<Course, ClassSession>> =
        courses.flatMap { course ->
            course.sessions.filter { it.day == day }.map { course to it }
        }.sortedBy { it.second.startMinutes }
}

/** Fixed "today" so the preview reads consistently wherever it is opened. */
val SampleStudentPortal = StudentPortalData(
    term = "First Semester · A.Y. 2026–2027",
    program = "BS Computer Science",
    yearLevel = "2nd Year",
    section = "CS-2A",
    today = ClassDay.Thu,
    courses = listOf(
        Course(
            code = "CS 201",
            title = "Data Structures",
            units = 3,
            instructor = "Prof. Marisol Cruz",
            sessions = listOf(
                ClassSession(ClassDay.Mon, "10:00 AM", "11:00 AM", "Room 304"),
                ClassSession(ClassDay.Wed, "10:00 AM", "11:00 AM", "Room 304"),
                ClassSession(ClassDay.Fri, "10:00 AM", "11:00 AM", "Room 304"),
            ),
            midtermRating = "1.75",
            finalRating = "1.50",
            finalGrade = "1.50",
            attendancePercent = 96,
        ),
        Course(
            code = "CS 210",
            title = "Object-Oriented Programming",
            units = 3,
            instructor = "Prof. Marisol Cruz",
            sessions = listOf(
                ClassSession(ClassDay.Mon, "1:00 PM", "2:30 PM", "Lab 2"),
                ClassSession(ClassDay.Wed, "1:00 PM", "2:30 PM", "Lab 2"),
            ),
            midtermRating = "1.50",
            finalRating = "1.25",
            finalGrade = "1.25",
            attendancePercent = 92,
        ),
        Course(
            code = "MATH 102",
            title = "Discrete Mathematics",
            units = 3,
            instructor = "Dr. Ramon Bautista",
            sessions = listOf(
                ClassSession(ClassDay.Tue, "8:00 AM", "9:30 AM", "Room 201"),
                ClassSession(ClassDay.Thu, "8:00 AM", "9:30 AM", "Room 201"),
            ),
            midtermRating = "2.00",
            finalRating = "1.75",
            finalGrade = "1.75",
            attendancePercent = 89,
        ),
        Course(
            code = "GE 105",
            title = "Ethics and Society",
            units = 3,
            instructor = "Dr. Liza Reyes",
            sessions = listOf(
                ClassSession(ClassDay.Tue, "10:00 AM", "11:30 AM", "Room 118"),
                ClassSession(ClassDay.Thu, "10:00 AM", "11:30 AM", "Room 118"),
            ),
            midtermRating = "1.50",
            finalRating = "1.50",
            finalGrade = "1.50",
            attendancePercent = 94,
        ),
        Course(
            code = "FIL 101",
            title = "Komunikasyon sa Akademikong Filipino",
            units = 3,
            instructor = "G. Noel Aquino",
            sessions = listOf(
                ClassSession(ClassDay.Wed, "3:00 PM", "4:00 PM", "Room 212"),
                ClassSession(ClassDay.Fri, "3:00 PM", "4:00 PM", "Room 212"),
            ),
            midtermRating = "1.75",
            finalRating = "1.75",
            finalGrade = "1.75",
            attendancePercent = 91,
        ),
        Course(
            code = "PE 102",
            title = "Physical Fitness",
            units = 2,
            instructor = "Coach Joel Dela Cruz",
            sessions = listOf(
                ClassSession(ClassDay.Fri, "4:00 PM", "5:00 PM", "Gymnasium"),
            ),
            midtermRating = "1.25",
            finalRating = "1.25",
            finalGrade = "1.25",
            attendancePercent = 98,
        ),
    ),
    tuitionTotal = 42_600.0,
    tuitionPaid = 30_150.0,
    tuitionBalance = "₱12,450.00",
    tuitionDue = "Due Nov 15",
    fees = listOf(
        FeeItem("Tuition (17 units)", "₱32,000.00"),
        FeeItem("Miscellaneous fees", "₱6,400.00"),
        FeeItem("Laboratory fees", "₱3,200.00"),
        FeeItem("Athletic and cultural fee", "₱1,000.00"),
    ),
    payments = listOf(
        Payment("Sep 30, 2026", "University cashier", "OR-2026-09114", "₱10,150.00"),
        Payment("Aug 12, 2026", "Bank transfer", "BT-884201", "₱20,000.00"),
    ),
    announcements = listOf(
        Announcement(
            source = "Office of the Registrar",
            title = "Second semester enrollment opens Nov 4",
            body = "Continuing students in CS-2A may enroll online from Nov 4 to Nov 8. Clear any " +
                "hold at the registrar before your slot is released.",
            date = "Oct 26",
        ),
        Announcement(
            source = "College of Computing",
            title = "Prelim grades are now posted",
            body = "Prelim ratings for all CS and MATH subjects have been released. Contact your " +
                "instructor within five days for corrections.",
            date = "Oct 22",
        ),
        Announcement(
            source = "University Library",
            title = "Library extends hours during finals",
            body = "The main library is open until 9:00 PM on weekdays from Oct 27 to Nov 14.",
            date = "Oct 20",
        ),
    ),
    holdNote = null,
)
