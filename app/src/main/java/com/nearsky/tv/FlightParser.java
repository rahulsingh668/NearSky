package com.nearsky.tv;

import java.util.*;
import org.json.*;

final class FlightParser {
    static List<Flight> parse(JSONObject root, long receivedAt) throws JSONException {
        double now = root.optDouble("now", Double.NaN);
        if (now > 100000000000.0) now /= 1000; // readsb seconds; adsb.lol milliseconds
        double feedAge = receivedAt / 1000.0 - now;
        if (!Double.isFinite(now) || feedAge > 60 || feedAge < -300) throw new JSONException("Feed clock is stale or device clock is incorrect");
        JSONArray aircraft = root.optJSONArray("aircraft");
        if (aircraft == null) aircraft = root.optJSONArray("ac");
        if (aircraft == null) throw new JSONException("Missing aircraft array");
        List<Flight> result = new ArrayList<>();
        for (int i = 0; i < aircraft.length(); i++) {
            JSONObject a = aircraft.optJSONObject(i);
            if (a == null) continue;
            double alt = a.optDouble("alt_baro", a.optDouble("alt_geom", Double.NaN));
            result.add(new Flight(a.optString("hex", ""), a.optString("flight", "").trim(), a.optString("r", ""),
                    a.optString("t", ""), a.optString("desc", ""), a.optDouble("lat", Double.NaN),
                    a.optDouble("lon", Double.NaN), alt, a.optDouble("gs", Double.NaN),
                    a.optDouble("track", Double.NaN), a.optDouble("seen_pos", Double.NaN) + Math.max(0, feedAge),
                    "ground".equals(a.optString("alt_baro", ""))));
        }
        return result;
    }
}
