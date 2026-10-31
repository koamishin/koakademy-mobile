package com.example.myapplication.ui.portal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.portal.PortalAccount
import com.example.myapplication.portal.PortalDirectory
import com.example.myapplication.portal.PortalRole
import com.example.myapplication.portal.SampleFacultyPortal
import com.example.myapplication.portal.SampleStudentPortal
import com.example.myapplication.portal.avatarLabel
import com.example.myapplication.ui.components.KoakademyMark
import com.example.myapplication.ui.faculty.FacultyClassesScreen
import com.example.myapplication.ui.faculty.FacultyGradingScreen
import com.example.myapplication.ui.faculty.FacultyHomeScreen
import com.example.myapplication.ui.faculty.FacultyInboxScreen
import com.example.myapplication.ui.student.StudentFinanceScreen
import com.example.myapplication.ui.student.StudentGradesScreen
import com.example.myapplication.ui.student.StudentHomeScreen
import com.example.myapplication.ui.student.StudentScheduleScreen
import com.example.myapplication.ui.theme.AppTheme

fun tabsFor(role: PortalRole): List<PortalTab> = when (role) {
    PortalRole.Student -> listOf(
        PortalTab.StudentHome,
        PortalTab.StudentSchedule,
        PortalTab.StudentGrades,
        PortalTab.StudentFinance,
    )

    PortalRole.Faculty -> listOf(
        PortalTab.FacultyHome,
        PortalTab.FacultyClasses,
        PortalTab.FacultyGrading,
        PortalTab.FacultyInbox,
    )
}

/**
 * The signed-in portal. Students get academics, schedule, grades and finance; faculty get
 * their teaching load, class rosters, the grading queue and institutional messages.
 * The chrome is a newspaper running head and numbered index tabs; the avatar opens profile.
 */
@Composable
fun PortalScreen(account: PortalAccount, onOpenProfile: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val tabs = remember(account.role) { tabsFor(account.role) }
    var selectedIndex by rememberSaveable(account.id) { mutableStateOf(0) }
    val selectedTab = tabs[selectedIndex.coerceIn(0, tabs.lastIndex)]

    fun openTab(tab: PortalTab) {
        val index = tabs.indexOf(tab)
        if (index >= 0) selectedIndex = index
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            RunningHead(
                issue = if (account.role == PortalRole.Student) "VOL. II · NO. 12" else "VOL. II · NO. 12",
                account = account,
                onOpenProfile = onOpenProfile,
            )
        },
        bottomBar = {
            LedgerTabs(
                tabs = tabs,
                selected = selectedTab,
                onSelect = ::openTab,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
    ) { innerPadding ->
        when (selectedTab) {
            PortalTab.StudentHome -> StudentHomeScreen(
                account = account,
                data = SampleStudentPortal,
                innerPadding = innerPadding,
                onOpenTab = ::openTab,
            )

            PortalTab.StudentSchedule -> StudentScheduleScreen(
                data = SampleStudentPortal,
                innerPadding = innerPadding,
            )

            PortalTab.StudentGrades -> StudentGradesScreen(
                data = SampleStudentPortal,
                innerPadding = innerPadding,
            )

            PortalTab.StudentFinance -> StudentFinanceScreen(
                data = SampleStudentPortal,
                innerPadding = innerPadding,
            )

            PortalTab.FacultyHome -> FacultyHomeScreen(
                account = account,
                data = SampleFacultyPortal,
                innerPadding = innerPadding,
                onOpenTab = ::openTab,
            )

            PortalTab.FacultyClasses -> FacultyClassesScreen(
                data = SampleFacultyPortal,
                innerPadding = innerPadding,
            )

            PortalTab.FacultyGrading -> FacultyGradingScreen(
                data = SampleFacultyPortal,
                innerPadding = innerPadding,
            )

            PortalTab.FacultyInbox -> FacultyInboxScreen(
                data = SampleFacultyPortal,
                innerPadding = innerPadding,
            )
        }
    }
}

/** Newspaper-style running head: mark, wordmark, issue line, then the double rule. */
@Composable
private fun RunningHead(
    issue: String,
    account: PortalAccount,
    onOpenProfile: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(52.dp)
                .padding(start = 22.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KoakademyMark(size = 17.dp)
            Spacer(Modifier.width(9.dp))
            Text(
                text = "KOAKADEMY",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 12.sp,
                    letterSpacing = 2.6.sp,
                ),
                color = colors.onSurface,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = issue,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 7.5.sp,
                    letterSpacing = 1.sp,
                ),
                color = colors.onSurfaceVariant,
                maxLines = 1,
            )
            Spacer(Modifier.width(10.dp))
            ProfileButton(account = account, onClick = onOpenProfile)
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(colors.primary))
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.outlineVariant.copy(alpha = 0.9f)),
        )
    }
}

@Composable
private fun ProfileButton(
    account: PortalAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Open profile and settings" },
        contentAlignment = Alignment.Center,
    ) {
        AvatarCircle(label = account.avatarLabel(), size = 28.dp)
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(1.dp, colors.outlineVariant, CircleShape),
        )
    }
}

@Preview(name = "Student portal")
@Composable
private fun StudentPortalPreview() {
    AppTheme {
        PortalScreen(
            account = PortalDirectory.resolve("2024-00123"),
            onOpenProfile = {},
        )
    }
}

@Preview(name = "Faculty portal")
@Composable
private fun FacultyPortalPreview() {
    AppTheme {
        PortalScreen(
            account = PortalDirectory.resolve("F-2019-004"),
            onOpenProfile = {},
        )
    }
}
