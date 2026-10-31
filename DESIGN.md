# KoAkademy Mobile — design language

The portals are not a dashboard of rounded cards. They are built on one idea: **an academic
press, bound into a phone.** Three print devices carry it.

1. **The running head.** Every screen opens like a printed page: a folio line (section · context ·
   date), a serif headline, a supporting line, and the brand's split rule.
2. **The ledger.** Information is ruled, not contained. Hairline rules, numbered sections (`01`,
   `02`…), tabular figures in monospace, and one accent ink — terracotta — per screen.
3. **The document.** Paper behaves like paper: cut corners, a perforated tear line on the tuition
   slip, a punched identity card with a barcode, rubber stamps for states that are final.

Day headers are **filing tabs** on the edge of the sheet, the navigation is a set of **numbered
index tabs**, and the teaching day is a **time axis** where each class is as tall as it is long.

## References, and what each one contributed

| Reference | Device taken | Where it lands |
| --- | --- | --- |
| [Mobbin — settings & preferences](https://mobbin.com/explore/mobile/screens/settings-preferences) | The stacked rounded-card list that dominates settings screens — used as the baseline to design *against* | Removed from profile in favour of hairline rows |
| [Read.cv profile settings (Nicelydone)](https://nicelydone.club/examples/apps/readcv) | One document-like card instead of a switch list | `IdentityRecord` at the top of profile |
| [Minga digital student ID](https://minga.io/digital-student-id/) | Identity card carrying a barcode **and** a typed serial so it survives a failed scan; must render offline | Identity record: `BarcodeStrip` + serial line, purely local data |
| [ICAO 9303 machine-readable passports](https://en.wikipedia.org/wiki/Machine-readable_passport) | Fixed label / value / status columns, uppercase, monospace reserved for the record itself | `RecordCell` strip — used as *form*, not a fabricated MRZ (no invented check digits) |
| [Mercury design breakdown](https://oh-my-design.kr/design-systems/mercury) | Hairline table rules, near-zero radius, tabular figures so columns align | Profile rows, register headers, receipt amounts |
| [Swiss design for web designers](https://swissthemes.design/insights/swiss-design-for-web-designers) | One fixed type scale, rules instead of shadows, deliberate grid breaks | Masthead, `SectionIndex`, the oversized average on the transcript |
| [shadcn boarding-pass stub](https://www.shadcn.io/blocks/solid/features-boarding-pass-ticket-stub) | A perforation made of **punched notches**, not dashes, with a hairline arc implying paper thickness | `PerforationLine` on the bursar statement |
| [Pure-CSS folded corner (Gallagher)](https://nicolasgallagher.com/pure-css-folded-corner-effect/) | Corner cut with a crease, so a card reads as a folded sheet | `CutCornerShape` on timetable blocks, `RuledSheet`, identity card |
| [Assembli print-metaphor blocks](https://www.assembli.design/) | Put the *outcome* on the stub and the narrative on the body | Receipt: amount and due date above, line items below. Submission deck: stamp + title above, file + note below |
| [Material 3 Expressive research](https://design.google/library/expressive-material-design-google-research) | Hierarchy from containment, size and one accent — not elevation; keep familiar affordances even when expressive | Ledger tabs keep icon **and** label; no tonal elevation anywhere |
| [Paper shaders / torn edges (Codrops)](https://tympanus.net/codrops/2026/10/19/paper-shaders-off-background/) | Deckled paper edges and crease shading | **Not used.** Texture shaders cost more than they return at phone size; the torn receipt edge already carries the idea |

## The vocabulary

| Device | Component | File |
| --- | --- | --- |
| Folio, serif headline, split rule | `PageMasthead`, `SplitRule` | [BrandSystem.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/portal/BrandSystem.kt) |
| Numbered index tabs (navigation) | `LedgerTabs` | BrandSystem.kt |
| Week as filing tabs | `DayTabs` | BrandSystem.kt |
| Day plan sized by duration, with marginalia for free time | `DayTimetable`, `freeLabel` | BrandSystem.kt |
| Receipt, tear line, barcode, stamps | `ReceiptPanel`, `ReceiptRow`, `PerforationLine`, `BarcodeStrip`, `RubberStamp`, `AcademicSeal`, `PostmarkStamp` | BrandSystem.kt |
| Register figures | `RegisterColumns`, `Metric` | BrandSystem.kt |
| Transcript on one shared axis | `RatingComparison` | BrandSystem.kt |
| Numbered section headers, term progress | `SectionIndex`, `TermProgress` | [EditorialComponents.kt](./sharedUI/src/commonMain/kotlin/com/example/myapplication/ui/portal/EditorialComponents.kt) |
| Ruled paper sheet | `RuledSheet` | BrandSystem.kt |

## Rules

- **Type.** Serif (`FontFamily.Serif`) for headlines, dates and large numerals; sans for reading;
  monospace for serials, money and anything that must align in a column.
- **Micro-caps** (8–10sp, 1.2–2sp tracking) are reserved for folios, labels and serials. Functional
  settings text stays at body size — the settings list is 16sp, never tracked caps.
- **Rules over shadows.** Hairline `outlineVariant` for structure; the only elevation is paper
  stacked on paper in the submission deck.
- **One accent per screen.** Terracotta marks the selected day, the balance due, the awaiting
  stamp, the number of the section. Ink carries everything else.
- **Numbers are typography.** Units, averages, balances, ratings and attendance are monospace or
  serif tabular, so a column of them lines up.
- **Motion is paper.** The selected day tab rises on a height spring; nothing else animates.
  No shimmer, no skeleton pulses.
- **Familiar affordances stay familiar.** The navigation keeps an icon and a label for every
  destination, and the day tabs are still a row you can tap across.
- **The first pages are the quietest.** Onboarding and sign-in keep one section name, one serif
  headline, one short line and at most one specimen; position is stated by a single 3-segment rule,
  not by labelled tabs. Ornament — stamps, barcodes, index numbers — belongs to the portal, where
  the reader already knows where they are. When a page feels crowded, the first thing to cut is a
  repeat of information another element already carries.

## Open items

- The receipt's bottom edge is a torn zigzag (`ReceiptEdgeShape`); the side notches on the tear
  line are painted in the page colour, so the slip must sit on `background` for them to read as cuts.
- No screenshot tests — both themes are verified by hand on the emulator (`screenshots/`).
