"""First expansion: individually configured airline-colour illustration drafts.

These use simplified geometric tail motifs and outlined substitute lettering.
They are review drafts, not official graphics or approved exact reproductions.
No hash-based colour or arbitrary code-to-logo generation is used here.
"""

# ICAO | name | fuselage lettering | primary | accent | official brand reference
DATA = '''
AAL|American Airlines|American|#174A80|#D52B3A|https://www.aa.com/
DAL|Delta Air Lines|DELTA|#09254B|#CF163B|https://news.delta.com/
UAL|United Airlines|UNITED|#005DAA|#59A8DE|https://www.united.com/
SWA|Southwest Airlines|Southwest|#2454A4|#F9C531|https://www.southwest.com/
ASA|Alaska Airlines|Alaska|#123D5B|#91B940|https://news.alaskaair.com/
JBU|JetBlue|jetBlue|#122C54|#28B8D7|https://news.jetblue.com/
FFT|Frontier Airlines|FRONTIER|#08754A|#535A59|https://news.flyfrontier.com/
AAY|Allegiant Air|allegiant|#125BA3|#FDB935|https://www.allegiantair.com/
ACA|Air Canada|AIR CANADA|#181B1C|#E32128|https://www.aircanada.com/
WJA|WestJet|WestJet|#003C56|#00A8A3|https://www.westjet.com/
VIR|Virgin Atlantic|virgin atlantic|#CE143E|#682848|https://www.virginatlantic.com/
EZY|easyJet|easyJet|#F36B21|#FFFFFF|https://www.easyjet.com/
RYR|Ryanair|RYANAIR|#123367|#F5CA27|https://www.ryanair.com/
EIN|Aer Lingus|Aer Lingus|#007F78|#72BD47|https://www.aerlingus.com/
EWG|Eurowings|Eurowings|#89214A|#32ABC7|https://www.eurowings.com/
CFG|Condor|condor|#D8A51D|#FFFFFF|https://www.condor.com/
KLM|KLM|KLM|#008CCB|#FFFFFF|https://news.klm.com/
TRA|Transavia|transavia|#00A650|#FFFFFF|https://www.transavia.com/
IBE|Iberia|IBERIA|#CE182B|#F0B522|https://www.iberia.com/
VLG|Vueling|vueling|#5B5C5E|#FFD431|https://www.vueling.com/
AEA|Air Europa|Air Europa|#173E80|#C71937|https://www.aireuropa.com/
ITY|ITA Airways|ITA AIRWAYS|#0764AF|#E9E4D9|https://www.ita-airways.com/
SWR|SWISS|SWISS|#DC1C2C|#FFFFFF|https://www.swiss.com/
AUA|Austrian Airlines|Austrian|#D71332|#FFFFFF|https://www.austrian.com/
BEL|Brussels Airlines|brussels airlines|#BD1D43|#133F6D|https://www.brusselsairlines.com/
SAS|SAS|SAS|#132A85|#C5C9CA|https://www.sasgroup.net/
FIN|Finnair|FINNAIR|#101E4E|#FFFFFF|https://www.finnair.com/
NAX|Norwegian|norwegian|#DD2733|#152F53|https://www.norwegian.com/
TAP|TAP Air Portugal|TAP AIR PORTUGAL|#D81D2D|#91BD29|https://www.flytap.com/
PGT|Pegasus Airlines|pegasus|#E7B719|#DA2637|https://www.flypgs.com/
AFL|Aeroflot|AEROFLOT|#103C73|#DD243B|https://www.aeroflot.com/
LOT|LOT Polish Airlines|LOT Polish Airlines|#123969|#FFFFFF|https://www.lot.com/
AEE|Aegean Airlines|AEGEAN|#103969|#62C8E5|https://en.about.aegeanair.com/
KAC|Kuwait Airways|KUWAIT|#143F7C|#70B9DC|https://www.kuwaitairways.com/
ELY|EL AL|EL AL|#12346D|#74A0C0|https://www.elal.com/
RJA|Royal Jordanian|ROYAL JORDANIAN|#34363B|#B79559|https://www.rj.com/
MEA|Middle East Airlines|MEA|#006C46|#D32435|https://www.mea.com.lb/
MSR|EgyptAir|EGYPTAIR|#153F7D|#C9AA58|https://www.egyptair.com/
ETH|Ethiopian Airlines|Ethiopian|#BE2130|#F2C323|https://corporate.ethiopianairlines.com/
KQA|Kenya Airways|KENYA AIRWAYS|#CA1D36|#087D4F|https://www.kenya-airways.com/
SAA|South African Airways|SOUTH AFRICAN|#123C72|#E12035|https://www.flysaa.com/
RAM|Royal Air Maroc|ROYAL AIR MAROC|#B71D34|#1E795C|https://www.royalairmaroc.com/
TGW|Scoot|scoot|#F7CC22|#232326|https://www.flyscoot.com/
MAS|Malaysia Airlines|malaysia airlines|#133F75|#CF233B|https://www.malaysiaairlines.com/
AXM|AirAsia|airasia|#E3212C|#FFFFFF|https://newsroom.airasia.com/
HVN|Vietnam Airlines|Vietnam Airlines|#08788A|#D2AC59|https://www.vietnamairlines.com/content/dam/legacy-site-assets/FilesDownload/AboutUs/Corporate-Identity/GSM-2017-Web1.pdf
GIA|Garuda Indonesia|Garuda Indonesia|#0A6979|#60B9BD|https://www.garuda-indonesia.com/
PAL|Philippine Airlines|PHILIPPINE AIRLINES|#0C3679|#D51D36|https://www.philippineairlines.com/
CEB|Cebu Pacific|cebu pacific|#05A4C0|#EFCC29|https://www.cebupacificair.com/
CPA|Cathay Pacific|CATHAY PACIFIC|#00645B|#D5DBD8|https://www.cathaypacific.com/cx/en_MY/inspiration/cathay-stories/how-cathay-pacific-refreshed-its-aircraft-livery.html
CCA|Air China|AIR CHINA|#153B72|#CC2037|https://www.airchina.com.cn/
CES|China Eastern Airlines|CHINA EASTERN|#193A76|#CE1E3E|https://www.ceair.com/
CSN|China Southern Airlines|CHINA SOUTHERN|#16659B|#D52B45|https://www.csair.com/
CHH|Hainan Airlines|HAINAN AIRLINES|#BD2035|#EAB43B|https://www.hainanairlines.com/
AAR|Asiana Airlines|ASIANA AIRLINES|#55565A|#D52037|https://flyasiana.com/
QFA|Qantas|QANTAS|#D62431|#FFFFFF|https://www.qantasnewsroom.com.au/
VOZ|Virgin Australia|Virgin Australia|#C91D3A|#FFFFFF|https://newsroom.virginaustralia.com/
JST|Jetstar|Jetstar|#30343B|#F26522|https://www.jetstar.com/
ANZ|Air New Zealand|AIR NEW ZEALAND|#171C1E|#FFFFFF|https://www.airnewzealand.co.nz/
FJI|Fiji Airways|FIJI AIRWAYS|#483322|#E2D3AC|https://www.fijiairways.com/
LAN|LATAM Airlines|LATAM|#192E63|#D7284B|https://www.latamairlines.com/
AVA|Avianca|avianca|#D4202D|#FFFFFF|https://www.avianca.com/
CMP|Copa Airlines|Copa Airlines|#153E7B|#C7A968|https://www.copaair.com/
AMX|Aeromexico|AEROMEXICO|#102F55|#D6213A|https://www.aeromexico.com/
VOI|Volaris|volaris|#202127|#B32C98|https://www.volaris.com/
GLO|GOL Linhas Aereas|GOL|#EF6E25|#FFFFFF|https://www.voegol.com.br/
AZU|Azul Brazilian Airlines|azul|#17376C|#23A9D6|https://www.voeazul.com.br/
'''

ROWS=[line.split('|') for line in DATA.strip().splitlines()]
CAT=[(code,name,label,a,b,'BoldItalic' if code in ('ASA','VIR','VOZ','EIN','AVA') else 'Bold',url) for code,name,label,a,b,url in ROWS]
PRIMARY={r[0]:r[3] for r in ROWS}
BODY={'AAL':'#CDD1D3','KLM':'#65BFE5','ITY':'#2787C5','HVN':'#168A9A','AXM':'#E3212C',
      'JST':'#D8DDDF','SWA':'#2454A4','RJA':'#55565A','AFL':'#D1D9DF'}
INK={code:('#FFFFFF' if code in ('SWA','AXM','HVN','RJA','ITY') else a) for code,_,_,a,_,_ in ROWS}
INK.update({'KLM':'#06447D','CFG':'#23272D','TGW':'#252629','PGT':'#D52032'})

def tail(code,a,b,p,c,l,t):
    """Explicit per-airline motifs. Coordinates refer to the shared clipped fin."""
    base=p('M164 251L83 49Q77 37 91 37H122L314 249Z',a)
    white='#FFFFFF'
    if code=='AAL': return base+''.join(p(f'M{82+i*13} {68+i*24}H{145+i*23}L{166+i*23} {88+i*24}H{90+i*13}Z',white if i%2 else b) for i in range(7))
    if code=='DAL':return base+p('M123 200L179 94L233 200L179 172Z',b)+p('M123 204L179 178L233 204Z','#A6102B')
    if code in ('UAL','CMP'):
        return base+c(174,166,49,b)+''.join(l(f'M{130+i*18} 124Q{151+i*11} 166 {130+i*18} 208',a,3) for i in range(5))+''.join(l(f'M128 {138+i*18}Q174 {151+i*12} 218 {138+i*18}',a,3) for i in range(4))
    if code=='SWA':return base+p('M81 83L136 54L305 249H235Z','#D72739')+p('M80 144L117 115L253 249H206Z',b)
    if code=='ASA':return base+p('M126 204Q115 151 151 117Q183 93 212 138L205 195L174 218Z','#E4E9E7')+p('M160 139Q193 120 209 144L192 151L198 164L184 170L193 182L170 196L150 184L143 163Z',a)+p('M78 231Q153 204 291 244L313 253H89Z',b)
    if code=='JBU':return base+''.join(p(f'M{95+i*22} 50L{133+i*22} 50L{300+i*22} 252H{262+i*22}Z',b if i%2 else '#39618D') for i in range(6))
    if code=='FFT':return base+p('M124 204L132 141L153 123L159 89L175 121L193 94L201 141L218 160L213 206L176 221Z','#E5D9C7')+p('M144 166L163 161L155 172Z',a)+p('M181 161L199 166L188 173Z',a)+p('M164 189L177 184L185 192L174 201Z','#313C38')
    if code=='AAY':return base+c(176,169,33,b)+''.join(l(f'M176 169L{176+int(61*__import__("math").cos(i*__import__("math").pi/6))} {169+int(61*__import__("math").sin(i*__import__("math").pi/6))}',b,6) for i in range(12))
    if code=='ACA':return base+c(174,165,45,b)+p('M174 127L182 147L194 139L190 156L205 159L185 175L188 187L177 185V203H171V185L160 187L163 175L144 159L158 156L155 139L166 147Z',a)
    if code=='WJA':return base+p('M149 151L172 119L195 151L182 174L173 208L164 174Z',b)+p('M155 147L172 169L189 147L181 182L172 205L163 182Z',white)
    if code in ('VIR','VOZ'):return base+t('Virgin',118,169,30,white)
    if code=='EZY':return base+t('easyJet',118,175,29,white)
    if code=='RYR':return base+p('M142 132Q173 107 205 131L196 143L166 139L175 197L159 201L148 139Z',b)+''.join(l(f'M{159+i*7} 139L{167+i*5} 189',b,3) for i in range(5))
    if code=='EIN':return base+c(158,147,20,white)+c(189,147,20,white)+c(174,124,20,white)+p('M170 147H180L182 204H166Z',white)
    if code=='EWG':return base+p('M90 67H126L301 247H263Z',b)+p('M102 125H140L252 247H216Z',white)
    if code=='CFG':return base+p('M110 176L208 126L194 163L218 169L176 184Z',white)
    if code=='KLM':return base+''.join(c(x,138,7,white) for x in (148,174,200))+p('M144 150L151 169H197L204 150Z',white)+t('KLM',136,200,30,white)
    if code=='TRA':return base+c(174,165,48,white)+t('t',157,191,73,a)
    if code=='IBE':return base+p('M82 118L127 86L272 248H225Z',b)
    if code=='VLG':return p('M164 251L83 49H122L314 249Z','#E4E5E3')+''.join(c(125+j*24+i*9,109+i*23,6,a) for i in range(5) for j in range(3))
    if code=='AEA':return base+l('M132 178L158 139L176 176L194 135L216 181',white,8)
    if code=='ITY':return base+p('M81 58L116 38L302 250H279Z','#078750')+p('M95 82L112 66L281 250H260Z',white)+p('M107 110L121 95L262 250H244Z','#D72A3E')
    if code=='SWR':return base+p('M157 125H186V149H210V178H186V202H157V178H133V149H157Z',white)
    if code=='AUA':return base+p('M133 180L217 121L183 178L201 181L151 201L164 177Z',white)
    if code=='BEL':return p('M164 251L83 49H122L314 249Z',white)+''.join(c(x,y,7,a) for x,y in [(150,119),(173,119),(196,119),(139,143),(162,143),(185,143),(208,143),(150,167),(173,167),(196,167),(162,191),(185,191),(174,215)])
    if code=='SAS':return base+t('SAS',123,179,44,white)
    if code=='FIN':return base+p('M140 121H207L197 137H166V154H195L186 169H166V202H140Z',white)
    if code=='NAX':return p('M164 251L83 49H122L314 249Z',white)+c(174,150,30,'#B9BEC0')+p('M128 214Q130 177 174 175Q220 176 223 214Z','#929A9E')
    if code=='TAP':return base+p('M82 40H123L310 250H244Z',b)+t('TAP',126,181,39,white)
    if code=='PGT':return base+t('pegasus',116,175,26,b)
    if code=='AFL':return base+p('M83 67L126 45L306 249H257Z',white)+p('M92 99L119 77L280 249H247Z',b)
    if code=='LOT':return base+c(174,165,46,white)+p('M139 178L203 139L185 164L208 172L176 177L158 198L162 177Z',a)
    if code=='AEE':return base+p('M128 173L176 137L211 145L180 154L160 176Z',white)+p('M154 193L195 159L232 167L202 176L182 197Z',b)
    if code=='KAC':return base+p('M125 166Q167 115 219 141L188 155L169 179L144 197L149 174Z',white)
    if code=='ELY':return p('M164 251L83 49H122L314 249Z',white)+l('M174 112L215 182H133ZM174 205L133 135H215Z',a,5)+p('M104 213L284 237L304 250H125Z',a)
    if code=='RJA':return base+p('M139 151L149 119L173 141L198 117L209 151L198 176H151Z',b)+l('M151 188H198',b,5)
    if code=='MEA':return p('M164 251L83 49H122L314 249Z',white)+p('M175 100L157 127H165L145 151H158L134 177H165L160 202H190L185 177H217L192 151H204L184 127H193Z',a)+p('M83 68L118 49L300 250H269Z',b)
    if code=='MSR':return base+p('M133 191L139 142L167 122L203 131L217 150L184 145L178 160L188 182L164 207L164 162L154 153Z',b)+c(180,139,3,a)
    if code=='ETH':return base+p('M85 49H122L310 251H270Z','#148B49')+p('M85 99L114 78L282 251H245Z',b)
    if code=='KQA':return base+l('M137 186L151 138L173 162L198 133L214 183',white,9)+p('M83 210L263 231L301 251H128Z',b)
    if code=='SAA':return base+p('M82 59L122 40L313 251H259Z',white)+p('M82 111L113 86L276 251H227Z','#159657')+p('M91 161L114 144L225 251H181Z',b)+p('M96 199L123 182L181 251H143Z','#F2C629')
    if code=='RAM':return base+l('M174 113L187 149H224L194 171L205 207L174 185L143 207L155 171L125 149H162Z',b,5)
    if code=='TGW':return base+t('scoot',119,177,36,'#222326')
    if code=='MAS':return p('M164 251L83 49H122L314 249Z',white)+p('M136 138L174 112L211 139L182 154L218 185L175 209L132 185L166 154Z',a)+p('M174 112L211 139L182 154L218 185L175 209V179L192 174L175 160Z',b)
    if code=='AXM':return base+t('airasia',117,178,35,white)
    if code=='HVN':return base+''.join(p(d,b) for d in ['M174 112Q148 142 174 175Q201 142 174 112Z','M171 179Q125 164 132 134Q162 140 171 179Z','M178 179Q221 162 214 133Q185 140 178 179Z','M166 185Q126 188 125 162Q152 160 166 185Z','M183 185Q220 188 224 161Q198 160 183 185Z'])
    if code=='GIA':return base+''.join(p(f'M{117+i*8} {146+i*13}L{201+i*10} {104+i*13}L{184+i*9} {139+i*13}L{133+i*6} {165+i*13}Z',b if i%2 else '#BCE1DE') for i in range(5))
    if code=='PAL':return base+p('M81 49L123 41L308 249H161Z',b)+''.join(l(f'M179 171L{137+i*12} {109+abs(3-i)*8}', '#F4CD32',5) for i in range(7))+c(179,171,23,'#F4CD32')
    if code=='CEB':return p('M164 251L83 49H122L314 249Z',b)+p('M119 177Q154 114 209 137L180 151L210 173L175 175L146 207L153 177Z',a)
    if code=='CPA':return base+p('M124 182Q173 121 218 143Q178 150 148 179L210 158L164 186Z',white)
    if code=='CCA':return p('M164 251L83 49H122L314 249Z',white)+l('M137 191Q116 133 161 128Q201 122 209 162L185 195M149 179Q137 148 162 144Q185 143 193 163M159 173L174 157L181 176L170 196',b,6)
    if code=='CES':return p('M164 251L83 49H122L314 249Z',white)+p('M128 175L205 124L182 157L220 170L178 175L153 201L158 178Z',a)+p('M150 150L184 120L169 152L190 158L167 168L144 193L152 165Z',b)
    if code=='CSN':return p('M164 251L83 49H122L314 249Z','#63BDE4')+''.join(c(x,y,20,b) for x,y in [(174,134),(150,153),(160,181),(190,181),(199,153)])+c(174,160,12,'#F3CB55')
    if code=='CHH':return base+''.join(l(f'M{133+i*12} 185Q{126+i*12} 142 {164+i*9} 121Q{189+i*8} 133 {182+i*9} 157',b,6) for i in range(4))
    if code=='AAR':return p('M164 251L83 49H122L314 249Z','#C5C6C2')+p('M83 49H123L313 251H241Z',b)+p('M91 119L121 96L265 251H222Z','#F1C42E')+p('M111 172L130 153L223 251H193Z','#335C8F')
    if code=='QFA':return base+p('M135 191L163 170L178 137L193 134L190 147L177 159L180 179L200 187L229 187L217 200L184 198L171 185L153 200L130 204L109 194L134 197Z',white)
    if code=='JST':return base+p('M174 117L186 151L222 151L193 173L204 207L174 186L144 207L155 173L126 151H162Z',b)
    if code=='ANZ':return base+l('M211 135Q170 102 143 140Q119 178 158 198Q197 216 211 182Q220 157 194 151Q170 145 164 165Q160 181 178 181',white,9)
    if code=='FJI':return base+l('M136 134H207V199H136ZM145 144H198V189H145ZM157 155H187V178H157Z',b,4)+l('M137 166H207M173 134V199',b,4)
    if code=='LAN':return base+''.join(p(f'M{120+i*15} {137+i*14}L{169+i*14} {109+i*14}L{211+i*9} {137+i*14}L{164+i*10} {164+i*14}Z',b if i%2 else '#E64173') for i in range(4))
    if code=='AVA':return base+p('M127 156Q159 116 213 132L182 151L217 153L184 168L161 194L146 190L160 165Z',white)
    if code=='AMX':return base+p('M139 203V149L161 124L197 128L216 150L195 148L194 167L180 173L181 194L162 205V164L151 159V204Z',white)+''.join(l(f'M{146+i*12} 141L{139+i*12} 115',white,5) for i in range(4))
    if code=='VOI':return base+''.join(p(f'M{x} {y}h14v14h-14Z',b if (i+j)%2 else white) for i,y in enumerate((133,153,173,193)) for j,x in enumerate((141,161,181,201)))
    if code=='GLO':return base+t('GOL',127,182,42,white)
    if code=='AZU':return base+''.join(p(f'M{x} {y}h13v13h-13Z',col) for x,y,col in [(162,122,'#17AAD4'),(177,122,'#E9337C'),(147,137,'#EABA24'),(162,137,'#43B690'),(177,137,'#7475B7'),(192,137,'#E97A30'),(162,152,'#19A7C3'),(177,152,'#F3C32E'),(192,152,'#E83E50'),(177,167,'#7199CD'),(177,182,'#33A8B1'),(162,197,'#BBCA3B')])
    raise ValueError('Missing explicit tail design: '+code)

def paint(code,a,b,p,l):
    if code=='CFG':return ''.join(p(f'M{x} 205H{x+35}V330H{x}Z',a) for x in range(80,1140,70))
    if code=='EZY':return p('M75 231Q310 238 470 320H75Z',a)
    if code=='JBU':return p('M75 277H1140V330H75Z',a)
    if code=='RYR':return p('M75 279H1140V330H75Z',a)+l('M75 275H1140',b,5)
    if code=='AAY':return p('M75 275H1140V330H75Z',a)+l('M75 272H1140',b,5)
    if code=='VLG':return p('M75 284H1140V330H75Z','#DDDFDD')+l('M75 280H1140',b,6)
    if code=='CPA':return p('M75 282H1140V330H75Z','#C5CDCA')
    if code in ('ACA','WJA','SAS','AFL'):return p('M75 285H1140V330H75Z',a)+ (l('M75 281H1140',b,4) if code=='AFL' else '')
    if code=='EIN':return p('M75 284H1140V330H75Z',a)
    if code in ('ELY','CCA','LOT','CMP'):
        return l('M75 278H1140',a,5)+l('M75 288H1140',b if code!='CCA' else a,3)
    if code=='TAP':return p('M75 218Q317 235 421 320H75Z',b)+p('M75 240Q249 263 352 320H75Z',a)
    if code=='NAX':return p('M880 210H1140V330H880Z',a)
    if code=='KQA':return l('M75 283H1140',b,4)+l('M75 290H1140',a,4)
    if code=='GIA':return p('M75 243Q254 251 440 320H75Z',a)+l('M75 250Q272 269 420 316',b,8)
    if code=='KAC':return p('M75 235Q212 254 378 320H75Z',a)+l('M75 240Q230 265 406 320',b,7)
    if code=='CEB':return p('M75 244Q233 243 433 320H75Z',a)+l('M75 239Q246 254 451 320',b,10)
    if code=='AEE':return p('M75 245Q237 260 399 320H75Z',a)+l('M75 247Q250 269 407 320',b,4)
    if code=='TGW':return p('M75 281Q346 286 1140 290V330H75Z',a)
    if code=='AUA':return p('M75 286H1140V330H75Z',a)
    if code=='IBE':return p('M75 242Q220 253 376 320H75Z',a)+l('M75 250Q210 260 360 320',b,6)
    if code=='SWA':return p('M75 288H1140V330H75Z','#D72739')+l('M75 284H1140',b,4)
    if code=='LAN':return p('M75 230Q266 247 449 320H75Z',a)+l('M75 236Q244 254 414 320',b,8)
    if code=='AVA':return p('M75 239Q206 253 358 320H75Z',a)
    return ''
