package com.example.myapplication.auth

/** How the app should resolve its light or dark appearance. */
enum class ThemePreference(val label: String) {
    System("System"),
    Light("Light"),
    Dark("Dark"),
}

/**
 * Small key/value contract the UI uses to remember where the user left off and which
 * settings they chose.
 *
 * Implementations live in the platform modules, which keeps the shared UI free of
 * platform APIs while still letting the first-start onboarding appear only once.
 */
interface AppPreferences {

    /** True once the intro carousel has been completed or skipped. */
    var hasCompletedOnboarding: Boolean

    /** Student ID or institutional email of the signed-in user, or null when signed out. */
    var signedInAccount: String?

    /** Follow the device, or force light/dark. */
    var themePreference: ThemePreference

    /** Notify when new grades are posted. */
    var notifyGrades: Boolean

    /** Notify for registrar and college announcements. */
    var notifyAnnouncements: Boolean

    /** Notify ahead of tuition due dates. */
    var notifyDues: Boolean
}

/** Volatile implementation used by previews and tests. */
class InMemoryAppPreferences(
    override var hasCompletedOnboarding: Boolean = false,
    override var signedInAccount: String? = null,
    override var themePreference: ThemePreference = ThemePreference.System,
    override var notifyGrades: Boolean = true,
    override var notifyAnnouncements: Boolean = true,
    override var notifyDues: Boolean = true,
) : AppPreferences
