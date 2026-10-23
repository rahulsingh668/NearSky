# Version 1.5.2 validation — 23 October 2026

- `assembleDebug`, `assembleDebugAndroidTest`, `lintDebug` and APK signature verification passed.
- Installed versionCode 10 / versionName 1.5.2 on the TCL TV, retaining saved location and display preferences.
- **412 native assertions passed**: all 91 bundled carriers have offline company-country values; all SVGs still render; domestic/international classification compares valid endpoint ISO country codes; missing/invalid codes remain unknown; airline-country responses must match the exact ICAO operator. Remote OK, Back, Menu and existing-activity settings intent checks continue to pass.
- Company country is independent of flight classification. Foreign-carrier domestic routes remain domestic; route type is never inferred from the TV location, the airline's home country, airport prefix or aircraft registration.
- Company-country seed coverage exactly matches the 91 bundled artwork identifiers and every seed retains a source URL. Runtime exact-code airline metadata can refresh these values.
- The visible gear shrank from a 23 dp outline in a filled 44 dp tile to a muted 16 dp outline with no filled tile and a faint focus ring. Its 48 dp interactive target is preserved. Footer uses © 2026.

# Version 1.5.1 validation — 23 October 2026

- `assembleDebug`, `assembleDebugAndroidTest` and `lintDebug` passed. APK signature verified.
- Installed versionCode 9 / versionName 1.5.1 on the TCL TV; saved New York location, radius and display preferences retained.
- **313 native assertions passed**, including remote OK opening the settings panel from full-screen preview, returning to preview, Back reopening settings, Menu reopening settings, and a settings intent reopening an existing activity. Existing data and all 182 SVG rendering checks still pass.
- Eight physical-TV LIVE screenshots show the red indicator alternating bright and dim; header red-pixel counts: 76, 0, 0, 78, 76, 0, 76, 76. Screenshot inspection confirms the bottom-left gear and exact updated copyright text, without the removed distance disclaimer.
- The actual DreamService now uses the same focusable settings control and interactive input. Its intent reopens the app panel, exits the dream and resets to live mode. The existing-component intent logic is covered by instrumentation. The TCL's Somnambulator shortcut did not activate a dream during this run, so the DreamService handoff itself was not observed in a running system dream. Its saved component and enabled setting remain unchanged.

# Version 1.5.0 validation — 22 October 2026

- `assembleDebug`, `assembleDebugAndroidTest`, and `lintDebug` passed.
- Installed versionCode 8 / versionName 1.5.0 on the TCL TV, retaining saved preferences.
- Physical TCL instrumentation passed **308 assertions**, including native AndroidSVG parsing and visible-pixel rasterization for **91 aircraft SVGs and 91 wordmark references**.
- Host XML checks passed for all 182 assets: unique IDs, resolved local references, no scripts or external image requests. Approved IndiGo artwork remains byte-identical.
- The real-code export contains exactly 91 SVGs with status metadata. The legacy 17,576-code export parses in full and its 91 overrides match source bytes. All bundled aircraft SVGs match the APK bytes.
- Browser checks: ICAO CPA and IATA CX find Cathay Pacific; the all-identities view retains Spirit NKS as historical. New drafts are labeled as drafts. Country and artwork-status filters are provided.
- Four rendered contact sheets were inspected; contrast and window-row lettering overlap in new artwork were corrected. This is structural/render validation, not certification of exact airline logos or current paint.
- Inventory: 3,571 identity/code records, 3,111 distinct codes; 1,794 historical, 1,776 with unverified operating status and one Hawaiian legacy-code record. 91 built designs; 1,685 remaining reference-pending records in the operating-status review queue. None are automatically asserted active.

# Version 1.4.1 validation — 22 October 2026

- Card airline names use the normal sans-serif face at 38px in the 1920 × 1080 design instead of the large branded heading outlines. Aircraft SVG lettering and paint are unchanged.
- `assembleDebug` passed and APK signature verified. Installed version code 7 / version name 1.4.1 on the TCL TV with saved preferences retained.
- Visually inspected the real live display: IndiGo and Air India Express names fit clearly in the smaller simple font. No data or SVG logic changed; the previous 107 native assertions were not rerun for this cosmetic adjustment.

# Version 1.4 validation — 22 October 2026

- Applied the approved IndiGo artwork unchanged and added hand-redrawn standard schemes for 23 other carriers, with larger outlined English headings, carrier colours and simplified tail marks. Unknown codes retain labeled concept artwork. All illustrations use a representative shared airliner, not exact per-type geometry or per-registration paint.
- All **48 assets** (24 aircraft + 24 wordmarks) passed host XML, unique-ID, local-reference and no-external-image/script checks. The approved IndiGo SVG is byte-identical to its review source.
- `assembleDebug assembleDebugAndroidTest lintDebug` passed. APK signature verified. Version code 6 / version name 1.4.
- Installed the final APK on the physical TCL Smart TV with existing settings preserved. **107 native data/settings/rendering assertions passed**, including rendering all 48 bundled SVGs with AndroidSVG and checking for painted pixels.
- Visually inspected the real 1920 × 1080 sample screen with Air India, IndiGo and SpiceJet; lettering and metrics fit. The live DreamService was subsequently started again. New captures are in `docs/screenshots/standard-liveries-demo.png` and `standard-liveries-live.png`.
- Re-exported the full **17,576-code SVG pack**; every file parses as XML and all 24 bundled overrides match the source exactly. Remaining identifiers are original concept artwork, not verified standard liveries.
- Artwork/English brand lettering are approximations. Complex tail marks are simplified, Arabic wordmarks are not supplied, and fleet transitions, leases, special paint and Air India Express tail variants are not matched individually. The standard Lufthansa scheme is used rather than anniversary specials; Korean Air represents the 2025 blue/KOREAN scheme.
- The existing 26 host airline-name checks and 14 route checks were not repeated because the parser/generator behavior they exercise was unchanged. Native regression checks cover the modified rendering integration. A long-duration TV soak test was not performed.

# Version 1.3.1 validation — 22 October 2026

- Changed the bottom-right footer to “Developed by Rahul Singh”. Version code 5 / version 1.3.1.
- `assembleDebug` passed and APK signature verified. Installed successfully on the connected TCL TV with settings preserved. Visually confirmed the exact footer text in a 1920 × 1080 capture of the live TCL display (`NearSky-footer-TCL.png`). No data logic changed; previous route tests were not repeated for this text-only update.

# Version 1.3 validation — 22 October 2026

- Added origin → destination airport codes and city/airport names to all three landscape cards. Routes use the reported callsign and provider-returned exact ICAO/IATA aliases; another flight’s response is rejected.
- **14 host route checks passed** against the actual parser, including a captured adsbdb AIC101 response, exact callsign matching, IATA aliases, normalization, absent data, malformed responses, ICAO airport fallback, airport name fallback and rejecting unsafe identifiers/partial routes.
- Positive reference-route cache expires after 30 minutes, unsuccessful lookups after 5 minutes. Memory is bounded to 64 entries with at most six pending requests. No route data is persisted, and sample flights do not query real routes.
- Final `assembleDebug lintDebug` passed; lint reports zero errors (16 warnings). Debug APK signature verified. Version code 4 / version 1.3 installed successfully with `adb install -r` on the TCL Smart TV. The live fullscreen activity opened successfully.
- Inspected an actual 1920 × 1080 TCL capture with three live flights: origin/destination codes and city names fit on all cards along with airline, model, distance and four metrics. Capture: `NearSky-routes-TCL.png`. Route rows reflect provider reference data; these routes can be outdated or inconsistent with current positions. Manufacturer idle activation was not changed or retested.
- The previous 34 device smoke assertions were not rerun for this update; the route parser has the 14 focused host checks above. No long-duration TV soak test was performed.

# Version 1.2 validation — 22 October 2026

- Added verified public airline names to the fuselage SVG text and the card heading; callsign and aircraft type remain below the name. The custom palettes and patterns remain unchanged.
- Verified live adsbdb airline responses for 12 bundled carriers, with public brand spelling overrides for IndiGo and SpiceJet. Provenance and captured response fixtures are included in the source.
- **26 host checks passed** for brand names, code fallback, spacing, missing names, length bounds, XML escaping, safe SVG override replacement, exact-code API matching, ambiguous result arrays, unknown responses and all 12 captured live responses. These exercise the actual static response parser and SVG generator.
- Exhaustive validation of the regenerated 17,576 SVG pack passed: all files parse as XML, all designs remain distinct, and AIC/IGO/SEJ fuselage labels match Air India/IndiGo/SpiceJet. Other non-seeded codes retain code lettering in the static pack; the live app resolves additional names on demand.
- `assembleDebug assembleDebugAndroidTest lintDebug` passed. Version code 3, version name 1.2. The updated SVG fuselage lettering was visually inspected using macOS Quick Look. This incremental name update was not rerun in an Android TV emulator; the previous version's device results are retained below for context.
- Additional names use an exact-ICAO-code lookup and bounded local caching. Unverified names remain blank with the operator code shown. Names are XML-escaped and limited to 80 code points. No registered-owner name is substituted as an airline name.

# Version 1.1 validation — 22 October 2026

- `assembleDebug assembleDebugAndroidTest lintDebug`: successful; zero lint errors. The debug APK signature verifies. Version code 2, version name 1.1.
- Android TV API 34 emulator: **34 data, settings, identifier and SVG-rendering assertions passed**. Added checks cover case-insensitive ICAO prefixes, rejecting private registrations and invalid identifiers, deterministic designs, distinct designs, and SVG parsing/rendering for all six pattern families, selected airline codes, an extreme code and the neutral design.
- Exported **17,576 SVGs**, covering the entire AAA–ZZZ identifier space. Exhaustive host validation confirmed every SVG is well-formed XML, has the expected viewBox, is distinct, and contains no scripts or external images. Unassigned codes are included; this is not a count of real airlines.
- Installed the final build with per-code SVG asset overrides. Visually verified `NearSky-wide-preview.png` on a 1920 × 1080 Android TV display: three equal-width cards use the landscape width, all artwork and details fit, and AIC/IGO/SEJ have distinct concept palettes and patterns. The screenshot contains prominently labeled sample flights.
- The final SVG library, license, 12 editable examples and generator/export source are included in the source archive. The runtime uses generated SVGs when an override is absent or invalid; missing operator information uses the neutral design.
- The original live-data filtering and DreamService lifecycle are retained. The original idle activation checks below apply to version 1.0; automatic idle activation was not repeated during this layout-only update. This version's application rendering and SVG integration were checked on the emulator. No physical TV was connected.

# Version 1.0 validation (historical)

## Passed

- Built the debug application APK and Android instrumentation APK with JDK 17, Android SDK Platform 35, Build Tools 36.0.0, Android Gradle Plugin 8.9.1 and Gradle 8.11.1.
- `assembleDebug assembleDebugAndroidTest lintDebug`: successful. Lint found no errors. Remaining warnings concern English strings, orientation, version-gated manifest attributes, banner size and allocations during the one-second display redraw.
- APK signature verified with `apksigner`; manifest checked with `aapt`. The APK declares the TV launcher activity, screensaver service, optional touchscreen support, Internet permission, minimum API 26 and target API 35.
- Installed both APKs on Google's Android 14 / API 34 Android TV x86 emulator, 1920 × 1080 at 320 dpi.
- **17 on-device data and settings assertions passed**, covering great-circle distances, crossing the date line, compass directions, invalid coordinates, sorting, three-aircraft limit, duplicates, grounded aircraft, radius filtering, expiration, empty coverage, parser schema variants, ground markers, padded callsigns, altitude fallback, stale feed clocks, malformed schemas, invalid settings and default preference loading. Some assertions cover multiple related cases.
- The shared data logic also passed 15 host-side assertions using a standalone JSON implementation.
- Fetched the provider's current recommended v3 response for Ghaziabad; parsed and selected the nearest three aircraft within a strict 50 km great-circle radius. Verified an aircraft metadata response and a real JPEG photo download from the image provider.
- Rendered the installed app's sample preview on the TV emulator. Visually verified the centered portrait panel, three stacked cards, photo attribution, labeled illustration fallback, flight details and prominent DEMO state. `NearSky-preview.png` is an actual emulator capture, not a browser mockup.
- Verified that the remote Menu key opens the location dialog, with a focused city selector, Ghaziabad coordinates, editable name/latitude/longitude and 50 km radius, and accessible Save/Cancel controls.
- **Verified real idle screensaver activation.** After selecting NearSky, enabling activation on sleep and disabling the emulator's developer stay-awake setting, Android's DreamManager reported `com.nearsky.tv.FlightDreamService` as its current non-preview dream, with the service bound successfully. Sample mode is confined to the app preview; this service uses the live feed.
- Captured `NearSky-live.png` from that non-preview screensaver with three live aircraft around Ghaziabad. Verified a remote OK key initiates waking and the DreamManager subsequently reports no current dream. No app exceptions appeared in the AndroidRuntime error log during the run.

## Device-specific observations

- The test TV image does not resolve `android.settings.DREAM_SETTINGS`; the app catches this and shows manual setup guidance.
- `cmd dreams start-dreaming` exists on this TV image but rejects non-root shell callers. The normal idle activation path works without root. The setup guide documents this distinction.
- Test-only changes such as a 15-second idle timeout were made solely to a disposable emulator. No physical TV was connected or configured.

## Still to check on your TV

Manufacturer-specific screensaver selection, idle/power settings, display overscan, photo availability and network coverage in your home. This is a debug-signed sideload build, not a Play Store release. The app has not undergone a long-duration physical-TV soak test. Position expiration is covered by data tests; a prolonged on-device network outage was not simulated.
