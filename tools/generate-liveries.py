#!/usr/bin/env python3
"""Rebuild the bundled airline illustration catalogue. Python 3 + JDK 17 only."""
from pathlib import Path
import html,json,re,subprocess,tempfile,xml.etree.ElementTree as ET,importlib.util
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'app/src/main/assets'
BASE=(ROOT/'tools/approved-indigo.svg').read_text()
BODY='M89 246C137 232 199 218 280 218H963C1026 218 1063 225 1094 245C1120 260 1141 272 1134 284C1126 303 1076 314 1018 315H294C219 312 144 285 89 246Z'
TAIL='M164 251L83 49Q77 37 91 37H122L314 249Z'
# English markings are redrawn approximations; no claim of official brand master assets.
CAT=[
('IGO','IndiGo','IndiGo','#203F91','#7EC4E7','Regular','https://www.airbus.com/en/newsroom/press-releases/2019-10-indigo-signs-for-300-a320neo-family-aircraft'),
('AIC','Air India','AIR INDIA','#D31B32','#B69B58','Bold','https://www.airindia.com/in/en/newsroom/press-release/airbus-a350-inauguration-scindia.html'),
('SEJ','SpiceJet','SpiceJet','#E51D2A','#F5B334','BoldItalic','https://sg-images.spicejet.com/Magazine/SJ_April23.pdf'),
('AKJ','Akasa Air','Akasa Air','#F36D30','#4C2476','Bold','https://www.akasaair.com/'),
('AXB','Air India Express','air india express','#F26B32','#17A2A0','Bold','https://www.tata.com/newsroom/business/tales-of-india-air-india-express'),
('BAW','British Airways','BRITISH AIRWAYS','#132952','#D51C43','Regular','https://www.britishairways.com/'),
('DLH','Lufthansa','Lufthansa','#071D49','#FFFFFF','Bold','https://newsroom.lufthansagroup.com/en/right-on-time-for-the-end-of-the-100th-crane-birthday-first-airbus-a380-in-new-lufthansa-design/'),
('ETD','Etihad Airways','ETIHAD','#A78C50','#D2BA85','Regular','https://www.youtube.com/watch?v=fp01PXJ5UmE'),
('QTR','Qatar Airways','QATAR','#681B46','#B6B7B9','Regular','https://www.qatarairways.com/en-au/press-releases/2006/Mar/aboutus_news_08march06.html'),
('SIA','Singapore Airlines','SINGAPORE AIRLINES','#092A50','#C8A35E','BoldItalic','https://www.singaporeair.com/'),
('THY','Turkish Airlines','TURKISH AIRLINES','#D71F2B','#1E3049','Bold','https://blog.turkishairlines.com/en/mesut-manioglu-pioneer-of-modern-graphic-design/'),
('UAE','Emirates','Emirates','#B89C54','#D52433','Bold','https://www.emirates.com/media-centre/emirates-unveils-new-signature-livery-for-its-fleet/'),
('KAL','Korean Air','KOREAN','#102F5D','#7CB5DE','Bold','https://news.koreanair.com/'),
('UPS','UPS','Worldwide Services','#472817','#CDA449','Regular','https://about.ups.com/'),
('FDX','FedEx Express','FedEx','#4D148C','#FF6600','Bold','https://www.fedex.com/content/dam/fedex/eu-europe/resources/sprievodca-sluzbami-fedex.pdf'),
('AFR','Air France','AIRFRANCE','#102450','#E7223C','Bold','https://corporate.airfrance.com/'),
('ANA','All Nippon Airways','ANA','#0753A5','#52B8E8','BoldItalic','https://www.ana.co.jp/eng/aboutana/press/2011/110622-1.html'),
('JAL','Japan Airlines','JAPAN AIRLINES','#131721','#D51D37','BoldItalic','https://press.jal.co.jp/en/release/201102/003357.html'),
('SVA','Saudia','SAUDIA','#087E52','#BEA36B','Bold','https://www.saudia.com/'),
('THA','Thai Airways','THAI','#5A237A','#D7B26C','Bold','https://www.thaiairways.com/static/common/pdf/royal_orchid_plus/Newletter/Newsletter_Apr-Jun2016_EN_lastest.pdf'),
('ALK','SriLankan Airlines','SriLankan','#006B9E','#D79824','BoldItalic','https://www.srilankan.com/'),
('OMA','Oman Air','OMAN AIR','#077D8C','#BAA064','Regular','https://www.omanair.com/'),
('FDB','flydubai','flydubai','#057BC1','#F37735','Regular','https://www.flydubai.com/en/help/our-fleet/'),
('GFA','Gulf Air','GULF AIR','#092A46','#BC9B54','Regular','https://www.gulfair.com/'),
]
spec=importlib.util.spec_from_file_location('additional',ROOT/'tools/additional-liveries.py')
additional=importlib.util.module_from_spec(spec);spec.loader.exec_module(additional)
ORIGINAL_CODES={r[0] for r in CAT}
CAT+=additional.CAT
assert len({r[0] for r in CAT})==len(CAT), 'Duplicate airline code in artwork catalogue'

def path(d,color,**kw):
 return '<path d="'+d+'" fill="'+color+'"'+''.join(' '+k.replace('_','-')+'="'+str(v)+'"' for k,v in kw.items())+'/>'
def circle(x,y,r,c):return f'<circle cx="{x}" cy="{y}" r="{r}" fill="{c}"/>'
def line(d,c,w=4):return path(d,'none',stroke=c,stroke_width=w,stroke_linecap='round',stroke_linejoin='round')
def text(s,x,y,size,c,weight='bold'):
 return f'<text x="{x}" y="{y}" font-family="sans-serif" font-size="{size}" font-weight="{weight}" fill="{c}">{html.escape(s)}</text>'
def inset(svg,x,y,w,h):
 return re.sub(r'<svg\b[^>]*>',lambda m:m.group(0).replace('>','',1)+f' x="{x}" y="{y}" preserveAspectRatio="xMinYMid meet" >',svg,count=1).replace(re.search(r'width="[^"]+"',svg)[0],f'width="{w}"',1).replace(re.search(r'height="[^"]+"',svg)[0],f'height="{h}"',1)
def bird(c):
 return path('M125 167L224 119L190 162L230 176L178 174L152 202L156 179L123 180L148 167Z',c)
def falcon(c):
 return path('M124 173L231 106L188 157L233 136L204 170L229 163L196 187L160 195L141 210L147 182Z',c)
def oryx(c):
 return path('M154 198L166 169L153 157L172 153L172 135L187 146L203 139L197 157L214 165L196 174L181 200L176 180Z',c)+line('M178 148Q153 104 148 70M187 147Q182 101 168 63',c,4)
def crane(c):
 return line('M143 167L192 148L217 139M174 154L151 132M166 160L181 181',c,6)+path('M133 164L158 155L148 174Z',c)
def tail_mark(code,a,b,logo):
 if code in additional.PRIMARY:return additional.tail(code,a,b,path,circle,line,text)
 if code=='AIC':
  return path(TAIL,a)+path('M104 164L164 105L232 164L164 224Z','#5B1C45')+line('M111 164L164 112L220 164L164 216Z',b,8)+line('M137 164L164 136L192 164L164 192Z',b,4)
 if code=='SEJ':
  return path(TAIL,a)+''.join(circle(132+j*17+i*5,119+i*18,5.4 if j%2==0 else 4.4,'#F9BD40') for i in range(5) for j in range(3))
 if code=='AKJ':
  return path(TAIL,b)+path('M121 202L162 105L199 166L230 184L181 167L161 145L142 202Z',a)
 if code=='AXB':
  return path(TAIL,a)+''.join(path(f'M{x} {y-12}L{x+13} {y}L{x} {y+12}L{x-13} {y}Z',b if (i+j)%2==0 else '#FFCF7F') for i,y in enumerate(range(90,245,31)) for j,x in enumerate(range(98,300,31)))
 if code=='BAW':
  return path(TAIL,a)+line('M91 77L284 246M86 235L230 107','#FFFFFF',32)+line('M91 77L284 246M86 235L230 107',b,15)+line('M169 69L191 253M85 165L262 165','#FFFFFF',30)+line('M169 69L191 253M85 165L262 165',b,15)
 if code=='DLH':return path(TAIL,a)+f'<circle cx="174" cy="164" r="43" fill="none" stroke="#FFFFFF" stroke-width="4"/>'+crane('#FFFFFF')
 if code=='ETD':
  return path(TAIL,'#CFB784')+''.join(path(d,c) for d,c in [('M75 40L172 154L131 247Z','#B59C6B'),('M75 40L122 40L172 154Z','#DCCDAB'),('M122 40L237 171L172 154Z','#8D7651'),('M172 154L237 171L313 251L220 248Z','#3D332A'),('M172 154L220 248L131 247Z','#E0C9A0'),('M237 171L313 251L279 192Z','#B99C64')])
 if code=='QTR':return path(TAIL,'#ECEDEF')+line('M95 175H254M99 182H261M103 189H270','#C7C8CA',3)+oryx(a)
 if code=='SIA':return path(TAIL,a)+falcon(b)
 if code=='THY':return path(TAIL,a)+circle(174,166,45,'#FFFFFF')+path('M143 176L199 125L184 159L215 166L179 176L160 200L165 177Z',a)
 if code=='UAE':return path(TAIL,'#FFFFFF')+path('M80 48H125L300 247H254Z','#D82133')+path('M91 122L171 85L194 112L103 155Z','#008346')+path('M105 166L213 135L238 162L116 194Z','#FFFFFF')+path('M118 198L251 180L293 245H137Z','#151B1D')
 if code=='KAL':return path(TAIL,'#89BDDF')+line('M138 178C121 121 194 108 207 150C221 195 158 212 147 176C136 141 187 129 196 159C203 184 171 192 163 173',a,10)
 if code=='UPS':return path(TAIL,a)+path('M130 123Q173 108 211 123V169Q202 197 172 211Q140 197 130 169Z',b)+path('M136 128Q172 117 205 128V168Q197 191 172 204Q146 191 136 168Z',a)+text('ups',141,174,39,b)
 if code=='FDX':return path(TAIL,a)+text('Fed',120,166,31,'#FFFFFF')+text('Ex',173,166,31,b)
 if code=='AFR':return path(TAIL,'#FFFFFF')+''.join(path(f'M{80+i*14} 65L{90+i*14} 65L{240+i*14} 250L{230+i*14} 250Z',a if i<3 else b) for i in range(4))
 if code=='ANA':return path(TAIL,a)+text('ANA',125,182,43,'#FFFFFF')
 if code=='JAL':return path(TAIL,'#FFFFFF')+circle(174,163,45,b)+crane('#FFFFFF')+text('JAL',150,193,20,'#FFFFFF')
 if code=='SVA':return path(TAIL,a)+line('M158 184L192 154M158 154L192 184',b,4)+path('M173 169V128M148 137Q160 112 175 126Q190 111 203 137M154 147Q161 130 175 136Q190 128 198 147',b,stroke=b,stroke_width=3)
 if code=='THA':return path(TAIL,a)+path('M172 111C136 125 143 149 166 152C142 152 136 185 172 204C210 185 202 154 179 152C202 147 205 124 172 111Z',b)+path('M172 140C154 153 154 169 172 183C190 169 190 153 172 140Z','#B9307C')
 if code=='ALK':return path(TAIL,'#FFFFFF')+path('M121 168Q151 111 202 125Q178 149 152 172Z','#D89022')+path('M129 183Q170 133 224 155Q187 175 149 192Z','#03835E')+path('M135 199Q187 158 242 187Q188 204 148 208Z','#087AAA')+path('M148 207L128 190L124 174L145 178L164 195Z','#951C3A')
 if code=='OMA':return path(TAIL,a)+''.join(line(f'M{124+i*8} {130+i*12}Q172 {100+i*10} {219+i*2} {147+i*10}',b,5) for i in range(4))
 if code=='FDB':return path(TAIL,a)+path('M78 80Q167 75 210 147L262 250H224L175 154Q135 105 90 117Z',b)
 if code=='GFA':return path(TAIL,b)+falcon('#F6F0DF')
 return path(TAIL,a)

def paint(code,a,b):
 if code in additional.PRIMARY:return additional.paint(code,a,b,path,line)
 if code in ('BAW','SIA','SVA','ANA'):
  if code=='BAW':return path('M75 288Q490 301 1146 293V340H75Z',a)
  if code=='SIA':return path('M74 270H1140V289H74Z',a)+path('M74 266H1140V270H74Z',b)+path('M74 289H1140V293H74Z',b)
  if code=='ANA':return path('M74 271H1140V280H74Z',a)+path('M74 280H1140V286H74Z',b)
  return path('M74 271H1140V278H74Z',a)+path('M74 279H1140V284H74Z',b)+path('M74 285H1140V291H74Z','#183B71')
 if code=='AIC':return path('M70 286Q200 271 345 318V340H70Z',a)+line('M78 285Q235 287 357 315',b,4)
 if code=='AKJ':return path('M77 228Q333 229 493 320H80Z',a)+path('M78 223Q254 241 383 314H80Z',b)
 if code=='AXB':return path('M75 270Q282 267 451 316H75Z',a)+line('M84 270Q259 273 406 312',b,5)
 if code=='THA':return path('M74 234Q255 252 448 316H74Z',a)+line('M80 246Q263 266 453 316','#BD3A82',8)+line('M78 237Q269 258 480 316',b,5)
 if code=='UPS':return path('M74 230Q279 238 503 316H74Z',a)+line('M83 237Q302 252 512 314',b,7)
 if code=='FDX':return path('M74 235Q165 231 264 316H74Z',a)
 if code=='OMA':return path('M75 276Q349 291 1144 301V340H75Z',a)+line('M77 272Q350 287 1140 297',b,4)
 if code=='GFA':return line('M75 290H1140',b,5)
 if code=='THY':return path('M88 267Q311 242 447 314Q284 276 114 288Z','#DEE3E8')
 return ''

def split_color(svg,color,fraction):
 w=float(re.search(r'width="([0-9.]+)"',svg)[1]);h=float(re.search(r'height="([0-9.]+)"',svg)[1])
 inner=re.sub(r'^<svg[^>]*>|</svg>$','',svg)
 overlay=re.sub(r'fill="#[A-Fa-f0-9]+"',f'fill="{color}"',inner)
 return svg.replace('</svg>',f'<defs><clipPath id="brand-split"><rect x="{w*fraction}" y="0" width="{w}" height="{h}"/></clipPath></defs><g clip-path="url(#brand-split)">{overlay}</g></svg>')

def recolor_engine(svg,color):
 # Keep the three-dimensional lighting while matching the carrier palette.
 svg=re.sub(r'(<linearGradient id="engine".*?</linearGradient>)',lambda m:re.sub(r'stop-color="#[A-Fa-f0-9]+"',f'stop-color="{color}"',m[0]),svg,flags=re.S)
 svg=svg.replace('fill="#203C8B"',f'fill="{color}"').replace('fill="#193A87"',f'fill="{color}"')
 return svg

with tempfile.TemporaryDirectory() as temp:
 temp=Path(temp)
 subprocess.run(['javac','-encoding','UTF-8','-d',str(temp),str(ROOT/'tools/OutlineLettering.java')],check=True)
 jobs=[]
 for code,name,label,a,b,style,reference in CAT:
  if code=='IGO':continue
  font=ROOT/'tools/fonts'/('LiberationSerif-Bold.ttf' if code in ('UAE','QTR') else f'LiberationSans-{style}.ttf')
  ink=additional.INK.get(code,{'AKJ':b,'AXB':'#1B1C24','UAE':a,'SIA':a,'JAL':'#14171C','AFR':a,'THY':b,'OMA':'#967C40','GFA':'#102D45','KAL':a,'SVA':a}.get(code,a))
  head_label={'UPS':'UPS','QTR':'QATAR AIRWAYS','ETD':'ETIHAD AIRWAYS','ANA':'ANA','JAL':'JAPAN AIRLINES','KAL':'KOREAN AIR','THA':'THAI AIRWAYS'}.get(code,label)
  jobs.append('\t'.join(map(str,[font,label,ink,temp/f'{code}.svg'])))
  jobs.append('\t'.join(map(str,[font,head_label,'#EDF2EE',ASSETS/'wordmarks'/f'{code}.svg'])))
 batch=temp/'lettering.tsv';batch.write_text('\n'.join(jobs))
 subprocess.run(['java','-Djava.awt.headless=true','-cp',str(temp),'OutlineLettering','--batch',str(batch)],check=True)
 records=[]
 for code,name,label,a,b,style,reference in CAT:
  target=ASSETS/'liveries'/f'{code}.svg'
  if code=='IGO':
   target.write_text(BASE)
   header=(ROOT/'tools/approved-indigo-wordmark.svg').read_text()
   (ASSETS/'wordmarks'/f'{code}.svg').write_text(header)
  else:
   font=ROOT/'tools/fonts'/('LiberationSerif-Bold.ttf' if code in ('UAE','QTR') else f'LiberationSans-{style}.ttf')
   outlined=temp/f'{code}.svg'
   ink=additional.INK.get(code,{'AKJ':b,'AXB':'#1B1C24','UAE':a,'SIA':a,'JAL':'#14171C','AFR':a,'THY':b,'OMA':'#967C40','GFA':'#102D45','KAL':a,'SVA':a}.get(code,a))
   logo=outlined.read_text()
   if code in ('FDX','FDB'):logo=split_color(logo,b,.57 if code=='FDX' else .30)
   head_label={'UPS':'UPS','QTR':'QATAR AIRWAYS','ETD':'ETIHAD AIRWAYS','ANA':'ANA','JAL':'JAPAN AIRLINES','KAL':'KOREAN AIR','THA':'THAI AIRWAYS'}.get(code,label)
   if code in ('FDX','FDB'):
    header_path=ASSETS/'wordmarks'/f'{code}.svg'
    header_path.write_text(split_color(header_path.read_text(),b,.57 if code=='FDX' else .30))
   svg=BASE
   svg=re.sub(r'<title id="title">.*?</title>',f'<title id="title">{html.escape(name)} standard-livery illustration</title>',svg,flags=re.S)
   svg=re.sub(r'<desc id="desc">.*?</desc>','<desc id="desc">Hand-redrawn standard airline colours, tail motif and enlarged English lettering on a representative airliner. Approximation, not an official brand master, type-specific drawing or confirmation of an individual aircraft paint scheme.</desc>',svg,flags=re.S)
   svg=re.sub(r'<!-- White tail,.*?<!-- Rear horizontal stabilizer\. -->',f'<g clip-path="url(#tailClip)">{tail_mark(code,a,b,logo)}</g><!-- Rear horizontal stabilizer. -->',svg,flags=re.S)
   label_y,label_h=(221,32) if code in additional.PRIMARY else (229,54)
   svg=re.sub(r'<g clip-path="url\(#bodyClip\)">.*?</g>\s*<!-- Window row',f'<g clip-path="url(#bodyClip)">{paint(code,a,b)}{inset(logo,570 if code in ("SIA","BAW","AXB","UPS","JAL","THY") else 660,label_y,440 if code in ("SIA","BAW","AXB","UPS","JAL","THY") else 350,label_h)}</g><!-- Window row',svg,flags=re.S)
   # The preceding replacement removes all IndiGo paint and its vector logo group.
   svg=re.sub(r'<g transform="translate\(277 238\)">.*?</g>','',svg,flags=re.S) if code not in ('AIC','SEJ','AKJ','AXB') else svg
   svg=re.sub(r'<text x="302" y="242".*?</text>','',svg,flags=re.S)
   svg=re.sub(r'<text x="674" y="353".*?</text>','',svg,flags=re.S)
   engine={'AIC':a,'SEJ':a,'AKJ':a,'AXB':a,'BAW':a,'DLH':'#D3DEE8','ETD':'#CAB991','QTR':'#D0D3D8','SIA':'#D0D8DE','THY':'#E6EDF1','UAE':'#E9ECEC','KAL':'#79ABD0','UPS':'#D6DDE1','FDX':a,'AFR':'#EDF0F1','ANA':'#DBE4EA','JAL':'#E9ECEE','SVA':a,'THA':a,'ALK':'#E3E9EB','OMA':a,'FDB':'#E2E8EC','GFA':'#F0EEE6'}.get(code,a)
   svg=recolor_engine(svg,engine)
   if code in additional.BODY:
    body=additional.BODY[code]
    svg=re.sub(r'(<linearGradient id="fuselage".*?</linearGradient>)',lambda m:re.sub(r'stop-color="#[A-Fa-f0-9]+"',f'stop-color="{body}"',m[0]),svg,flags=re.S)
   if code=='ACA':svg=svg.replace('<!-- Cockpit glazing, doors and fine airframe details. -->','<path d="M1056 241Q1109 247 1130 272L1081 276L1048 259Z" fill="#191B1D"/><!-- Cockpit glazing, doors and fine airframe details. -->')
   if code=='KAL':
    svg=svg.replace('stop-color="#FFFFFF"','stop-color="#ABD1EA"',1).replace('stop-color="#F8FBFD"','stop-color="#8FBCE0"',1).replace('stop-color="#E5EBF0"','stop-color="#74A7D0"',1)
   if code=='ETD':svg=svg.replace('stop-color="#FFFFFF"','stop-color="#F6F1E4"',1).replace('stop-color="#F8FBFD"','stop-color="#EFE8D6"',1)
   if code=='QTR':svg=svg.replace('stop-color="#FFFFFF"','stop-color="#ECEDEF"',1).replace('stop-color="#F8FBFD"','stop-color="#E0E2E5"',1)
   if code in ('UPS','FDX'):
    svg=re.sub(r'<g fill="#213C50".*?</g>','',svg,flags=re.S)
    svg=svg.replace('<!-- Cockpit glazing, doors and fine airframe details. -->','<rect x="795" y="230" width="93" height="66" rx="4" fill="none" stroke="#93A5B4" stroke-width="1.2"/><!-- Cockpit glazing, doors and fine airframe details. -->')
   if code=='FDX':
    # Keep the visible Fed/Ex colour split instead of a single-colour word.
    svg=svg.replace(ink,'#4D148C')
   target.write_text(svg)
  ET.fromstring(target.read_text())
  records.append({'icao':code,'name':name,'paint_label':label,'primary':a,'accent':b,'reference':reference,'review_status':'bundled' if code in ORIGINAL_CODES else 'bundled_draft','scheme':('Individually configured airline-colour draft; geometric tail and substitute lettering need visual review against current official fleet imagery.' if code not in ORIGINAL_CODES else 'Standard scheme represented; Air India Express tail art and transitional fleet paint vary.'),'lettering':'Hand-redrawn / outlined English approximation; not official brand master assets.'})
 (ASSETS/'livery-catalogue.json').write_text(json.dumps({'version':2,'date':'2026-10-22','airframe':'Representative shared airliner silhouette; no per-aircraft paint matching.','airlines':records},indent=2)+'\n')
 java='package com.nearsky.tv;\n\n/** Bundled standard-livery catalogue; representative airline schemes, not per-aircraft paint. */\nfinal class AirlineLiveries {\n    private AirlineLiveries(){}\n    static String primary(String code){\n        switch(LiveryDesign.normalized(code)){\n'
 for r in records:java+='            case "'+r['icao']+'": return "'+r['primary']+'";\n'
 java+='            default: return null;\n        }\n    }\n    static boolean contains(String code){return primary(code)!=null;}\n}\n'
 (ROOT/'app/src/main/java/com/nearsky/tv/AirlineLiveries.java').write_text(java)
 names_file=ROOT/'app/src/main/java/com/nearsky/tv/AirlineNames.java'
 names=names_file.read_text()
 assignments='\n'.join('        names.put('+json.dumps(r['icao'])+','+json.dumps(r['name'])+');' for r in sorted(records,key=lambda r:r['icao']))+'\n'
 names=re.sub(r'        names.put\("AIC".*?(?=        KNOWN=)',assignments,names,flags=re.S)
 names_file.write_text(names)
 print(f'Generated {len(records)} liveries and heading wordmarks.')
