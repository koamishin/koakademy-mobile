package com.example.myapplication

import android.content.Context
import com.example.myapplication.auth.AppPreferences
import com.example.myapplication.auth.ThemePreference

/**
 * Persists the onboarding flag, the signed-in user and the profile settings so they
 * survive app restarts. That is what makes the intro carousel appear only on the first start.
 */
class SharedPreferencesAppPreferences(context: Context) : AppPreferences {

    private val storage = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override var hasCompletedOnboarding: Boolean
        get() = storage.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) {
            storage.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()
        }

    override var signedInAccount: String?
        get() = storage.getString(KEY_SIGNED_IN_USER, null)
        set(value) {
            storage.edit().putString(KEY_SIGNED_IN_USER, value).apply()
        }

    override var themePreference: ThemePreference
        get() = storage.getString(KEY_THEME, null)
            ?.let { stored -> ThemePreference.entries.firstOrNull { it.name == stored } }
            ?: ThemePreference.System
        set(value) {
            storage.edit().putString(KEY_THEME, value.name).apply()
        }

    override var notifyGrades: Boolean
        get() = storage.getBoolean(KEY_NOTIFY_GRADES, true)
        set(value) {
            storage.edit().putBoolean(KEY_NOTIFY_GRADES, value).apply()
        }

    override var notifyAnnouncements: Boolean
        get() = storage.getBoolean(KEY_NOTIFY_ANNOUNCEMENTS, true)
        set(value) {
            storage.edit().putBoolean(KEY_NOTIFY_ANNOUNCEMENTS, value).apply()
        }

    override var notifyDues: Boolean
        get() = storage.getBoolean(KEY_NOTIFY_DUES, true)
        set(value) {
            storage.edit().putBoolean(KEY_NOTIFY_DUES, value).apply()
        }

    private companion object {
        const val FILE_NAME = "app_preferences"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_SIGNED_IN_USER = "signed_in_user_email"
        const val KEY_THEME = "theme_preference"
        const val KEY_NOTIFY_GRADES = "notify_grades"
        const val KEY_NOTIFY_ANNOUNCEMENTS = "notify_announcements"
        const val KEY_NOTIFY_DUES = "notify_dues"
    }
}
