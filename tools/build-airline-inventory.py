#!/usr/bin/env python3
"""Normalize public airline identities; never infer active status from a missing end date.

Usage: python3 tools/build-airline-inventory.py SNAPSHOT_DIRECTORY
The directory must contain wikidata.json, faa.html, major-airlines.json.
See data/airlines/README.md for the query, source URLs and limitations.
"""
from pathlib import Path
from html.parser import HTMLParser
import csv, json, re, sys, collections

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'data/airlines'
SOURCE = Path(sys.argv[1])
OUT.mkdir(parents=True, exist_ok=True)
DATE = '2026-10-22'

class Rows(HTMLParser):
    def __init__(self):
        super().__init__(); self.rows=[]; self.cells=None; self.cell=None
    def handle_starttag(self, tag, attrs):
        if tag=='tr': self.cells=[]
        if tag=='td' and self.cells is not None: self.cell=[]
    def handle_data(self, text):
        if self.cell is not None: self.cell.append(text)
    def handle_endtag(self, tag):
        if tag=='td' and self.cell is not None:
            self.cells.append(' '.join(''.join(self.cell).split())); self.cell=None
        if tag=='tr' and self.cells is not None:
            if len(self.cells)==4 and re.fullmatch('[A-Z]{3}', self.cells[0]): self.rows.append(self.cells)
            self.cells=None

parser=Rows(); parser.feed((SOURCE/'faa.html').read_text())
faa=collections.defaultdict(list)
for code,name,country,callsign in parser.rows:
    r={'icao':code,'operator':name,'country':country,'callsign':callsign}
    if r not in faa[code]: faa[code].append(r)

catalogue=json.loads((ROOT/'app/src/main/assets/livery-catalogue.json').read_text())['airlines']
art={r['icao']:r for r in catalogue}
major={r['icao']:r for r in json.loads((SOURCE/'major-airlines.json').read_text())}
grouped={}
labels_path=SOURCE/'missing-labels.json'
fallback_labels=json.loads(labels_path.read_text()).get('entities',{}) if labels_path.exists() else {}
for binding in json.loads((SOURCE/'wikidata.json').read_text())['results']['bindings']:
    b={k:v['value'] for k,v in binding.items()}; code=b['icao'].strip().upper()
    if not re.fullmatch('[A-Z]{3}',code): continue
    qid=b['airline'].rsplit('/',1)[1]; key=(qid,code)
    if b['airlineLabel']==qid:
        labels=fallback_labels.get(qid,{}).get('labels',{})
        if labels:b['airlineLabel']=next(iter(labels.values()))['value']
    if key not in grouped:
        grouped[key]={'icao':code,'name':b['airlineLabel'],'iata':set(),'country':set(),'ended':set(),
                      'identity':'https://www.wikidata.org/wiki/'+qid}
    r=grouped[key]
    for field,target in [('iata','iata'),('countryLabel','country'),('ended','ended')]:
        if field in b: r[target].add(b[field])

records=[]
# Explicit identity aliases; a code match alone is insufficient when codes are reused.
aliases={'AMX':'Q381563','AZU':'Q794272','CFG':'Q312517','ELY':'Q208489','GLO':'Q691637',
         'KAL':'Q213147','MEA':'Q859510','SAS':'Q187854','SWR':'Q156776'}
for r in grouped.values():
    r.update({k:'; '.join(sorted(r[k])) for k in ('iata','country','ended')})
    r['status']='historical' if r['ended'] else 'operating_status_unverified'
    r['status_source']=r['identity']
    r['snapshot_date']=DATE
    r['faa_assignment_present']='yes' if r['icao'] in faa else 'no'
    r['livery_status']='deferred_historical' if r['ended'] else 'reference_pending'
    # A code can be reassigned. Do not attach today's illustration to historical identities.
    if not r['ended'] and r['icao'] in art:
        current=major.get(r['icao'],{}).get('airline',art[r['icao']]['name'])
        def clean(s): return re.sub('[^a-z0-9]','',s.lower()).replace('airlines','').replace('airways','')
        if clean(r['name'])==clean(current) or clean(r['name'])==clean(art[r['icao']]['name']) or r['identity'].endswith('/'+aliases.get(r['icao'],'NO_MATCH')):
            r['livery_status']=art[r['icao']].get('review_status','bundled')
    # Explicitly researched exceptions, with primary evidence.
    if r['icao']=='NKS' and 'spirit' in r['name'].lower():
        r.update(status='historical',ended='2026-05-02',livery_status='deferred_historical',
                 status_source='https://www.spiritrestructuring.com/')
    if r['icao']=='HAL' and 'hawaiian' in r['name'].lower():
        r.update(status='brand_operated_under_ASA',livery_status='deferred_legacy_code',
                 status_source='https://news.alaskaair.com/company/alaska-airlines-and-hawaiian-airlines-reach-major-integration-milestone-a-single-operating-certificate/')
    records.append(r)

# Retain known bundled identities not yet classified as airline instances in Wikidata.
for code,r in art.items():
    if not any(x['icao']==code and x['livery_status'].startswith('bundled') for x in records):
        m=major.get(code,{})
        records.append({'icao':code,'name':r['name'],'iata':m.get('iata',''),'country':m.get('country',''),
            'ended':'','identity':r['reference'],'status':'operating_status_unverified',
            'status_source':r['reference'],'snapshot_date':DATE,'faa_assignment_present':'yes' if code in faa else 'no',
            'livery_status':r.get('review_status','bundled')})
records.sort(key=lambda r:(r['icao'],r['name']))

def save_csv(path,rows,fields):
    with path.open('w',newline='') as f:
        writer=csv.DictWriter(f,fieldnames=fields); writer.writeheader(); writer.writerows(rows)

fields=['icao','name','iata','country','status','ended','livery_status','faa_assignment_present','identity','status_source','snapshot_date']
save_csv(OUT/'airline-identities.csv',records,fields)
operators=[r for code in sorted(faa) for r in faa[code]]
save_csv(OUT/'assigned-operators.csv',operators,['icao','operator','country','callsign'])
queue=[r for r in records if r['status']=='operating_status_unverified']
save_csv(OUT/'operating-review-queue.csv',queue,fields)
stats={'snapshot_date':DATE,'airline_identity_code_pairs':len(records),'unique_airline_codes':len({r['icao'] for r in records}),
       'historical_identities':sum(r['status']=='historical' for r in records),'status_unverified_identities':len(queue),
       'assigned_operator_rows':len(operators),'assigned_operator_codes':len(faa),'bundled_artwork_codes':len(art),
       'note':'Not a verified current global airline census. Codes may be reused; no end date is not evidence of operation.'}
(OUT/'summary.json').write_text(json.dumps(stats,indent=2)+'\n')
print(json.dumps(stats,indent=2))
