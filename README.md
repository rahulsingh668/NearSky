# NearSky for Android TV / Google TV

Developed by **Rahul Singh** · Current version: **1.3.1**

**[Step-by-step how-to guide](docs/HOW_TO.md)** — build from source, install on a TV, configure location and radius, enable the screensaver, customize liveries and troubleshoot.

A native Android screensaver showing up to three nearby airborne aircraft in three large cards across the full width of a horizontal TV. Defaults to **Ghaziabad (28.6692, 77.4538), within 50 km**. Android 8.0 or newer. No account, GPS permission, API key, backend or subscription required for the current personal-use data sources.

Each card shows an original SVG concept livery with the real airline name on its fuselage. The airline name is also the card heading, followed by callsign, aircraft model, origin and destination airport codes with city names, registration, distance, direction from you, altitude, ground speed and heading. The aircraft art uses a generic airliner silhouette, not an exact fleet/type depiction. Colors and tail motifs are original designs. Airline names use their actual public names where verified, in ordinary lettering; official wordmark typography is not reproduced. Reference photos are an optional preference; when enabled, a photo matched to the aircraft's ICAO hex identifier replaces the SVG if available. Coverage and photos are not guaranteed. With fewer than three aircraft in range, remaining slots show an empty state.

## Install

The supplied `NearSky-debug.apk` is a debug-signed, sideloadable build. It is not a Play Store release. Install it using a TV file-transfer application or ADB. On the TV, authorize installation from the file-transfer application if prompted.

With Android Platform Tools on your computer, enable Developer Options and the TV's ADB / wireless debugging option. Follow the TV's pairing instructions if it uses wireless pairing:

```sh
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:DEBUGGING_PORT
adb devices
adb install -r NearSky-debug.apk
adb shell am start -n com.nearsky.tv/.MainActivity
```

Older TVs may use `adb connect TV_IP:5555` without `adb pair`. Use the address/ports shown by your TV; pairing and debugging ports may differ. These commands assume one connected Android device; otherwise add `-s YOUR_TV_SERIAL` after `adb`.

## Configure and preview

1. Open **NearSky** from the TV's apps list.
2. Select **Location & radius**. Choose a city preset or enter a display name and coordinates. A display name alone does not look up a location. You can use your home's map-pin coordinates for greater accuracy than the default city center.
3. Leave the radius at 50 km or change it between 1 and 200 km. Save.
4. Select **Full-screen preview**. Press Back to return to the menu.
5. **Try sample flights** demonstrates the three-card layout. A prominent DEMO label remains visible. Sample mode only applies to the app preview; the actual screensaver always uses live positions.

The remote's directional pad and OK navigate settings. Menu opens location settings. Custom SVG liveries are the default. Turn on **Prefer photos instead of custom SVG liveries** only if you want reference photos. Version 1.1 starts with SVG mode on upgrade as well; the saved location and radius remain unchanged. The setup menu opens as a centered overlay. Full-screen preview and the screensaver use the entire landscape display without that menu.

## Select as the idle screensaver

Select **Set as screensaver**, then choose **NearSky · Nearby aircraft** in your TV's Screen saver / Ambient Mode settings and set the start delay. Some Google TV devices hide third-party screensaver selection. On these devices, the following shell settings may work; availability is controlled by the TV manufacturer and OS.

First record your current values so you can restore them:

```sh
adb shell settings get secure screensaver_components
adb shell settings get secure screensaver_default_component
adb shell settings get secure screensaver_enabled
adb shell settings get secure screensaver_activate_on_sleep
```

Then select NearSky:

```sh
adb shell settings put secure screensaver_components com.nearsky.tv/.FlightDreamService
adb shell settings put secure screensaver_default_component com.nearsky.tv/.FlightDreamService
adb shell settings put secure screensaver_enabled 1
adb shell settings put secure screensaver_activate_on_sleep 1
```

To restore the previous screensaver, repeat the `put` commands with the values you recorded. If a previous value was `null`, use `adb shell settings delete secure THE_SETTING_NAME` for that setting. The app itself does not change these system settings. Power / energy-saving settings may turn the TV off before the screensaver starts; set the TV's idle screensaver delay earlier than its power-off delay. Also turn off Developer Options → Stay awake if enabled. A fullscreen app preview stays awake intentionally; return to the launcher to test automatic idle activation.

For an immediate screensaver test on OS versions that offer it:

```sh
adb shell cmd dreams help
adb shell cmd dreams start-dreaming
adb shell cmd dreams stop-dreaming
```

If those shell commands are unavailable or return `Must be root`, use **Start now** / **Preview** in the TV's screensaver settings instead; root access is not needed to use the app. A normal remote key exits the screensaver. Reboots or manufacturer updates can reset third-party screensaver selection.

## Data behavior and privacy

- Default location is a Ghaziabad city-center approximation; edit it for your actual viewing position.
- Distance is horizontal great-circle distance to the aircraft's reported position, not slant distance or a prediction of its current position.
- Positions refresh 15 seconds after the previous request finishes. Network timeouts and failed requests can extend that interval. The feed is not guaranteed to be instantaneous.
- Grounded aircraft, invalid coordinates, duplicate identifiers and positions older than 60 seconds are excluded. Cached positions continue aging during an outage and disappear when too old. The app shows delayed / empty states instead of inserting sample flights.
- Aircraft reference photos and metadata load separately and are kept in a bounded memory cache. Photos are not requested while SVG mode is selected; aircraft metadata still supports model and operator lookup. They are never bundled into the APK. Image source URLs appear in **Sources & photo credits**.
- Location settings stay in private app preferences, excluded from Android backups. Coordinates are sent over HTTPS to adsb.fi for the nearby-aircraft query. Aircraft hex identifiers, flight callsigns and operator codes are sent to adsbdb; image requests go to Airport-data.com. Those services also receive your IP address. No analytics, GPS collection or background polling when the display stops.
- Route lookups use the exact reported callsign, including provider-returned ICAO/IATA aliases. Airport IATA codes are preferred, with ICAO codes as fallback; city names fall back to airport names. Missing coverage or lookup failures show Route unavailable. Routes are provider reference data, not verified real-time departure/arrival information, and may differ during diversions or callsign reuse. Results stay in a bounded memory cache for 30 minutes; unavailable results retry after 5 minutes while visible. Sample mode does not query routes for its fictional flights.
- Flights may lack callsigns, aircraft information or photos. No inferred origin/destination or airline is invented. Times follow the TV's configured clock and timezone.
- Dark colors and slight horizontal drift suit an ambient display; brightness and panel protection remain controlled by the TV. Not for aviation navigation.

## Build from source

Open this folder in Android Studio or use JDK 17 and an Android SDK with Platform 35 and Build Tools 36.0.0. Set `ANDROID_HOME` to the SDK path or create a private `local.properties` containing `sdk.dir=/your/android/sdk`.

```sh
./gradlew assembleDebug lintDebug assembleDebugAndroidTest
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. The Gradle wrapper uses Gradle 8.11.1 and Android Gradle Plugin 8.9.1. This app uses native Android APIs and Java, with the bundled Apache-licensed AndroidSVG 1.4 library for SVG parsing and vector rendering. See `THIRD_PARTY.md`.

Run the dependency-free instrumentation checks on a connected emulator or TV:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.nearsky.tv.test/com.nearsky.tv.SmokeInstrumentation
```

See `VALIDATION.md` for the checks actually performed and their limitations.

## Custom SVG liveries

Version 1.1 uses the full landscape display with three equal-width cards. Every A–Z three-letter operator identifier has a deterministic, original design. Version 1.2 adds real airline names on the fuselage and card headings. Twelve verified common-carrier names are bundled for immediate offline use; other names are fetched from the exact-code adsbdb airline endpoint and cached locally. A code remains visible if its name cannot be verified. Successful lookups are refreshed after 24 hours while the display is active; failed lookups retry after 15 minutes. A code keeps the same palette, tail pattern, fuselage stripe and code markings across refreshes and devices.

- The separate `NearSky-SVG-liveries.zip` contains **17,576 standalone SVG files**, one for every possible AAA–ZZZ code. This is identifier coverage, not a claim that 17,576 airlines exist; unused and unassigned codes are deliberately included.
- The app generates SVGs on demand and keeps a bounded cache of vector drawing commands, so it does not need to bundle thousands of files or download artwork.
- A standard three-letter ICAO flight prefix followed by a flight number selects the design. If this is unavailable, the aircraft metadata's operator code can supply it. This identifies a code, not a guarantee about codeshares, wet-leased aircraft, or the paint currently on that aircraft.
- Registrations such as `N123AB` and `VT-ANB` are not treated as airline codes. With no usable operator identifier, the screen uses a neutral NS design and says the operator is unidentified.
- Each exported SVG has a transparent background, a 1200 × 440 viewBox, paths, gradients and ordinary system-font code lettering. There are no external images, downloaded fonts or scripts. The pack includes verified names for the 12 bundled carrier examples; other files retain code lettering until a verified name is supplied to the generator. The live app can resolve names for other carriers on demand.
- Twelve ready-to-edit examples are in `app/src/main/assets/liveries/`. All files in the full pack are editable in SVG/vector editors. Edit any of these files, or add a new uppercase `CODE.svg` file to that directory, and rebuild to override that operator's design. Keep the generated `<text id="airline-name">` element if you want the app to update its name dynamically. The app uses a valid SVG asset first and generates a design if the asset is missing or invalid. To change all generated designs, edit `LiveryDesign.svg()`.

Regenerate the complete pack with JDK 17:

```sh
./tools/export-liveries.sh /absolute/path/NearSky-SVG-liveries.zip
```

The export tool uses the exact same `LiveryDesign.java` source as the Android app. This avoids a second illustration implementation drifting away from what the TV shows.

## Sources

- Live positions: [adsb.fi](https://adsb.fi), [open data API and personal/non-commercial use terms](https://github.com/adsbfi/opendata). Requests stay well below one per second. Review provider terms before distribution or commercial use.
- Routes, airline names, aircraft metadata and photo lookup: [adsbdb](https://www.adsbdb.com/), [API source and documentation](https://github.com/mrjackwills/adsbdb).
- Aircraft reference photographs: [Airport-data.com](https://www.airport-data.com/), copyright their respective photographers. Images remain remotely hosted; credits and individual image URLs are available in the app.
- Screensaver platform: Android [DreamService](https://developer.android.com/reference/android/service/dreams/DreamService).
- Aircraft concept liveries: original SVG artwork generated locally by this application; no official logos or real livery assets are included.

NearSky has been installed and opened on your TCL TV. Manufacturer-specific screensaver selection and idle behavior still need to be checked there.

Airline-name provenance and public brand spellings are recorded in `app/src/main/assets/airline-names-provenance.json`. Airline names are resolved by operator code, never by substituting the registered aircraft owner's name. Cached names share the app's backup exclusions.
