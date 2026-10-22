package com.nearsky.tv;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.*;
import java.util.concurrent.*;

/** Main-thread cache with bounded entries, deduplicated requests and negative caching. */
final class RouteRepository {
    static final class Result {
        final FlightRoute route;
        final boolean loading;
        Result(FlightRoute route,boolean loading){this.route=route;this.loading=loading;}
    }
    private static final Result LOADING=new Result(null,true),UNAVAILABLE=new Result(null,false);
    private static final class Entry {
        final Result result;final long expires;
        Entry(FlightRoute route,long now){result=new Result(route,false);expires=now+(route==null?300000L:1800000L);}
    }
    private final LinkedHashMap<String,Entry> cache=new LinkedHashMap<>(64,.75f,true);
    private final Set<String> pending=new HashSet<>();
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private final Handler main=new Handler(Looper.getMainLooper());
    private volatile boolean stopped;
    Result get(String callsign,Runnable changed){
        String key=FlightRoute.callsign(callsign);
        if(key.isEmpty()||stopped)return UNAVAILABLE;
        Entry entry=cache.get(key);
        if(entry!=null&&SystemClock.elapsedRealtime()<entry.expires)return entry.result;
        if(pending.size()>=6)return LOADING;
        if(pending.add(key))worker.execute(()->{
            FlightRoute fetched=null;
            try{fetched=FlightRoute.parse(key,Network.json("https://api.adsbdb.com/v0/callsign/"+key));}
            catch(Exception ignored){/* Missing coverage and outages use an explicit unavailable state. */}
            FlightRoute result=fetched;
            main.post(()->{
                if(stopped)return;
                pending.remove(key);cache.put(key,new Entry(result,SystemClock.elapsedRealtime()));
                while(cache.size()>64)cache.remove(cache.keySet().iterator().next());
                changed.run();
            });
        });
        return LOADING;
    }
    void stop(){stopped=true;worker.shutdownNow();main.removeCallbacksAndMessages(null);cache.clear();pending.clear();}
}
