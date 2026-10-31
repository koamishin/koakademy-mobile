package com.example.myapplication.portal

/** Which portal an institutional account opens. */
enum class PortalRole(val label: String, val portalName: String) {
    Student("Student", "Student portal"),
    Faculty("Faculty", "Faculty portal"),
}

/**
 * A signed-in KoAkademy account. In a connected build this comes from the institution's
 * server; for now it is resolved locally from the ID or email that was entered.
 */
data class PortalAccount(
    val id: String,
    val name: String?,
    val role: PortalRole,
    val affiliation: String,
)

/**
 * Maps the account entered on the login screen to a role. Known demo accounts carry a
 * readable name; anything else is inferred, with faculty employee IDs starting with "F".
 */
object PortalDirectory {

    private val demoAccounts: Map<String, PortalAccount> = listOf(
        PortalAccount(
            id = "2024-00123",
            name = "Ada Santos",
            role = PortalRole.Student,
            affiliation = "BS Computer Science · 2nd Year · CS-2A",
        ),
        PortalAccount(
            id = "ada.santos@school.edu",
            name = "Ada Santos",
            role = PortalRole.Student,
            affiliation = "BS Computer Science · 2nd Year · CS-2A",
        ),
        PortalAccount(
            id = "F-2019-004",
            name = "Marisol Cruz",
            role = PortalRole.Faculty,
            affiliation = "Instructor · Department of Computer Science",
        ),
        PortalAccount(
            id = "m.cruz@school.edu",
            name = "Marisol Cruz",
            role = PortalRole.Faculty,
            affiliation = "Instructor · Department of Computer Science",
        ),
    ).associateBy { it.id.lowercase() }

    fun resolve(account: String): PortalAccount {
        val trimmed = account.trim()
        demoAccounts[trimmed.lowercase()]?.let { return it }

        val role = if (looksLikeFaculty(trimmed)) PortalRole.Faculty else PortalRole.Student
        return PortalAccount(
            id = trimmed,
            name = readableName(trimmed),
            role = role,
            affiliation = when (role) {
                PortalRole.Student -> "Enrolled student · Institution account"
                PortalRole.Faculty -> "Faculty member · Institution account"
            },
        )
    }

    /** Employee IDs on KoAkademy start with F, e.g. F-2019-004. */
    private fun looksLikeFaculty(account: String): Boolean =
        Regex("^F[-_]?\\d", RegexOption.IGNORE_CASE).containsMatchIn(account)
}

/**
 * "ada.santos@school.edu" becomes "Ada Santos". Institutional IDs such as 2024-00123 or
 * F-2019-004 carry no readable name, so they resolve to null.
 */
fun readableName(account: String): String? {
    if (!account.contains('@')) return null
    val local = account.substringBefore('@')
    if (local.none { it.isLetter() }) return null
    return local.split('.', '_', '-')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(" ") { part -> part.replaceFirstChar { it.uppercaseChar() } }
}

/** First name for students, "Prof. <surname>" for faculty. */
fun PortalAccount.greetingName(): String? {
    val full = name ?: return null
    return when (role) {
        PortalRole.Student -> full.substringBefore(' ')
        PortalRole.Faculty -> "Prof. ${full.substringAfterLast(' ')}"
    }
}

/** One or two letters for the header avatar. */
fun PortalAccount.avatarLabel(): String {
    val full = name
    if (full != null) {
        val initials = full.split(' ')
            .filter { it.isNotBlank() }
            .map { it.first().uppercaseChar() }
        return initials.take(2).joinToString("")
    }
    return id.filter { it.isLetterOrDigit() }.takeLast(2).uppercase().ifEmpty { "K" }
}
