package com.example.myapplication.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.auth.AppPreferences
import com.example.myapplication.auth.InMemoryAppPreferences
import com.example.myapplication.auth.ThemePreference
import com.example.myapplication.portal.PortalAccount
import com.example.myapplication.portal.PortalDirectory
import com.example.myapplication.ui.PlatformBackHandler
import com.example.myapplication.ui.portal.BarcodeStrip
import com.example.myapplication.ui.portal.DocumentLabel
import com.example.myapplication.ui.portal.MonogramTile
import com.example.myapplication.ui.portal.PageMasthead
import com.example.myapplication.ui.portal.RubberStamp
import com.example.myapplication.ui.portal.SectionIndex
import com.example.myapplication.ui.theme.AppTheme

private enum class ProfileDialog { Password, Sessions, Support, Privacy, About }

/**
 * Account file: the record header, the identity card, then the settings as ruled entries.
 * Everything here is real device state — the appearance choice and the notification
 * switches persist through [AppPreferences].
 */
@Composable
fun ProfileScreen(
    account: PortalAccount,
    preferences: AppPreferences,
    themePreference: ThemePreference,
    onThemePreferenceChange: (ThemePreference) -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var notifyGrades by rememberSaveable { mutableStateOf(preferences.notifyGrades) }
    var notifyAnnouncements by rememberSaveable { mutableStateOf(preferences.notifyAnnouncements) }
    var notifyDues by rememberSaveable { mutableStateOf(preferences.notifyDues) }
    var dialog by rememberSaveable { mutableStateOf<ProfileDialog?>(null) }
    var confirmSignOut by rememberSaveable { mutableStateOf(false) }

    PlatformBackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ProfileTitleBar(account = account, onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .navigationBarsPadding()
                .padding(bottom = 30.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            PageMasthead(
                folio = "Account file · ${account.role.label} portal",
                title = account.name ?: "Institutional account",
                subtitle = account.affiliation,
                meta = account.role.label,
            )

            Spacer(Modifier.height(20.dp))
            IdentityRecord(account = account)

            Spacer(Modifier.height(30.dp))
            SectionIndex(number = "01", title = "Notifications")
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = colors.outlineVariant)
            SwitchEntry(
                title = "Grades posted",
                subtitle = "When an instructor releases a rating",
                checked = notifyGrades,
                onCheckedChange = {
                    notifyGrades = it
                    preferences.notifyGrades = it
                },
            )
            FileHairline()
            SwitchEntry(
                title = "Announcements",
                subtitle = "Registrar and college notices",
                checked = notifyAnnouncements,
                onCheckedChange = {
                    notifyAnnouncements = it
                    preferences.notifyAnnouncements = it
                },
            )
            FileHairline()
            SwitchEntry(
                title = "Payment reminders",
                subtitle = "Tuition due dates and balances",
                checked = notifyDues,
                onCheckedChange = {
                    notifyDues = it
                    preferences.notifyDues = it
                },
            )
            FileHairline()

            Spacer(Modifier.height(28.dp))
            SectionIndex(number = "02", title = "Appearance")
            Spacer(Modifier.height(14.dp))
            ThemeSelector(selected = themePreference, onSelect = onThemePreferenceChange)
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (themePreference == ThemePreference.System) {
                    "Following your device. KoAkademy switches with system dark mode."
                } else {
                    "Always ${themePreference.label.lowercase()}, regardless of your device setting."
                },
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(28.dp))
            SectionIndex(number = "03", title = "Security")
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = colors.outlineVariant)
            LinkEntry(
                icon = Icons.Outlined.Lock,
                title = "Password",
                subtitle = "Resets are handled by your registrar or IT office",
                onClick = { dialog = ProfileDialog.Password },
            )
            FileHairline()
            LinkEntry(
                icon = Icons.Outlined.Devices,
                title = "Active sessions",
                subtitle = "Signed in on this device",
                onClick = { dialog = ProfileDialog.Sessions },
            )
            FileHairline()

            Spacer(Modifier.height(28.dp))
            SectionIndex(number = "04", title = "Support")
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = colors.outlineVariant)
            LinkEntry(
                icon = Icons.Outlined.HelpOutline,
                title = "Help and support",
                subtitle = "Registrar, IT helpdesk and enrollment",
                onClick = { dialog = ProfileDialog.Support },
            )
            FileHairline()
            LinkEntry(
                icon = Icons.Outlined.PrivacyTip,
                title = "Privacy",
                subtitle = "What this build stores on your device",
                onClick = { dialog = ProfileDialog.Privacy },
            )
            FileHairline()
            LinkEntry(
                icon = Icons.Outlined.Info,
                title = "About KoAkademy",
                subtitle = "Version 1.0 · self-hosted by your institution",
                onClick = { dialog = ProfileDialog.About },
            )
            FileHairline()

            Spacer(Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, colors.error.copy(alpha = 0.45f))
                    .clickable { confirmSignOut = true },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SIGN OUT",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                    color = colors.error,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "You'll need your institutional account to sign back in.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(26.dp))
            Text(
                text = "KOAKADEMY MOBILE · 1.0 — SELF-HOSTED BY YOUR INSTITUTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.5.sp,
                    letterSpacing = 1.2.sp,
                ),
                color = colors.outline,
            )
        }
    }

    ProfileMessageDialog(dialog = dialog, onClose = { dialog = null })

    if (confirmSignOut) {
        AlertDialog(
            onDismissRequest = { confirmSignOut = false },
            shape = MaterialTheme.shapes.large,
            containerColor = colors.surface,
            titleContentColor = colors.onSurface,
            textContentColor = colors.onSurfaceVariant,
            title = { Text("Sign out of KoAkademy?") },
            text = { Text("You'll need your institutional account to sign back in.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmSignOut = false
                        onSignOut()
                    },
                ) {
                    Text("Sign out", color = colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSignOut = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

/** Running head for the file: back arrow, section label and the account serial. */
@Composable
private fun ProfileTitleBar(account: PortalAccount, onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth().background(colors.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.onSurface,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
                color = colors.secondary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = account.id.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(end = 20.dp),
            )
        }
        HorizontalDivider(color = colors.outlineVariant)
    }
}

/**
 * The identity card: monogram, status stamp and a record strip in tabular figures, closed by
 * a barcode with the typed serial beneath it so it still works when a scanner doesn't.
 */
@Composable
private fun IdentityRecord(account: PortalAccount) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topEnd = 22.dp),
        color = colors.surfaceContainerLowest,
        border = BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MonogramTile(name = account.name ?: "KoAkademy", size = 46.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    DocumentLabel(text = "Institutional record")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = account.role.portalName,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                    )
                    Text(
                        text = "KoAkademy · Self-hosted",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.onSurfaceVariant,
                    )
                }
                RubberStamp(text = "Active")
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = colors.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                RecordCell("Account", account.id, Modifier.weight(1.15f))
                RecordCell("Role", account.role.label, Modifier.weight(0.9f))
                RecordCell("Status", "Active", Modifier.weight(0.75f))
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = colors.outlineVariant)
            Spacer(Modifier.height(12.dp))

            BarcodeStrip()
            Spacer(Modifier.height(5.dp))
            Text(
                text = "${account.id.uppercase()} · PRESENT FOR CAMPUS ENTRY",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    letterSpacing = 0.6.sp,
                ),
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RecordCell(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 8.sp,
                letterSpacing = 1.3.sp,
            ),
            color = colors.outline,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value.uppercase(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 0.4.sp,
            ),
            color = colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FileHairline() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
}

@Composable
private fun SwitchEntry(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onSecondary,
                checkedTrackColor = colors.secondary,
                uncheckedThumbColor = colors.outline,
                uncheckedTrackColor = colors.surfaceContainerHighest,
                uncheckedBorderColor = colors.outlineVariant,
            ),
        )
    }
}

@Composable
private fun LinkEntry(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(17.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(10.dp))
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = colors.outline,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** Appearance as a set of flush paper tabs — no pills, matching the rest of the file. */
@Composable
private fun ThemeSelector(
    selected: ThemePreference,
    onSelect: (ThemePreference) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .border(1.dp, colors.outlineVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        ThemePreference.entries.forEachIndexed { index, option ->
            val isSelected = option == selected
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxSize()
                        .background(colors.outlineVariant),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(if (isSelected) colors.primary else Color.Transparent)
                    .clickable { onSelect(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                    color = if (isSelected) colors.onPrimary else colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProfileMessageDialog(dialog: ProfileDialog?, onClose: () -> Unit) {
    if (dialog == null) return
    val colors = MaterialTheme.colorScheme
    val (title, body) = when (dialog) {
        ProfileDialog.Password -> "Password" to
            "Your KoAkademy password is managed by your institution. Resets are issued by the " +
            "registrar or IT office; the change form is in the web panel."

        ProfileDialog.Sessions -> "Active sessions" to
            "This phone is the only active session for your account. Signing out removes it " +
            "from this device immediately."

        ProfileDialog.Support -> "Help and support" to
            "For enrollment, grades, tuition or account problems, contact your registrar or the " +
            "IT helpdesk of your institution. Include your account ID so they can find your record."

        ProfileDialog.Privacy -> "Privacy" to
            "This preview build stores only your account ID and your app settings on this device. " +
            "Nothing is sent to a server, and no analytics are collected."

        ProfileDialog.About -> "About KoAkademy" to
            "KoAkademy is an open-source academic management platform for enrollment, academics " +
            "and finance. This companion app is version 1.0, running on sample data until it is " +
            "connected to your institution's server."
    }
    AlertDialog(
        onDismissRequest = onClose,
        shape = MaterialTheme.shapes.large,
        containerColor = colors.surface,
        titleContentColor = colors.onSurface,
        textContentColor = colors.onSurfaceVariant,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Close")
            }
        },
    )
}

@Preview(name = "Profile")
@Composable
private fun ProfilePreview() {
    AppTheme {
        ProfileScreen(
            account = PortalDirectory.resolve("2024-00123"),
            preferences = InMemoryAppPreferences(),
            themePreference = ThemePreference.System,
            onThemePreferenceChange = {},
            onBack = {},
            onSignOut = {},
        )
    }
}
