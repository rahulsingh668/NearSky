package com.nearsky.tv;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.*;

/** Three full-width landscape cards, scaled from a 1920 x 1080 composition. */
public final class FlightView extends View {
    private static final int BG=Color.rgb(8,13,18), INK=Color.rgb(237,242,238), MUTED=Color.rgb(151,167,177), MINT=Color.rgb(196,229,201);
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Typeface regular=Typeface.create("sans-serif",Typeface.NORMAL), bold=Typeface.create("sans-serif-medium",Typeface.NORMAL), mono=Typeface.create("monospace",Typeface.NORMAL);
    private final LiveryCache liveries;
    private Settings settings;
    private FlightRepository repository;
    private PhotoRepository photos;
    private AirlineNameRepository airlineNames;
    private RouteRepository routes;
    private List<Flight> all=Collections.emptyList();
    private long received;
    private boolean running,demo;
    private String error;
    private final Runnable tick=new Runnable(){@Override public void run(){if(running){invalidate();postDelayed(this,1000);}}};
    public FlightView(Context c){super(c);liveries=new LiveryCache(c.getAssets());setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);}
    public void start(boolean demo){
        stop();this.demo=demo;running=true;settings=Settings.load(getContext());
        photos=new PhotoRepository();routes=new RouteRepository();airlineNames=new AirlineNameRepository(getContext());all=Collections.emptyList();received=0;error=null;
        if(demo){
            all=Arrays.asList(
                new Flight("8005ec","AIC101","VT-ANB","B788","Boeing 787-8",settings.lat-.045,settings.lon+.025,12500,280,48,0,false),
                new Flight("80163c","IGO202","VT-IQL","A20N","Airbus A320neo",settings.lat-.11,settings.lon-.08,24600,410,126,0,false),
                new Flight("800b80","SEJ303","VT-SLA","B737","Boeing 737-700",settings.lat+.16,settings.lon+.12,32000,455,274,0,false));
            received=SystemClock.elapsedRealtime();
        }else{
            repository=new FlightRepository();repository.start(settings,(flights,at,failure)->{if(flights!=null){all=flights;received=at;}error=failure;invalidate();});
        }
        tick.run();
    }
    public void stop(){if(routes!=null){routes.stop();routes=null;}running=false;removeCallbacks(tick);if(repository!=null){repository.stop();repository=null;}if(photos!=null){photos.stop();photos=null;}if(airlineNames!=null){airlineNames.stop();airlineNames=null;}}
    @Override protected void onDetachedFromWindow(){stop();super.onDetachedFromWindow();}
    public List<String> photoSources(){
        List<String> urls=new ArrayList<>();
        if(photos!=null&&settings.photos)for(Flight f:nearest()){
            PhotoRepository.Photo photo=photos.get(f.hex,true,this::invalidate);
            if(photo!=null&&!photo.sourceUrl.isEmpty())urls.add(f.registration+": "+photo.sourceUrl);
        }
        return urls;
    }
    private List<Flight> nearest(){return settings==null?Collections.emptyList():Geo.nearest(all,settings.lat,settings.lon,settings.radiusKm,demo?0:Math.max(0,(SystemClock.elapsedRealtime()-received)/1000.0));}
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);c.drawColor(BG);if(settings==null)return;
        float s=Math.min(getWidth()/1920f,getHeight()/1080f);
        c.save();c.translate((getWidth()-1920*s)/2,(getHeight()-1080*s)/2);c.scale(s,s);
        float drift=(float)Math.sin(SystemClock.elapsedRealtime()/90000.0)*2;c.translate(drift,0);
        p.setShader(new RadialGradient(1000,200,1300,new int[]{Color.rgb(22,39,47),BG},null,Shader.TileMode.CLAMP));c.drawRect(0,0,1920,1080,p);p.setShader(null);
        p.setColor(Color.rgb(27,43,51));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);
        for(int i=1;i<5;i++)c.drawCircle(1450,320,i*280,p);p.setStyle(Paint.Style.FILL);
        text(c,"N E A R S K Y",64,60,22,MINT,bold);
        fit(c,settings.name,64,140,64,1350,INK,bold);
        text(c,String.format(Locale.US,"YOUR LOCAL SKY  /  %.0f KM RADIUS",settings.radiusKm),67,184,21,MUTED,regular);
        textRight(c,new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),1856,100,54,INK,mono);
        textRight(c,new SimpleDateFormat("EEE, d MMM",Locale.getDefault()).format(new Date()),1856,140,20,MUTED,regular);
        boolean expired=received!=0&&SystemClock.elapsedRealtime()-received>60000;
        String status=demo?"DEMO · SAMPLE FLIGHTS":error!=null||expired?"FEED DELAYED":received==0?"CONNECTING":"LIVE";
        int stateColor=demo||error!=null||expired?Color.rgb(236,192,124):MINT;
        List<Flight> visible=nearest();textRight(c,status+String.format(Locale.US,"  /  %02d IN VIEW",visible.size()),1856,184,19,stateColor,bold);
        StringBuilder accessibility=new StringBuilder(settings.name+". "+status+". ");
        for(int i=0;i<3;i++){
            float left=64+i*608;
            if(i<visible.size()){
                Flight f=visible.get(i);drawFlight(c,f,i,left);
                accessibility.append(f.title()).append(", ").append(number(Geo.distanceKm(settings.lat,settings.lon,f.lat,f.lon),"kilometers away")).append(", altitude ").append(number(f.altitudeFt,"feet")).append(", ground speed ").append(number(f.speedKnots*1.852,"kilometers per hour")).append(". ");
                if(!demo&&routes!=null){RouteRepository.Result r=routes.get(f.callsign,this::invalidate);if(r.route!=null)accessibility.append("From ").append(r.route.origin.city).append(" ").append(r.route.origin.code).append(" to ").append(r.route.destination.city).append(" ").append(r.route.destination.code).append(". ");}
            }else drawEmpty(c,i,left,received==0&&error==null?"Listening to the sky":error!=null||expired?"Waiting for fresh positions":"No more aircraft in range");
        }
        text(c,"POSITION DATA  adsb.fi  ·  15 SEC REFRESH",64,1008,18,MUTED,regular);
        textRight(c,"Developed by Rahul Singh",1856,1008,17,MUTED,regular);
        text(c,demo?"Sample positions · return to Live from the menu":error!=null?"Connection interrupted · fresh positions only":"Distance from your location · not a navigation tool",64,1040,16,MUTED,regular);
        setContentDescription(accessibility.toString());c.restore();
    }
    private void drawFlight(Canvas c,Flight f,int rank,float x){
        float top=224,bottom=944,w=576;
        String code=LiveryDesign.fromCallsign(f.callsign);
        PhotoRepository.Photo photo=photos!=null?photos.get(f.hex,settings.photos,this::invalidate):null;
        if(code.isEmpty()&&photo!=null)code=photo.operator;
        String airlineName=airlineNames!=null?airlineNames.get(code,this::invalidate):AirlineNames.known(code);
        String brandColor=AirlineLiveries.primary(code);
        int accent=Color.parseColor(brandColor==null?LiveryDesign.primary(code):brandColor);
        round(c,x,top,x+w,bottom,24,Color.rgb(21,32,41));
        c.save();Path clip=new Path();clip.addRoundRect(new RectF(x,top,x+w,bottom),24,24,Path.Direction.CW);c.clipPath(clip);
        p.setShader(new LinearGradient(x,top,x+w,490,Color.rgb(31,51,63),Color.rgb(17,31,42),Shader.TileMode.CLAMP));c.drawRect(x,top,x+w,490,p);p.setShader(null);
        boolean isPhoto=settings.photos&&photo!=null&&photo.bitmap!=null;
        if(isPhoto){
            Bitmap b=photo.bitmap;float scale=Math.max(w/b.getWidth(),266f/b.getHeight());float bw=b.getWidth()*scale,bh=b.getHeight()*scale;
            c.save();c.clipRect(x,top,x+w,490);c.drawBitmap(b,null,new RectF(x+(w-bw)/2,top+(266-bh)/2,x+(w+bw)/2,top+(266+bh)/2),p);c.restore();
            p.setShader(new LinearGradient(0,410,0,490,Color.TRANSPARENT,Color.argb(220,11,22,30),Shader.TileMode.CLAMP));c.drawRect(x,410,x+w,490,p);p.setShader(null);
        }else{
            text(c,code.isEmpty()?"NS":code,x+28,422,148,Color.argb(16,255,255,255),bold);
            liveries.draw(c,code,airlineName,new RectF(x+10,267,x+w-10,461));
        }
        round(c,x+24,246,x+175,284,9,Color.argb(225,8,19,26));
        text(c,rank==0?"01  NEAREST":String.format(Locale.US,"%02d  NEARBY",rank+1),x+38,272,17,MINT,bold);
        textRight(c,code.isEmpty()?"UNIDENTIFIED OPERATOR":code+(isPhoto?"  /  REFERENCE PHOTO":AirlineLiveries.contains(code)?"  /  STANDARD LIVERY":"  /  CONCEPT LIVERY"),x+w-24,475,14,Color.rgb(204,218,225),regular);
        if(isPhoto)textRight(c,"Photo: Airport-data.com via adsbdb",x+w-24,456,12,INK,regular);
        c.restore();
        p.setColor(accent);c.drawRect(x+24,511,x+28,549,p);
        if(!liveries.drawBrand(c,code,new RectF(x+43,500,x+w-26,556)))
            fit(c,airlineName.isEmpty()?f.title():airlineName,x+43,548,44,w-70,INK,bold);
        String model=photo!=null&&!photo.aircraft.isEmpty()?photo.aircraft:f.description.isEmpty()?f.type.isEmpty()?"Aircraft type unavailable":f.type:f.description;
        fit(c,(airlineName.isEmpty()?"":f.title()+"  ·  ")+model,x+26,590,21,w-52,MUTED,regular);
        RouteRepository.Result route=demo||routes==null?null:routes.get(f.callsign,this::invalidate);
        drawRoute(c,x,route);
        double distance=Geo.distanceKm(settings.lat,settings.lon,f.lat,f.lon);
        text(c,String.format(Locale.US,"%.1f",distance),x+24,735,54,MINT,mono);
        p.setTextSize(54);p.setTypeface(mono);float numberWidth=p.measureText(String.format(Locale.US,"%.1f",distance));
        text(c,"km",x+36+numberWidth,733,25,MUTED,regular);
        textRight(c,Geo.compass(Geo.bearing(settings.lat,settings.lon,f.lat,f.lon))+" OF YOU",x+w-26,729,19,MUTED,bold);
        p.setColor(Color.rgb(43,58,68));c.drawRect(x+26,762,x+w-26,763,p);
        metric(c,x+26,810,number(f.altitudeFt,"ft"),"ALTITUDE");
        metric(c,x+306,810,number(f.speedKnots*1.852,"km/h"),"GROUND SPEED");
        metric(c,x+26,891,f.registration.isEmpty()?"—":f.registration,"REGISTRATION");
        metric(c,x+306,891,Double.isFinite(f.heading)?String.format(Locale.US,"%03.0f° %s",f.heading,Geo.compass(f.heading)):"—","HEADING");
    }
    private void drawRoute(Canvas c,float x,RouteRepository.Result result){
        text(c,"ORIGIN",x+26,619,13,MUTED,bold);
        text(c,"DESTINATION",x+306,619,13,MUTED,bold);
        if(result!=null&&result.route!=null){
            FlightRoute route=result.route;
            fit(c,route.origin.code,x+26,655,32,218,INK,mono);
            centered(c,"→",x+277,653,30,MINT,regular);
            fit(c,route.destination.code,x+306,655,32,244,INK,mono);
            fit(c,route.origin.city,x+26,682,18,228,MUTED,regular);
            fit(c,route.destination.city,x+306,682,18,244,MUTED,regular);
        }else{
            fit(c,demo?"Routes appear for live flights":result!=null&&result.loading?"Looking up route…":"Route unavailable",x+26,647,23,524,MUTED,regular);
        }
    }
    private void drawEmpty(Canvas c,int rank,float x,String message){
        round(c,x,224,x+576,944,24,Color.rgb(17,27,36));
        text(c,String.format(Locale.US,"%02d",rank+1),x+28,274,19,MUTED,mono);
        p.setColor(Color.rgb(41,59,70));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);
        c.drawCircle(x+288,460,95,p);c.drawCircle(x+288,460,130,p);c.drawLine(x+130,460,x+446,460,p);c.drawLine(x+288,303,x+288,617,p);p.setStyle(Paint.Style.FILL);
        centered(c,"+",x+288,471,32,MINT,mono);
        fit(c,message,x+28,698,28,520,INK,regular);
        text(c,"Fresh airborne positions appear here",x+28,742,20,MUTED,regular);
        text(c,"Your selected radius stays in effect",x+28,776,18,MUTED,regular);
    }
    private static String number(double n,String unit){return Double.isFinite(n)?String.format(Locale.US,"%,.0f %s",n,unit):"—";}
    private void metric(Canvas c,float x,float y,String value,String label){fit(c,value,x,y,29,244,INK,mono);text(c,label,x,y+30,14,MUTED,regular);}
    private void round(Canvas c,float l,float t,float r,float b,float radius,int color){p.setColor(color);c.drawRoundRect(l,t,r,b,radius,radius,p);}
    private void text(Canvas c,String s,float x,float y,float size,int color,Typeface type){p.setColor(color);p.setTextSize(size);p.setTypeface(type);c.drawText(s,x,y,p);}
    private void textRight(Canvas c,String s,float x,float y,float size,int color,Typeface type){p.setTextSize(size);p.setTypeface(type);text(c,s,x-p.measureText(s),y,size,color,type);}
    private void centered(Canvas c,String s,float x,float y,float size,int color,Typeface type){p.setTextSize(size);p.setTypeface(type);text(c,s,x-p.measureText(s)/2,y,size,color,type);}
    private void fit(Canvas c,String s,float x,float y,float size,float width,int color,Typeface type){p.setTextSize(size);p.setTypeface(type);if(p.measureText(s)>width){while(p.measureText(s+"…")>width&&s.length()>1)s=s.substring(0,s.length()-1);s+="…";}text(c,s,x,y,size,color,type);}
}
