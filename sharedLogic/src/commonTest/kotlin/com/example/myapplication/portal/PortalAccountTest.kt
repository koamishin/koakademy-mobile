package com.example.myapplication.portal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PortalAccountTest {

    @Test
    fun demoStudentAccountOpensStudentPortal() {
        val account = PortalDirectory.resolve("2024-00123")

        assertEquals(PortalRole.Student, account.role)
        assertEquals("Ada Santos", account.name)
        assertEquals("Ada", account.greetingName())
        assertEquals("AS", account.avatarLabel())
    }

    @Test
    fun demoFacultyAccountOpensFacultyPortal() {
        val account = PortalDirectory.resolve("F-2019-004")

        assertEquals(PortalRole.Faculty, account.role)
        assertEquals("Marisol Cruz", account.name)
        assertEquals("Prof. Cruz", account.greetingName())
    }

    @Test
    fun institutionalEmailIsMatchedRegardlessOfCase() {
        val account = PortalDirectory.resolve("  M.CRUZ@School.edu ")

        assertEquals(PortalRole.Faculty, account.role)
        assertEquals("Marisol Cruz", account.name)
    }

    @Test
    fun facultyEmployeeIdIsInferredWhenNotInDemoDirectory() {
        val account = PortalDirectory.resolve("F-2021-118")

        assertEquals(PortalRole.Faculty, account.role)
        assertNull(account.name)
        assertEquals("Faculty member · Institution account", account.affiliation)
    }

    @Test
    fun unknownStudentEmailKeepsItsReadableName() {
        val account = PortalDirectory.resolve("juan.dela.cruz@school.edu")

        assertEquals(PortalRole.Student, account.role)
        assertEquals("Juan Dela", account.name)
        assertEquals("JD", account.avatarLabel())
    }
}
