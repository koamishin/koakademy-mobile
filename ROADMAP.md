# KoAkademy Mobile — recommended features

The current build is a design-complete, data-static preview: navigation, both portals and profile
settings work, but nothing is connected to a KoAkademy server. The list below is ordered by what
unlocks the most value for a real institution.

## Before a real pilot (blocking)

| Feature | Why it matters |
| --- | --- |
| **Server integration** | Replace sample data with the KoAkademy API (Laravel Sanctum tokens or OIDC through the Koamishin portal). Every screen already has a single data source to swap. |
| **Secure session storage** | Move tokens from SharedPreferences to EncryptedSharedPreferences / Keystore, add refresh and expiry handling, and clear cache on sign-out. |
| **Push notifications** | Firebase Cloud Messaging (Android) and APNs (iOS) for grade posting, registrar holds and tuition reminders; the profile switches already model the preferences. |
| **Offline and error states** | Cache the last schedule, grades and balance (SQLDelight), and show retry/offline banners instead of empty screens. |
| **Enrollment module** | Students: subject selection with prerequisites, schedules and slots, requirements checklist and upload, registrar status. This is the third pillar promised by onboarding. |

## High-value next

| Feature | Why it matters |
| --- | --- |
| **Faculty grade entry** | The grading queue is read-only today. Add rating entry with validation, save-as-draft and a confirmation before posting. |
| **Payments** | Show assessment and accept online payment (GCash, Maya, card) with receipts, instead of "pay at the cashier". |
| **Attendance** | QR or code check-in for students, live attendance lists for faculty, and the at-risk flags the portal already displays. |
| **Calendar export** | Add class sessions and deadlines to the device calendar; term calendar for enrollment and exam weeks. |
| **Document requests** | Request TOR, certificate of enrollment and good moral character, with status tracking — a common registrar queue. |
| **Search and deep links** | Find a subject, student or message quickly; open a specific grade or notice from a notification. |

## Experience and reach

| Feature | Why it matters |
| --- | --- |
| **Biometric unlock** | Fingerprint/face unlock for a phone that is often shared or left on a desk. |
| **Filipino localization** | The platform already carries Filipino copy; a language switch would serve more students and parents. |
| **Tablet and foldable layouts** | Two-pane schedule + details, and a wider gradebook, using window size classes. |
| **Accessibility pass** | Larger text, screen-reader labels for charts and progress, and colour-contrast checks beyond AA. |
| **Campus announcements and chat** | Read receipts and replies for faculty messages, plus college-wide announcements in-app. |
| **Widgets and shortcuts** | Next class and tuition balance on the home screen; quick action to the schedule. |

## Platform hardening

| Feature | Why it matters |
| --- | --- |
| **Crash and error reporting** | Sentry or Firebase Crashlytics on both platforms, with PII scrubbing. |
| **CI and device tests** | Run the shared unit tests plus a screenshot test per portal on every change. |
| **Analytics with consent** | Screen-level adoption data, opt-in only, to guide which modules to build next. |
| **Multi-institution branding** | KoAkademy is self-hosted, so the mark, colours and copy should come from the server's branding config instead of being compiled in. |
| **White-label release flavors** | Separate application IDs per institution so each school can ship its own listing. |

## Suggested order

1. Server integration + secure sessions + push notifications — makes the app genuinely useful.
2. Enrollment and payments — completes the three pillars and removes the biggest queue at the registrar.
3. Faculty grade entry and attendance — makes the faculty side a daily tool, not a viewer.
4. Polish: offline cache, search, localization, biometrics, tablets.
