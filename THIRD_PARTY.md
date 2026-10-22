# Third-party notices

## AndroidSVG 1.4

SVG parsing and rendering: AndroidSVG by Paul LeBeau and contributors.

- Project: https://bigbadaboom.github.io/androidsvg/
- Source: https://github.com/BigBadaboom/androidsvg
- Maven artifact: com.caverock:androidsvg:1.4
- License: Apache License, Version 2.0. The full text is bundled at `app/src/main/assets/Apache-2.0.txt` and included in this source archive.
- The unmodified upstream JAR is included in `app/libs/androidsvg-1.4.jar` so builds can use the same renderer without fetching an extra dependency.

## Airline illustrations and lettering

The 24 bundled aircraft illustrations are hand-redrawn representations of standard airline schemes. Their English lettering and simplified tail marks are approximations, not official brand master assets or a guarantee of a particular aircraft's paint. Airline names and marks belong to their respective owners; no endorsement is implied. The unbundled identifier fallbacks remain original concept artwork.

Build-time lettering outlines use Liberation Sans and Liberation Serif. The font files are included under `tools/fonts/` solely for regeneration; they are not copied into the Android APK. Copyright and full license notices are in `tools/fonts/LICENSE.txt` and `app/src/main/assets/LICENSE-Liberation.txt`. Liberation is licensed under the SIL Open Font License 1.1. Outlined lettering has no external font dependency when displayed.

Operator identifiers and flight metadata come from the providers described in README.md.

## Static airline research inventory

`data/airlines/` includes normalized Wikidata structured data (CC0), factual FAA company-code assignments and an AviationShop CC0 major-carrier starting set. See [source notes](data/airlines/README.md) and `sources.json` for URLs, date, hashes and limitations. These data notes do not license live positions, routes, photographs or airline marks. No claim of an exhaustive current active-airline register is made.
