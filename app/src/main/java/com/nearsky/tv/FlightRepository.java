package com.nearsky.tv;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.*;
import java.util.concurrent.*;

final class FlightRepository {
    interface Listener { void onFlights(List<Flight> flights, long receivedElapsed, String error); }
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private volatile boolean stopped;
    void start(Settings s, Listener listener) {
        executor.scheduleWithFixedDelay(() -> {
            try {
                String url = String.format(Locale.US, "https://opendata.adsb.fi/api/v3/lat/%.6f/lon/%.6f/dist/%d", s.lat, s.lon, (int)Math.ceil(s.radiusKm / 1.852));
                List<Flight> flights = FlightParser.parse(Network.json(url), System.currentTimeMillis());
                long at = SystemClock.elapsedRealtime();
                main.post(() -> { if (!stopped) listener.onFlights(flights, at, null); });
            } catch (Exception e) {
                main.post(() -> { if (!stopped) listener.onFlights(null, 0, "Unable to refresh · retrying"); });
            }
        }, 0, 15, TimeUnit.SECONDS);
    }
    void stop() { stopped = true; executor.shutdownNow(); main.removeCallbacksAndMessages(null); }
}
