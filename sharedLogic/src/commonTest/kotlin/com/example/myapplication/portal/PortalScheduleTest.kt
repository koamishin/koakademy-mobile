package com.example.myapplication.portal

import kotlin.test.Test
import kotlin.test.assertEquals

class PortalScheduleTest {

    @Test
    fun studentSessionsRunInClockOrder() {
        val thursday = SampleStudentPortal.sessionsFor(ClassDay.Thu)

        assertEquals(listOf("8:00 AM", "10:00 AM"), thursday.map { it.second.start })
        assertEquals(listOf("MATH 102", "GE 105"), thursday.map { it.first.code })
    }

    @Test
    fun facultySessionsRunInClockOrder() {
        val thursday = SampleFacultyPortal.sessionsFor(ClassDay.Thu)

        assertEquals(listOf("10:00 AM", "1:00 PM", "2:30 PM"), thursday.map { it.second.start })
    }

    @Test
    fun generalAverageIsWeightedByUnits() {
        // 25.75 rating points over 17 units.
        assertEquals(1.51, SampleStudentPortal.gpa, 0.005)
        assertEquals(17, SampleStudentPortal.units)
    }

    @Test
    fun facultyTotalsMatchTheirSections() {
        assertEquals(107, SampleFacultyPortal.studentCount)
        assertEquals(4, SampleFacultyPortal.pendingGrading)
    }
}
