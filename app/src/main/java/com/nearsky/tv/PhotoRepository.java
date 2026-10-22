package com.nearsky.tv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.LruCache;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

final class PhotoRepository {
    static final class Photo {
        final Bitmap bitmap;
        final String aircraft, owner, sourceUrl, operator;
        Photo(Bitmap b, String aircraft, String owner, String source, String operator) {
            bitmap = b; this.aircraft = aircraft; this.owner = owner; sourceUrl = source; this.operator = LiveryDesign.normalized(operator);
        }
    }
    private final LruCache<String, Photo> cache = new LruCache<>(12);
    private final Map<String, Long> attempted = new HashMap<>();
    private final Set<String> pending = new HashSet<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private volatile boolean stopped;
    Photo get(String hex, boolean loadPhotos, Runnable changed) {
        String key = hex + (loadPhotos ? ":photo" : ":metadata");
        Photo p = cache.get(key);
        if (p != null) return p;
        Long at = attempted.get(key);
        if (!stopped && !pending.contains(key) && (at == null || SystemClock.elapsedRealtime() - at > 900000)) {
            pending.add(key);
            executor.execute(() -> {
                Photo photo = null;
                try {
                    JSONObject a = Network.json("https://api.adsbdb.com/v0/aircraft/" + hex).getJSONObject("response").getJSONObject("aircraft");
                    String address = a.optString("url_photo", "");
                    Bitmap bitmap = null;
                    // Only load the photo host documented by adsbdb; never arbitrary URLs.
                    if (loadPhotos && (address.startsWith("https://image.airport-data.com/") || address.startsWith("https://airport-data.com/"))) {
                        byte[] raw = Network.bytes(address, 4 * 1024 * 1024);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
                        options.inSampleSize = 1;
                        while (options.outWidth / options.inSampleSize > 1200 || options.outHeight / options.inSampleSize > 800) options.inSampleSize *= 2;
                        options.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
                    }
                    photo = new Photo(bitmap, (a.optString("manufacturer", "") + " " + a.optString("type", "")).trim(), a.optString("registered_owner", ""), bitmap == null ? "" : address, a.optString("registered_owner_operator_flag_code", ""));
                } catch (Exception ignored) { /* Photos are optional. Keep the honest illustration fallback. */ }
                Photo loaded = photo;
                main.post(() -> {
                    if (stopped) return;
                    pending.remove(key); attempted.put(key, SystemClock.elapsedRealtime());
                    if (attempted.size() > 200) attempted.clear();
                    if (loaded != null) cache.put(key, loaded);
                    changed.run();
                });
            });
        }
        return null;
    }
    void stop() { stopped = true; executor.shutdownNow(); main.removeCallbacksAndMessages(null); cache.evictAll(); }
}
