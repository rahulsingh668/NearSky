#!/usr/bin/env python3
"""Build the offline searchable inventory, artwork gallery and real-code SVG export."""
from pathlib import Path
import csv,html,json,math,re,zipfile
ROOT=Path(__file__).resolve().parents[1]
catalogue=json.loads((ROOT/'app/src/main/assets/livery-catalogue.json').read_text())['airlines']
records=list(csv.DictReader((ROOT/'data/airlines/airline-identities.csv').open()))
stats=json.loads((ROOT/'data/airlines/summary.json').read_text())

# A GitHub-readable listing retains identities because one code may have multiple owners over time.
md=['# Airline code inventory','',
    f"Snapshot: {stats['snapshot_date']}. **{len(records):,} identity/code records**, **{stats['unique_airline_codes']:,} distinct ICAO codes**. This includes historical airlines and is not a verified current global census.",'',
    'Use [the searchable offline gallery](AIRLINES.html), [CSV list](../data/airlines/airline-identities.csv), [operating review queue](../data/airlines/operating-review-queue.csv) or [source notes](../data/airlines/README.md).', '',
    'Operating airlines have priority. “Unverified” means no closure date was found; it does **not** confirm that an airline operates today. “Draft” means an airline-specific colour illustration has been built and needs visual refinement. Historical identities do not inherit artwork from a new owner of the same code.','',
    '| ICAO | IATA | Airline identity | Country | Status | Artwork |','| --- | --- | --- | --- | --- | --- |']
def safe(s):return s.replace('|','/').replace('\n',' ')
for r in records:
    md.append('| '+' | '.join([r['icao'],safe(r['iata']),f"[{safe(r['name'])}]({r['identity']})",safe(r['country']),r['status'].replace('_',' '),r['livery_status'].replace('_',' ')])+' |')
(ROOT/'docs/AIRLINES.md').write_text('\n'.join(md)+'\n')

payload=json.dumps({'records':records,'art':catalogue},ensure_ascii=False).replace('<','\\u003c')
page='''<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>NearSky airline inventory & livery review</title>
<style>:root{color-scheme:dark;font:16px system-ui,sans-serif;background:#0b141d;color:#edf2ee}body{max-width:1500px;margin:auto;padding:30px}h1{font-size:32px}p{line-height:1.55;color:#b7c9d2}a{color:#8cd8ef}input,select,button{font:inherit;padding:12px;border:1px solid #617787;border-radius:8px;background:#162a36;color:#edf2ee}input{min-width:280px;flex:1}.controls{display:flex;gap:12px;flex-wrap:wrap;position:sticky;top:0;background:#0b141d;padding:16px 0;z-index:1}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:16px}.card{background:#182a36;border:1px solid #304a59;border-radius:14px;padding:18px}.card img{width:100%;aspect-ratio:1200/440;object-fit:contain}.card h2{font-size:20px;margin:8px 0}.card p{font-size:14px;margin:8px 0}.code{font:14px ui-monospace,monospace;color:#8cd8ef}.badge{display:inline-block;padding:4px 7px;border:1px solid #6c7e88;border-radius:5px;font-size:12px}.empty{height:100px;display:grid;place-items:center;color:#8ca5b3}table{width:100%;border-collapse:collapse}td,th{text-align:left;padding:12px;border-bottom:1px solid #304a59}th{font-size:13px}button:focus-visible,input:focus-visible,select:focus-visible,a:focus-visible{outline:3px solid #f1ce71;outline-offset:3px}@media(max-width:650px){body{padding:16px}input{min-width:180px}td:nth-child(4),th:nth-child(4){display:none}}</style>
<h1>NearSky · Airline codes & livery review</h1><p id="summary"></p><p>Operating airlines first. The inventory includes historical identities. Absence of a closure date is not proof of current operations; sources can be incomplete. Artwork uses a shared airliner silhouette, simplified tail marks and outlined substitute English lettering. It does not identify an individual aircraft’s paint.</p>
<p><a href="../data/airlines/airline-identities.csv">Download airline CSV</a> · <a href="../data/airlines/operating-review-queue.csv">Operating review queue</a> · <a href="../data/airlines/README.md">Sources & limitations</a></p>
<div class="controls"><input id="search" type="search" aria-label="Search airline, ICAO, IATA or country" placeholder="Search airline, ICAO, IATA or country"><select id="view" aria-label="View"><option value="art">Built artwork</option><option value="queue">Operating status review</option><option value="all">All airline identities</option><option value="historical">Historical identities</option></select><select id="status" aria-label="Artwork status"><option value="any">All artwork statuses</option><option value="bundled">Original 24 designs</option><option value="bundled_draft">New illustration drafts</option><option value="reference_pending">Artwork pending</option></select></div><p role="status" id="count"></p><main id="results"></main>
<script id="inventory" type="application/json">PAYLOAD</script><script>
const data=JSON.parse(document.getElementById('inventory').textContent),$=id=>document.getElementById(id);
const esc=s=>String(s||'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
$('summary').textContent=`22 October 2026 · ${data.records.length.toLocaleString()} identity/code records · ${data.art.length} built airline illustrations (${data.art.filter(r=>r.review_status==='bundled_draft').length} new drafts)`;
function render(){const q=$('search').value.trim().toLowerCase(),view=$('view').value,status=$('status').value;
let rows=view==='art'?data.art.map(r=>({...data.records.find(x=>x.icao===r.icao&&x.livery_status===r.review_status),...r,livery_status:r.review_status})):data.records.filter(r=>view==='all'||(view==='historical'?r.status==='historical':r.status==='operating_status_unverified'));
rows=rows.filter(r=>(status==='any'||r.livery_status===status)&&[r.icao,r.iata,r.name,r.country].join(' ').toLowerCase().includes(q));
$('count').textContent=`${rows.length.toLocaleString()} matching ${view==='art'?'illustrations':'identity/code records'}`;
if(view==='art'){$('results').className='grid';$('results').innerHTML=rows.map(r=>`<article class="card"><img loading="lazy" src="../app/src/main/assets/liveries/${esc(r.icao)}.svg" alt="${esc(r.name)} representative airline illustration"><span class="code">${esc(r.icao)}</span><h2>${esc(r.name)}</h2><span class="badge">${r.review_status==='bundled_draft'?'New draft · visual review pending':'Existing design'}</span><p>${esc(r.scheme)}</p><p><a href="../app/src/main/assets/liveries/${esc(r.icao)}.svg">Open SVG</a> · <a href="${esc(r.reference)}" target="_blank" rel="noopener noreferrer">Brand reference</a></p></article>`).join('');}
else{$('results').className='';$('results').innerHTML='<table><thead><tr><th>ICAO / IATA</th><th>Airline identity</th><th>Status / artwork</th><th>Country</th></tr></thead><tbody>'+rows.map(r=>`<tr><td class="code">${esc(r.icao)} / ${esc(r.iata)}</td><td><a href="${esc(r.identity)}" target="_blank" rel="noopener noreferrer">${esc(r.name)}</a></td><td>${esc(r.status.replaceAll('_',' '))}<br><small>${esc(r.livery_status.replaceAll('_',' '))}</small></td><td>${esc(r.country)}</td></tr>`).join('')+'</tbody></table>';}}
for(const id of ['search','view','status'])$(id).addEventListener(id==='search'?'input':'change',render);render();
</script></html>'''.replace('PAYLOAD',payload)
(ROOT/'docs/AIRLINES.html').write_text(page)

# A real-code-only pack is distinct from the legacy AAA–ZZZ concept pack.
with zipfile.ZipFile(ROOT.parent/'NearSky-real-airline-liveries.zip','w',zipfile.ZIP_DEFLATED) as z:
    for r in catalogue:z.write(ROOT/f"app/src/main/assets/liveries/{r['icao']}.svg",f"liveries/{r['icao']}.svg")
    z.write(ROOT/'app/src/main/assets/livery-catalogue.json','catalogue.json')
    z.writestr('README.txt',f"NearSky: {len(catalogue)} real-airline-code illustrations. Includes 24 existing drawings and {len(catalogue)-24} new review drafts. This is not every airline worldwide. Simplified motifs and substitute lettering; representative airframe, not exact fleet paint. See catalogue.json for per-design status and references. Brand marks belong to their owners.\n")

# Paged SVG contact sheets avoid an enormous, unreadable single image.
for page_no,start in enumerate(range(0,len(catalogue),24),1):
    items=catalogue[start:start+24]; height=120+math.ceil(len(items)/4)*252
    svg=[f'<svg xmlns="http://www.w3.org/2000/svg" width="1920" height="{height}" viewBox="0 0 1920 {height}"><rect width="1920" height="{height}" fill="#0B141D"/><text x="40" y="52" font-family="sans-serif" font-size="28" fill="#EDF2EE">NEARSKY / AIRLINE ILLUSTRATIONS · {page_no}</text><text x="40" y="82" font-family="sans-serif" font-size="17" fill="#9CB1BE">24 existing schemes + 67 new drafts · simplified motifs and substitute lettering · visual review pending for drafts</text>']
    for i,r in enumerate(items):
        x=24+(i%4)*474;y=112+(i//4)*252
        art=(ROOT/f"app/src/main/assets/liveries/{r['icao']}.svg").read_text()
        for ident in re.findall(r'\bid="([^"]+)"',art):
            art=art.replace(f'id="{ident}"',f'id="{r["icao"]}-{ident}"').replace(f'url(#{ident})',f'url(#{r["icao"]}-{ident})').replace(f'href="#{ident}"',f'href="#{r["icao"]}-{ident}"')
        art=re.sub(r'<svg\b[^>]*>',f'<svg x="{x+6}" y="{y+14}" width="438" height="180" viewBox="0 0 1200 440">',art,count=1)
        svg.append(f'<rect x="{x}" y="{y}" width="456" height="234" rx="14" fill="#182A36"/>'+art+f'<text x="{x+18}" y="{y+217}" font-family="sans-serif" font-size="18" fill="#EDF2EE">{html.escape(r["name"])}</text><text x="{x+436}" y="{y+217}" text-anchor="end" font-family="sans-serif" font-size="14" fill="#8FA7B7">{r["icao"]}</text>')
    svg.append('</svg>');(ROOT/f'docs/screenshots/livery-sheet-{page_no}.svg').write_text(''.join(svg))
print(f'Built searchable gallery, Markdown index, {math.ceil(len(catalogue)/24)} sheets and {len(catalogue)}-SVG real-code pack.')
