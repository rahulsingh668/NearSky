package com.nearsky.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** Resolve airline names by exact ICAO code, never from an aircraft's registered owner. */
final class AirlineNameRepository {
    private final SharedPreferences prefs;
    private final Map<String,String> names=new HashMap<>();
    private final Map<String,Long> attempted=new HashMap<>();
    private final ExecutorService executor=Executors.newSingleThreadExecutor();
    private final Handler main=new Handler(Looper.getMainLooper());
    private volatile boolean stopped;
    AirlineNameRepository(Context context){prefs=context.getSharedPreferences("airline_names",Context.MODE_PRIVATE);}
    String get(String operator,Runnable changed){
        String code=LiveryDesign.normalized(operator);
        if(code.isEmpty())return "";
        if(!names.containsKey(code))names.put(code,AirlineNames.display(code,prefs.getString(code,AirlineNames.known(code))));
        long now=System.currentTimeMillis();
        long saved=prefs.getLong(code+".at",0);
        if(!stopped&&!attempted.containsKey(code)&&(saved==0||now-saved>86400000L||now<saved)){
            attempted.put(code,now);
            executor.execute(()->{
                String resolved="";
                try{resolved=parse(code,Network.json("https://api.adsbdb.com/v0/airline/"+code));}catch(Exception ignored){/* Offline names remain available. */}
                String result=resolved;
                main.post(()->{
                    if(stopped)return;
                    if(!result.isEmpty()){
                        if(names.size()>96)names.clear();
                        names.put(code,result);
                        SharedPreferences.Editor edit=prefs.edit();
                        if(prefs.getAll().size()>256)edit.clear();
                        edit.putString(code,result).putLong(code+".at",System.currentTimeMillis()).apply();
                        main.postDelayed(()->attempted.remove(code),86400000L);
                        changed.run();
                    }else main.postDelayed(()->attempted.remove(code),900000);
                });
            });
        }
        return names.getOrDefault(code,AirlineNames.known(code));
    }
    static String parse(String code,JSONObject root){
        JSONArray airlines=root.optJSONArray("response");
        if(airlines==null)return "";
        for(int i=0;i<airlines.length();i++){
            JSONObject airline=airlines.optJSONObject(i);
            if(airline!=null&&code.equals(airline.optString("icao","")))return AirlineNames.display(code,airline.optString("name",""));
        }
        return "";
    }
    void stop(){stopped=true;executor.shutdownNow();main.removeCallbacksAndMessages(null);}
}
