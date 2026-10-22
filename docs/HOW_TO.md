# Build, install and use NearSky

NearSky is a native Android TV / Google TV screensaver. Version 1.4.1 shows the three nearest airborne aircraft across a horizontal TV, with SVG airline illustrations, airline names, flight details, and reference origin/destination routes. The bottom-right credit reads **Developed by Rahul Singh**.

See the [screenshot gallery](SCREENSHOTS.md) for the live display, menus and settings on a real TCL TV.

## 1. Get the source

```sh
git clone https://github.com/rahulsingh668/NearSky.git
cd NearSky
```

## 2. Build an APK

Install JDK 17 and Android Studio. In Android Studio's SDK Manager install **Android SDK Platform 35** and **Android SDK Build-Tools 36.0.0**. Open the cloned repository as a project and allow Gradle to sync. The first build needs Internet access to download Gradle and build dependencies.

Set `ANDROID_HOME` to your Android SDK installation, or create an untracked `local.properties` in the repository root:

```properties
sdk.dir=/absolute/path/to/your/android/sdk
```

For a Windows SDK path in `local.properties`, use forward slashes, for example `C:/Users/YourName/AppData/Local/Android/Sdk`.

Build on macOS/Linux:

```sh
./gradlew assembleDebug lintDebug
```

Build on Windows:

```powershell
.\gradlew.bat assembleDebug lintDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. It is debug-signed and suitable for personal sideloading. There is no Play Store release or APK download bundled in this source repository.

## 3. Install on your TV

Your TV must run Android TV or Google TV, Android 8.0 or newer. Connect the computer and TV to the same local network. Install Android SDK Platform-Tools on your computer and make `adb` available in your terminal.

Enable Developer Options on the TV by opening its **About** screen and pressing the Android TV OS build / Build entry repeatedly until developer mode is enabled. Locate USB debugging, network debugging or wireless debugging in Developer Options; wording varies by TCL model and Android version.

For a TV offering wireless pairing, use the addresses shown on the TV:

```sh
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:DEBUGGING_PORT
```

For older network-debugging TVs:

```sh
adb connect TV_IP:5555
```

Accept the debugging authorization prompt on your own TV, then run:

```sh
adb devices
adb -s TV_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
adb -s TV_SERIAL shell am start -n com.nearsky.tv/.MainActivity
```

Replace `TV_SERIAL` with the exact device identifier from `adb devices`, typically an IP address plus port. `-r` retains the installed app's settings when the APK has the same signing key. A fresh computer normally uses a different debug signing key: an `INSTALL_FAILED_UPDATE_INCOMPATIBLE` error means you need the original signing key to update in place. Alternatively, uninstall the previous version on the TV and install the new APK; uninstalling deletes the saved app settings.

A file-transfer app is another option: transfer the APK, authorize that app to install unknown apps, and open the transferred APK on the TV.

## 4. Set the location and radius

1. Open NearSky from the TV apps list.
2. Choose **Location & radius**.
3. Choose a city preset or enter a display name, latitude and longitude. A display name does not automatically look up coordinates.
4. Default: Ghaziabad, latitude `28.6692`, longitude `77.4538`, radius **50 km**. Radius can be 1–200 km.
5. Save, then select **Full-screen preview**. Back reveals the menu; Menu opens location settings.

SVG airline illustrations are the default. **Prefer photos instead of custom SVG liveries** enables optional aircraft reference photos. **Try sample flights** demonstrates the layout with a visible DEMO indicator; the actual screensaver always uses live positions.

## 5. Use it as the idle screensaver

Choose **Set as screensaver** in NearSky. In the TV's Screen saver / Ambient Mode settings, select **NearSky · Nearby aircraft**, set a start delay, then use **Start now** / **Preview** if available.

Return to the TV launcher to test idle activation. The app's fullscreen preview deliberately keeps the display awake. Set the screensaver delay earlier than the TV's automatic power-off delay, and disable Developer Options → Stay awake if enabled.

Some manufacturers hide third-party screensaver selection. The [README's screensaver section](../README.md#select-as-the-idle-screensaver) documents optional ADB configuration and how to record and restore the previous values. These system changes are separate from installing the APK; NearSky does not change them automatically.

## 6. Understand the display

- Aircraft are sorted by horizontal distance to your selected coordinates, with a maximum of three cards and a strict selected radius.
- Positions refresh 15 seconds after the preceding request completes. Network timeouts can increase that interval. Grounded aircraft, invalid positions and positions older than 60 seconds are excluded.
- Routes come from an exact callsign lookup in adsbdb. Airport IATA codes are preferred, with ICAO codes as fallback. City names fall back to airport names. **Route unavailable** means the provider has no usable match or the lookup failed.
- Routes are reference data and can be outdated, reused or inconsistent with the current flight, including during diversions. They are not verified live departure/arrival records. Positive routes are cached in memory for 30 minutes; failed lookups retry after 5 minutes while visible.
- With fewer than three nearby aircraft, empty slots remain empty. The app does not fill live mode with fictional flights.
- The 24 bundled carriers use hand-redrawn standard airline colours, simplified tail motifs and enlarged outlined English lettering. Unknown carriers retain labeled concept artwork. All use a representative shared silhouette; individual fleet paint and aircraft types can differ.

## 7. Customize the artwork or footer

- Change the footer string in `app/src/main/java/com/nearsky/tv/FlightView.java`.
- Edit an aircraft SVG in `app/src/main/assets/liveries/`, or add an uppercase three-letter `CODE.svg` there. Preserve its 1200 × 440 viewBox. Standard-livery assets use fixed outlined lettering; generic concept fallbacks use the `airline-name` text element for dynamic names.
- Edit `tools/generate-liveries.py` and run it with Python 3 / JDK 17 to regenerate the 24 standard schemes and brand outline references. Live card names use the normal font in `FlightView.java`. Edit `LiveryDesign.java` for unbundled concept fallbacks.
- Generate the complete AAA–ZZZ SVG identifier pack with JDK 17:

```sh
./tools/export-liveries.sh /absolute/path/NearSky-SVG-liveries.zip
```

The pack includes 17,576 possible identifiers, including unused codes. It is not a list of 17,576 actual airlines. Rebuild and reinstall the APK after changing source or assets.

## 8. Verify changes

```sh
./gradlew assembleDebug assembleDebugAndroidTest lintDebug
adb -s TV_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
adb -s TV_SERIAL install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s TV_SERIAL shell am instrument -w com.nearsky.tv.test/com.nearsky.tv.SmokeInstrumentation
```

Instrumentation can interrupt the app; reopen NearSky afterwards. See [VALIDATION.md](../VALIDATION.md) for historical test results and which checks were actually performed for each version. Manufacturer idle activation and long-duration panel behavior require testing on the target TV.

## Troubleshooting

| Symptom | Check |
| --- | --- |
| TV is missing or offline in ADB | Same local network, current IP/port, debugging enabled and authorization accepted. Pairing and connection ports may differ. |
| No aircraft | Verify coordinates, radius and Internet access. Provider coverage may be empty. |
| Feed delayed | Check the TV connection and provider availability; expired positions are removed automatically. |
| Missing route, airline name or photo | Metadata coverage is separate from live positions; unavailable fields are not invented. |
| Screensaver never starts | Select NearSky in TV settings, return to the launcher, check idle/power delays and disable developer Stay awake. |
| Update signature mismatch | Use the original signing key or uninstall the old app before installing; uninstalling clears settings. |

## Data sources and privacy

Positions: adsb.fi. Routes, airline names and aircraft metadata: adsbdb. Optional photographs: Airport-data.com. Check [README.md](../README.md#sources) and [THIRD_PARTY.md](../THIRD_PARTY.md) for provider links, personal/non-commercial use terms and bundled library notices.

Selected coordinates are sent to adsb.fi. Callsigns, aircraft hex identifiers and operator codes are sent to adsbdb. Optional image requests go to the photo provider. These services receive your IP address. NearSky requires Internet access but no GPS permission, account or API key, and stops polling when the display stops. It is not an aviation navigation tool.
