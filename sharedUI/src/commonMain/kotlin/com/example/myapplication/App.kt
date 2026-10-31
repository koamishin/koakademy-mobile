package com.example.myapplication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.auth.AppPreferences
import com.example.myapplication.auth.InMemoryAppPreferences
import com.example.myapplication.auth.ThemePreference
import com.example.myapplication.portal.PortalDirectory
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.onboarding.OnboardingScreen
import com.example.myapplication.ui.portal.PortalScreen
import com.example.myapplication.ui.profile.ProfileScreen
import com.example.myapplication.ui.theme.AppTheme

private sealed interface Destination {
    data object Onboarding : Destination
    data object Login : Destination
    data class Home(val account: String) : Destination
    data class Profile(val account: String) : Destination
}

/**
 * Holds the current screen and mirrors session changes into [AppPreferences] so the
 * onboarding tour only appears on the first start and the signed-in account survives restarts.
 */
private class AppSession(private val preferences: AppPreferences) {

    var destination by mutableStateOf(initialDestination(preferences))
        private set

    fun completeOnboarding() {
        preferences.hasCompletedOnboarding = true
        destination = Destination.Login
    }

    fun signIn(account: String) {
        preferences.signedInAccount = account
        destination = Destination.Home(account)
    }

    fun signOut() {
        preferences.signedInAccount = null
        destination = Destination.Login
    }

    fun openProfile(account: String) {
        destination = Destination.Profile(account)
    }

    fun closeProfile(account: String) {
        destination = Destination.Home(account)
    }
}

private fun initialDestination(preferences: AppPreferences): Destination {
    val account = preferences.signedInAccount
    return when {
        account != null -> Destination.Home(account)
        !preferences.hasCompletedOnboarding -> Destination.Onboarding
        else -> Destination.Login
    }
}

@Composable
fun App(preferences: AppPreferences) {
    var themePreference by remember { mutableStateOf(preferences.themePreference) }
    val darkTheme = when (themePreference) {
        ThemePreference.System -> isSystemInDarkTheme()
        ThemePreference.Light -> false
        ThemePreference.Dark -> true
    }

    AppTheme(darkTheme = darkTheme) {
        val session = remember(preferences) { AppSession(preferences) }

        AnimatedContent(
            targetState = session.destination,
            transitionSpec = {
                if (targetState is Destination.Profile) {
                    (slideInHorizontally(tween(260)) { width -> width / 8 } + fadeIn(tween(200)))
                        .togetherWith(fadeOut(tween(140)))
                } else {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(160))
                }
            },
            label = "appScreen",
        ) { destination ->
            when (destination) {
                Destination.Onboarding -> OnboardingScreen(onFinished = session::completeOnboarding)
                Destination.Login -> LoginScreen(onSignIn = session::signIn)
                is Destination.Home -> PortalScreen(
                    account = remember(destination.account) { PortalDirectory.resolve(destination.account) },
                    onOpenProfile = { session.openProfile(destination.account) },
                )

                is Destination.Profile -> ProfileScreen(
                    account = remember(destination.account) { PortalDirectory.resolve(destination.account) },
                    preferences = preferences,
                    themePreference = themePreference,
                    onThemePreferenceChange = { chosen ->
                        preferences.themePreference = chosen
                        themePreference = chosen
                    },
                    onBack = { session.closeProfile(destination.account) },
                    onSignOut = session::signOut,
                )
            }
        }
    }
}

@Preview(name = "First start")
@Composable
private fun OnboardingPreview() {
    val preferences = remember { InMemoryAppPreferences() }
    App(preferences)
}

@Preview(name = "Login")
@Composable
private fun LoginPreview() {
    val preferences = remember { InMemoryAppPreferences(hasCompletedOnboarding = true) }
    App(preferences)
}

@Preview(name = "Student portal")
@Composable
private fun StudentPortalPreview() {
    val preferences = remember {
        InMemoryAppPreferences(
            hasCompletedOnboarding = true,
            signedInAccount = "2024-00123",
        )
    }
    App(preferences)
}

@Preview(name = "Faculty portal")
@Composable
private fun FacultyPortalPreview() {
    val preferences = remember {
        InMemoryAppPreferences(
            hasCompletedOnboarding = true,
            signedInAccount = "F-2019-004",
        )
    }
    App(preferences)
}
