# Real airline identities and artwork queue

Snapshot: **22 October 2026**. Operating airlines have priority for illustration work. This is a source-linked research inventory, **not a verified current global airline census**.

- `airline-identities.csv`: named airline identity/code pairs from Wikidata, plus bundled identities absent from that classification. One ICAO code may appear more than once because codes are reused or source statements conflict. Each identity retains its own link. Invalid non-three-letter values are omitted.
- `operating-review-queue.csv`: identities without a recorded closure date. **Missing closure information does not prove current operation.** This queue requires operating-status verification, name/code collision review and current fleet imagery before artwork is considered complete.
- `assigned-operators.csv`: FAA factual assignment table, including military, private/business aviation, manufacturers, flight schools and historical companies. These rows must **not** be counted as airlines automatically.
- `summary.json`: generated counts for this snapshot, not a claim of completeness.
- `sources.json`: retrieval date and SHA-256 hashes of the downloaded source snapshots.
- `name-fallbacks.json`: eight Wikidata label lookups in additional languages, preserving names missing from the query language list. Copy it to the snapshot folder as `missing-labels.json` when rebuilding.
- `wikidata-query.sparql`: reproducible airline-instance/subclass query using ICAO `P230`, IATA `P229`, country `P17` and dissolution `P576`. The query is intentionally broad; multiple values can produce multiple results, which the normalizer groups by identity and code. Date values are source statements, not independently verified closure dates.

## Status and artwork meanings

`historical`: a closure date is recorded, or primary evidence confirms operations ended. `operating_status_unverified`: there is no closure date in this snapshot; independent verification remains necessary. `brand_operated_under_ASA`: Hawaiian continues as a brand under Alaska's operating certificate; HAL is deferred as a legacy code, not counted as a separate current operator.

`bundled`: one of the 24 pre-existing representative illustrations. `bundled_draft`: one of the 67 new airline-specific illustration drafts; simplified motifs and substitute lettering need visual review. `reference_pending`: no matching airline illustration has been completed. `deferred_historical` / `deferred_legacy_code`: retained in the list but behind current operators in the artwork queue. Neither `bundled` status is a promise of exact fleet paint.

The 91-code artwork batch comes from the union of the existing 24 schemes and a public 86-major-airline list, excluding NKS and HAL from the expansion. That public list is a **starting set**, not an exhaustive airline register and not independent proof of current operations. The larger Wikidata queue includes many smaller carriers, cargo operators, subsidiaries and airlines whose status is uncertain.

Important exceptions researched against primary sources:

- [Spirit announcement](https://www.spiritrestructuring.com/): operations wound down from 2 May 2026. NKS remains historical and is not in the new artwork batch.
- [Alaska / Hawaiian single operating certificate](https://news.alaskaair.com/company/alaska-airlines-and-hawaiian-airlines-reach-major-integration-milestone-a-single-operating-certificate/): the two brands continue under the Alaska certificate. Matching an ASA flight to a Hawaiian-painted aircraft requires aircraft-level information; an operator-code illustration cannot resolve that accurately.

## Sources and reuse

1. [Wikidata Query Service](https://query.wikidata.org/) — structured identity data is [CC0](https://www.wikidata.org/wiki/Wikidata:Licensing). Community-maintained, incomplete and potentially stale. Query results are not equivalent to the official ICAO register. Airline classification can include mixed charter/business operators.
2. [FAA Chapter 3, Section 3](https://www.faa.gov/air_traffic/publications/atpubs/cnt_html/chap3_section_3.html) — factual company, country and callsign assignments in a US Federal publication. Presence does not confirm an airline's operating status or current paint.
3. [AviationShop airline-codes](https://github.com/AviationShop/airline-codes) — 86-major-airline starting list, published under CC0-1.0. It includes outdated entries, which is why it is not used as an active-status authority.

These source notes apply to the static code inventory. They **do not change the separate terms for live positions, route data or photos**. Airline marks also remain their owners' property.

## Rebuild

Download the three snapshots into a working directory as `wikidata.json`, `faa.html`, `major-airlines.json`. Send `wikidata-query.sparql` to the Query Service with `Accept: application/sparql-results+json`. Then run from the project directory:

```sh
python3 tools/generate-liveries.py
python3 tools/build-airline-inventory.py /path/to/snapshots
python3 tools/build-airline-gallery.py
```

The snapshots used for this release were retrieved on the date above; later queries will differ. Keep the snapshot date, source hashes and manually verified status exceptions up to date when refreshing. The inventory is documentation/research data; it is not loaded into the TV app as an assertion of current airline identities.
