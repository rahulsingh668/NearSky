package com.nearsky.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** Resolve airline names by exact ICAO code, never from an aircraft's registered owner. */
final class AirlineNameRepository {
    private final SharedPreferences prefs;
    private final Map<String,String> names=new HashMap<>();
    private final Map<String,String> countries=new HashMap<>();
    private final Map<String,Long> attempted=new HashMap<>();
    private final ExecutorService executor=Executors.newSingleThreadExecutor();
    private final Handler main=new Handler(Looper.getMainLooper());
    private volatile boolean stopped;
    AirlineNameRepository(Context context){
        prefs=context.getSharedPreferences("airline_names",Context.MODE_PRIVATE);
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(context.getAssets().open("airline-countries.json"),StandardCharsets.UTF_8))){
            StringBuilder text=new StringBuilder();String line;while((line=reader.readLine())!=null)text.append(line);
            JSONObject records=new JSONObject(text.toString()).getJSONObject("airlines");
            for(Iterator<String> keys=records.keys();keys.hasNext();){String code=keys.next();countries.put(code,AirlineNames.clean(records.getJSONObject(code).optString("country","")));}
        }catch(IOException|JSONException ignored){/* Missing countries remain explicitly unknown. */}
    }
    String country(String operator){
        String code=LiveryDesign.normalized(operator);
        return AirlineNames.clean(prefs.getString(code+".country",countries.getOrDefault(code,"")));
    }
    String get(String operator,Runnable changed){
        String code=LiveryDesign.normalized(operator);
        if(code.isEmpty())return "";
        if(!names.containsKey(code))names.put(code,AirlineNames.display(code,prefs.getString(code,AirlineNames.known(code))));
        long now=System.currentTimeMillis();
        long saved=prefs.getLong(code+".at",0);
        if(!stopped&&!attempted.containsKey(code)&&(saved==0||now-saved>86400000L||now<saved||country(code).isEmpty())){
            attempted.put(code,now);
            executor.execute(()->{
                String resolved="",resolvedCountry="";
                try{JSONObject response=Network.json("https://api.adsbdb.com/v0/airline/"+code);resolved=parse(code,response);resolvedCountry=parseCountry(code,response);}catch(Exception ignored){/* Offline names remain available. */}
                String result=resolved,resultCountry=resolvedCountry;
                main.post(()->{
                    if(stopped)return;
                    if(!result.isEmpty()||!resultCountry.isEmpty()){
                        if(names.size()>96)names.clear();
                        if(!result.isEmpty())names.put(code,result);
                        SharedPreferences.Editor edit=prefs.edit();
                        if(prefs.getAll().size()>256)edit.clear();
                        if(!result.isEmpty())edit.putString(code,result);
                        if(!resultCountry.isEmpty())edit.putString(code+".country",resultCountry);
                        edit.putLong(code+".at",System.currentTimeMillis()).apply();
                        main.postDelayed(()->attempted.remove(code),86400000L);
                        changed.run();
                    }else main.postDelayed(()->attempted.remove(code),900000);
                });
            });
        }
        return names.getOrDefault(code,AirlineNames.known(code));
    }
    static String parse(String code,JSONObject root){
        JSONObject airline=record(code,root);
        return airline==null?"":AirlineNames.display(code,airline.optString("name",""));
    }
    static String parseCountry(String code,JSONObject root){
        JSONObject airline=record(code,root);
        return airline==null?"":AirlineNames.clean(airline.optString("country",""));
    }
    private static JSONObject record(String code,JSONObject root){
        JSONArray airlines=root.optJSONArray("response");
        if(airlines==null)return null;
        for(int i=0;i<airlines.length();i++){
            JSONObject airline=airlines.optJSONObject(i);
            if(airline!=null&&code.equals(airline.optString("icao","")))return airline;
        }
        return null;
    }
    void stop(){stopped=true;executor.shutdownNow();main.removeCallbacksAndMessages(null);}
}
