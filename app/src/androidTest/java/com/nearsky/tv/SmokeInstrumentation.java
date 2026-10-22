package com.nearsky.tv;

import android.app.Instrumentation;
import android.app.Activity;
import android.os.Bundle;
import java.util.*;
import org.json.*;

/** Dependency-free instrumentation covering data correctness, not a mirror of the UI. */
public final class SmokeInstrumentation extends Instrumentation {
    private int assertions;
    @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }
    private void check(boolean pass, String message) { assertions++; if (!pass) throw new AssertionError(message); }
    private Flight flight(String id, double lat, double lon, double age, boolean ground) {
        return new Flight(id, "", "", "", "", lat, lon, 10000, 200, 0, age, ground);
    }
    @Override public void onStart() {
        Bundle result = new Bundle();
        try {
            check(Math.abs(Geo.distanceKm(0,0,0,1) - 111.195) < .01, "Great-circle distance in km");
            check(Math.abs(Geo.distanceKm(0,179.9,0,-179.9) - 22.239) < .01, "Date line wrapping");
            check(Geo.compass(359).equals("N"), "Compass wrap");
            check(Geo.compass(225).equals("SW"), "Compass southwest");
            check(!Geo.valid(Double.NaN,0), "Reject invalid coordinates");
            List<Flight> input = Arrays.asList(flight("000003",0,.3,0,false), flight("000002",0,.2,0,false), flight("000001",0,.1,0,false),
                    flight("000004",0,.4,0,false), flight("000005",0,.01,61,false), flight("000006",0,.02,0,true),
                    flight("000007",0,1,0,false), flight("000008",Double.NaN,0,0,false), flight("000001",0,.1,0,false), flight("~000009",0,0,0,false));
            List<Flight> selected = Geo.nearest(input,0,0,50,0);
            check(selected.size()==3, "At most three unique airborne, fresh aircraft");
            check(selected.get(0).hex.equals("000001") && selected.get(2).hex.equals("000003"), "Nearest first, excludes out of radius");
            check(Geo.nearest(input,0,0,50,61).isEmpty(), "Previously loaded positions expire without a refresh");
            check(Geo.nearest(Collections.emptyList(),0,0,50,0).isEmpty(), "Empty coverage stays empty");
            long now = System.currentTimeMillis();
            JSONObject root = new JSONObject(); root.put("now", now/1000.0); root.put("aircraft", new JSONArray("[{\"hex\":\"800001\",\"flight\":\" TEST1  \",\"lat\":28.6,\"lon\":77.4,\"seen_pos\":2,\"alt_baro\":\"ground\"},null,{\"hex\":\"800002\",\"lat\":28.6,\"lon\":77.4,\"seen_pos\":1,\"alt_geom\":1500}]"));
            List<Flight> parsed = FlightParser.parse(root,now);
            check(parsed.size()==2 && parsed.get(0).grounded, "Ground marker and malformed entry");
            check(parsed.get(0).callsign.equals("TEST1"), "Trim padded callsign");
            check(parsed.get(1).altitudeFt==1500, "Geometric altitude fallback");
            root.put("now",now); root.put("ac",root.remove("aircraft"));
            check(FlightParser.parse(root,now).size()==2, "Milliseconds and alternate ac schema");
            root.put("now",(now-120000)/1000.0);
            boolean staleRejected=false; try { FlightParser.parse(root,now); } catch(JSONException e) { staleRejected=true; }
            check(staleRejected,"Stale provider response rejected");
            boolean missingRejected=false; try { FlightParser.parse(new JSONObject().put("now",now),now); } catch(JSONException e) { missingRejected=true; }
            check(missingRejected,"Malformed schema is an error, not zero aircraft");
            boolean invalidRejected=false; try { new Settings("x",0,0,Double.NaN,true); } catch(IllegalArgumentException e) { invalidRejected=true; }
            check(invalidRejected,"NaN radius rejected");
            Settings s = Settings.load(getTargetContext());
            check(Geo.valid(s.lat,s.lon) && s.radiusKm>=1,"Persisted settings valid");
            check(LiveryDesign.fromCallsign("igo123A ").equals("IGO"),"ICAO callsign resolves to its operator design");
            check(LiveryDesign.fromCallsign("N123AB").isEmpty(),"US registration is not an airline code");
            check(LiveryDesign.fromCallsign("VT-ANB").isEmpty(),"Indian registration is not an airline code");
            check(LiveryDesign.normalized("<script>").isEmpty(),"Invalid identifiers cannot enter SVG markup");
            check(LiveryDesign.svg("IGO").equals(LiveryDesign.svg("igo")),"Design is stable and case independent");
            check(!LiveryDesign.svg("IGO").equals(LiveryDesign.svg("AIC")),"Airline codes receive distinct designs");
            for(String code:new String[]{"AAA","AAB","AAC","AAD","AAE","AAF","IGO","AIC","SEJ","ZZZ",""}) {
                android.graphics.Picture picture=com.caverock.androidsvg.SVG.getFromString(LiveryDesign.svg(code)).renderToPicture(1200,440);
                check(picture.getWidth()==1200 && picture.getHeight()==440,"SVG parses and renders: "+code);
            }
            result.putString("stream", "PASS: " + assertions + " data and settings assertions\n");
            finish(Activity.RESULT_OK,result);
        } catch(Throwable error) {
            result.putString("stream","FAIL after " + assertions + " assertions: " + error + "\n"); finish(Activity.RESULT_CANCELED,result);
        }
    }
}
