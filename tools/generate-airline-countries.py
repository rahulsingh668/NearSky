#!/usr/bin/env python3
"""Generate offline company-country seeds for the bundled illustrations.

Uses the source-linked CC0 research snapshot, with explicit major-list country
overrides retained beside it. Runtime exact-ICAO airline responses can refresh
these values. Country is never derived from aircraft registration or location.
"""
from pathlib import Path
import csv,json
ROOT=Path(__file__).resolve().parents[1]
rows=list(csv.DictReader((ROOT/'data/airlines/airline-identities.csv').open()))
overrides=json.loads((ROOT/'data/airlines/company-country-overrides.json').read_text())
records={}
for r in rows:
    if not r['livery_status'].startswith('bundled'):continue
    country=r['country'];source=r['identity']
    if r['icao'] in overrides:
        country=overrides[r['icao']]['country'];source=overrides[r['icao']]['source']
    assert country and ';' not in country, f"Review ambiguous country: {r['icao']}"
    records[r['icao']]={'country':country,'source':source}
(ROOT/'app/src/main/assets/airline-countries.json').write_text(json.dumps({
    'snapshot_date':'2026-10-23','description':'Company home country/territory, not aircraft location or flight classification. Runtime exact-code metadata may refresh these seeds.',
    'airlines':records},indent=2,ensure_ascii=False)+'\n')
print(f'Generated {len(records)} company-country seeds.')
