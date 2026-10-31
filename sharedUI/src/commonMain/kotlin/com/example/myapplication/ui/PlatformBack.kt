package com.example.myapplication.ui

import androidx.compose.runtime.Composable

/**
 * Routes the system back gesture to in-app navigation (profile, class roster) instead of
 * closing the app. Implemented per platform because only Android has a system back button.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
