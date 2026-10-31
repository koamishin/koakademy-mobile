package com.example.myapplication.portal

data class RosterStudent(
    val name: String,
    val id: String,
    val rating: String,
    val attendancePercent: Int,
    val standing: RosterStanding,
)

enum class RosterStanding(val label: String) {
    Good("On track"),
    Watch("Watch"),
    AtRisk("At risk"),
}

data class FacultySection(
    val code: String,
    val title: String,
    val program: String,
    val yearLevel: String,
    val schedule: String,
    val room: String,
    val enrolled: Int,
    val graded: Int,
    val average: String,
    val atRisk: Int,
    val roster: List<RosterStudent>,
    val sessionsToday: List<ClassSession> = emptyList(),
) {
    val gradedFraction: Float get() = if (enrolled == 0) 0f else graded.toFloat() / enrolled
}

enum class GradingStatus(val label: String) {
    Awaiting("Awaiting grade"),
    Draft("Draft saved"),
    Returned("Returned"),
}

data class GradingItem(
    val sectionCode: String,
    val activity: String,
    val student: String,
    val submitted: String,
    val status: GradingStatus,
    val attachment: String,
    val note: String,
)

data class FacultyMessage(
    val from: String,
    val fromRole: String,
    val subject: String,
    val preview: String,
    val body: String,
    val sent: String,
    val unread: Boolean,
)

data class FacultyDeadline(val label: String, val due: String)

/**
 * Sample faculty load used until the app is connected to an institution's KoAkademy server.
 * Rosters show a first page of each section; the web panel holds the full list.
 */
data class FacultyPortalData(
    val term: String,
    val department: String,
    val today: ClassDay,
    val sections: List<FacultySection>,
    val gradingQueue: List<GradingItem>,
    val messages: List<FacultyMessage>,
    val deadlines: List<FacultyDeadline>,
    val termWeek: Int = 12,
    val termWeeks: Int = 18,
) {
    val studentCount: Int get() = sections.sumOf { it.enrolled }
    val pendingGrading: Int get() = gradingQueue.count { it.status != GradingStatus.Returned }
    val unreadMessages: Int get() = messages.count { it.unread }

    fun sessionsFor(day: ClassDay): List<Pair<FacultySection, ClassSession>> =
        sections.flatMap { section ->
            section.sessionsToday.filter { it.day == day }.map { section to it }
        }.sortedBy { it.second.startMinutes }
}

val SampleFacultyPortal = FacultyPortalData(
    term = "First Semester · A.Y. 2026–2027",
    department = "Department of Computer Science",
    today = ClassDay.Thu,
    sections = listOf(
        FacultySection(
            code = "CS 201",
            title = "Data Structures",
            program = "BS Computer Science",
            yearLevel = "2nd Year",
            schedule = "MWF 10:00–11:00 AM",
            room = "Room 304",
            enrolled = 42,
            graded = 38,
            average = "1.86",
            atRisk = 4,
            sessionsToday = listOf(ClassSession(ClassDay.Thu, "10:00 AM", "11:00 AM", "Room 304")),
            roster = listOf(
                RosterStudent("Ada Santos", "2024-00123", "1.50", 96, RosterStanding.Good),
                RosterStudent("Miguel Reyes", "2024-00104", "1.75", 92, RosterStanding.Good),
                RosterStudent("Kim Dela Peña", "2024-00118", "2.00", 86, RosterStanding.Watch),
                RosterStudent("Josefa Lim", "2024-00131", "1.25", 98, RosterStanding.Good),
                RosterStudent("Nathan Ocampo", "2024-00140", "2.50", 74, RosterStanding.Watch),
                RosterStudent("Bea Villanueva", "2024-00152", "3.00", 61, RosterStanding.AtRisk),
                RosterStudent("Paolo Gutierrez", "2024-00167", "1.75", 90, RosterStanding.Good),
                RosterStudent("Hannah Mercado", "2024-00173", "2.25", 83, RosterStanding.Good),
            ),
        ),
        FacultySection(
            code = "CS 210",
            title = "Object-Oriented Programming",
            program = "BS Computer Science",
            yearLevel = "2nd Year",
            schedule = "MW 1:00–2:30 PM",
            room = "Lab 2",
            enrolled = 38,
            graded = 31,
            average = "2.02",
            atRisk = 5,
            sessionsToday = listOf(ClassSession(ClassDay.Thu, "1:00 PM", "2:30 PM", "Lab 2")),
            roster = listOf(
                RosterStudent("Rafael Bautista", "2024-00201", "1.50", 95, RosterStanding.Good),
                RosterStudent("Trina Salazar", "2024-00214", "1.75", 88, RosterStanding.Good),
                RosterStudent("Ivan Flores", "2024-00229", "2.75", 68, RosterStanding.AtRisk),
                RosterStudent("Mika Domingo", "2024-00233", "1.50", 97, RosterStanding.Good),
                RosterStudent("Gabriel Tan", "2024-00248", "2.00", 81, RosterStanding.Watch),
                RosterStudent("Sofia Navarro", "2024-00251", "1.25", 99, RosterStanding.Good),
            ),
        ),
        FacultySection(
            code = "CS 314",
            title = "Design and Analysis of Algorithms",
            program = "BS Computer Science",
            yearLevel = "3rd Year",
            schedule = "TTh 2:30–4:00 PM",
            room = "Room 410",
            enrolled = 27,
            graded = 27,
            average = "1.74",
            atRisk = 1,
            sessionsToday = listOf(ClassSession(ClassDay.Thu, "2:30 PM", "4:00 PM", "Room 410")),
            roster = listOf(
                RosterStudent("Angelo Pascual", "2023-00087", "1.50", 94, RosterStanding.Good),
                RosterStudent("Liza Fernandez", "2023-00092", "1.25", 98, RosterStanding.Good),
                RosterStudent("Dominic Sy", "2023-00104", "2.00", 85, RosterStanding.Good),
                RosterStudent("Patricia Uy", "2023-00119", "1.75", 91, RosterStanding.Good),
                RosterStudent("Karlo Mendoza", "2023-00122", "2.75", 70, RosterStanding.AtRisk),
                RosterStudent("Nadine Co", "2023-00138", "1.50", 96, RosterStanding.Good),
            ),
        ),
    ),
    gradingQueue = listOf(
        GradingItem(
            sectionCode = "CS 201",
            activity = "Laboratory 4 · Sorting",
            student = "Ada Santos",
            submitted = "Submitted Oct 27",
            status = GradingStatus.Awaiting,
            attachment = "lab4_sorting.py · 12 KB",
            note = "Implemented merge sort and quicksort. Both pass the sample cases; " +
                "quicksort is slower on the 10k input.",
        ),
        GradingItem(
            sectionCode = "CS 201",
            activity = "Problem set 3 · Trees",
            student = "Kim Dela Peña",
            submitted = "Submitted Oct 26",
            status = GradingStatus.Awaiting,
            attachment = "problem_set_3.pdf · 480 KB",
            note = "Answers for items 1–5. I was unsure about the height of a node with " +
                "two children — please check item 4.",
        ),
        GradingItem(
            sectionCode = "CS 210",
            activity = "Machine problem 2 · Inheritance",
            student = "Ivan Flores",
            submitted = "Submitted Oct 25",
            status = GradingStatus.Awaiting,
            attachment = "machine_problem_2.zip · 1.2 MB",
            note = "Refactored the shapes into one base class. The area() override is still " +
                "duplicated in two places.",
        ),
        GradingItem(
            sectionCode = "CS 210",
            activity = "Machine problem 2 · Inheritance",
            student = "Sofia Navarro",
            submitted = "Submitted Oct 25",
            status = GradingStatus.Draft,
            attachment = "machine_problem_2.pdf · 620 KB",
            note = "Draft saved with a provisional 1.50. Comment on the interface section " +
                "is still to be written.",
        ),
        GradingItem(
            sectionCode = "CS 314",
            activity = "Final paper · Approximation algorithms",
            student = "Karlo Mendoza",
            submitted = "Submitted Oct 24",
            status = GradingStatus.Returned,
            attachment = "final_paper.pdf · 2.4 MB",
            note = "Returned with remarks on the approximation proof and the running time " +
                "table.",
        ),
    ),
    messages = listOf(
        FacultyMessage(
            from = "Office of the Registrar",
            fromRole = "Registrar",
            subject = "Final grades due Nov 20",
            preview = "Encoding of final grades for the first semester closes on Nov 20 at 5:00 PM.",
            body = "Encoding of final grades for the first semester closes on Nov 20 at 5:00 PM. " +
                "Please complete all pending ratings in your gradebook before the deadline. " +
                "Late submissions require a signed request from your department chair.",
            sent = "Oct 27",
            unread = true,
        ),
        FacultyMessage(
            from = "Dean, College of Computing",
            fromRole = "Dean",
            subject = "Faculty meeting — Thursday, 3:00 PM",
            preview = "Agenda: second semester load assignment and lab equipment requests.",
            body = "The college faculty meeting is on Thursday at 3:00 PM in the AVR. " +
                "Agenda: second semester load assignment, laboratory equipment requests, and " +
                "the accreditation self-study. Attendance is required.",
            sent = "Oct 26",
            unread = true,
        ),
        FacultyMessage(
            from = "Ada Santos",
            fromRole = "Student, CS 201",
            subject = "Re: Machine problem 2 extension",
            preview = "Good day, Prof. Cruz. May I ask if the deadline can be moved to Friday?",
            body = "Good day, Prof. Cruz. May I ask if the deadline for Machine problem 2 can be " +
                "moved to Friday? Our team is still completing the test cases and we would like " +
                "to submit a working build. Thank you po.",
            sent = "Oct 25",
            unread = false,
        ),
        FacultyMessage(
            from = "Human Resources",
            fromRole = "HR Office",
            subject = "Semestral break leave filing",
            preview = "Leave applications for the semestral break are open until Nov 28.",
            body = "Leave applications for the semestral break are open until Nov 28. File through " +
                "the HR portal and route the application to your department chair for approval.",
            sent = "Oct 20",
            unread = false,
        ),
    ),
    deadlines = listOf(
        FacultyDeadline("Final grades encoding closes", "Nov 20 · 5:00 PM"),
        FacultyDeadline("Second semester load confirmation", "Nov 6"),
        FacultyDeadline("Accreditation self-study upload", "Nov 12"),
    ),
)
