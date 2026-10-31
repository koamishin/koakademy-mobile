KoAkademy Mobile — a companion app for [KoAkademy](https://github.com/koamishin/koakademy), the
open-source academic management platform for enrollment, academics and finance. This is a Kotlin
Multiplatform project targeting Android and iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/sharedLogic](./sharedLogic/src) is for the code that will be shared between app targets in the project.
  The most important subfolder is [commonMain](./sharedLogic/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/sharedUI](./sharedUI/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./sharedUI/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./sharedUI/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./sharedUI/src/jvmMain/kotlin)
    folder is the appropriate location.

### Design system

The interface is Material 3 rendered in KoAkademy's own brand: the warm paper background, near-black
ink and terracotta accent from the web panel are mapped onto every Material colour role — including
the surface containers, so dialogs, switches and sheets stay on-paper instead of falling back to the
default purple-grey scheme.

On top of that palette sits an **editorial layer** rather than a dashboard: every screen opens with a
running head and a serif headline, sections are numbered like a printed register, the week is a set of
filing tabs, the day is a timetable ruled to scale, grades are plotted on one shared axis, tuition is a
perforated bursar's slip, and profile is an account file with a punched identity card. Rounded cards,
tonal elevation and pill navigation are deliberately absent — rules, tabular figures and one accent ink
do the work instead. [DESIGN.md](./DESIGN.md) records the reference designs behind those decisions and
the full component vocabulary.

Both light and dark schemes, the full type scale and the shape set live in
[Theme.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/theme/Theme.kt). The editorial
components — masthead, filing tabs, scaled timetable, receipt, barcode, stamps, register figures and
transcript chart — are in
[BrandSystem.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/portal/BrandSystem.kt)
and [EditorialComponents.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/portal/EditorialComponents.kt);
the KoAkademy mark and wordmark are in
[KoakademyBrand.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/components/KoakademyBrand.kt).
Design references for both roles and both themes are in [screenshots](./screenshots).

Onboarding and sign-in are deliberately the quietest screens in the app: one section name, one serif
headline, one short line and, on onboarding, a single specimen document. They carry no numbered tabs,
stamps or barcodes, so the first impression is the brand's typography and paper rather than its
ornament — the portal is where the denser editorial detail earns its place.

### App flow

The UI is built with Jetpack Compose (Compose Multiplatform) and lives in [sharedUI](./sharedUI/src/commonMain/kotlin):

- **Onboarding** – three pages built around the platform's pillars: academics (schedule and attendance),
  enrollment with the registrar, and finance (grades and tuition). It is shown once, on the first start,
  and remembered through [AppPreferences](./sharedLogic/src/commonMain/kotlin/com/example/myapplication/auth/AppPreferences.kt).
- **Login** – institutional sign-in with either an institutional ID or the institution-issued email,
  password visibility toggle, inline validation and registrar-focused password-reset guidance. Two ruled
  rows at the foot of the page fill in a student or faculty demo account, since there is no server behind
  this build yet.
- **Role-aware portal** – the account decides which portal opens; both share the brand header, a
  set of numbered index tabs and the avatar shortcut to profile settings. Role resolution lives in
  [PortalAccount.kt](./sharedLogic/src/commonMain/kotlin/com/example/myapplication/portal/PortalAccount.kt).
  - **Student portal** – *Home* (the week as filing tabs above a timetable ruled to scale, term
    register, registrar notices), *Schedule* (the same day plan at reading size plus the class list),
    *Grades* (general average as a display numeral and a seal, every course plotted midterm→final on a
    shared 1.00–5.00 axis, attendance in a course record table), and *Finance* (a bursar's slip with a
    perforated tear line, balance, settled progress, barcode and payment history).
  - **Faculty portal** – *Home* (the teaching day to scale with free blocks annotated in the margin,
    load register, queue counts, deadlines), *Classes* (a section register with graded progress and
    averages, tapping through to a roster with attendance and standing), *Grading* (a deck of
    submission slips you page through, each carrying the student's file and note, then the whole
    queue), and *Inbox* (registrar, dean, HR and student letters as a correspondence ledger).
- **Profile & settings** – opened from the avatar and presented as an account file: masthead,
  institutional identity card with a barcode and a record strip, then ruled entries for grades,
  announcement and payment notifications, appearance (System / Light / Dark), password and
  active-session information, help, privacy, about, and sign out. Appearance and notification choices
  are stored through
  [AppPreferences](./sharedLogic/src/commonMain/kotlin/com/example/myapplication/auth/AppPreferences.kt)
  and survive app restarts; the system back gesture returns from profile and class rosters instead of
  closing the app.

### Demo accounts

| Role | Account | Name |
| --- | --- | --- |
| Student | `2024-00123` | Ada Santos |
| Faculty | `F-2019-004` | Marisol Cruz |

Any email address also signs in; an employee ID starting with `F` opens the faculty portal, everything
else opens the student portal. The role is derived from the stored account, so it survives a restart.

Portal content is sample data for First Semester, A.Y. 2026–2027: the app is not connected to an
institution's KoAkademy server yet. Sign-in validates locally (any valid ID/email plus a password of at
least 8 characters), and the signed-in account is stored on the device (SharedPreferences via
[SharedPreferencesAppPreferences](./androidApp/src/main/kotlin/com/example/myapplication/SharedPreferencesAppPreferences.kt)),
so relaunching the app returns you to the portal; signing out clears it.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug` (set `sdk.dir` in `local.properties` to your Android SDK first)
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :sharedUI:testAndroidHostTest :sharedLogic:testAndroidHostTest`
- iOS tests: `./gradlew :sharedLogic:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…