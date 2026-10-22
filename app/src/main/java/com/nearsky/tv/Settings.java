package com.nearsky.tv;

import android.content.Context;
import android.content.SharedPreferences;

public final class Settings {
    public final String name;
    public final double lat, lon, radiusKm;
    public final boolean photos;
    public Settings(String name, double lat, double lon, double radiusKm, boolean photos) {
        if (name.trim().isEmpty() || !Geo.valid(lat, lon) || !Double.isFinite(radiusKm) || radiusKm < 1 || radiusKm > 200)
            throw new IllegalArgumentException("Enter a name, valid coordinates and radius between 1 and 200 km.");
        this.name = name.trim(); this.lat = lat; this.lon = lon; this.radiusKm = radiusKm; this.photos = photos;
    }
    public static Settings load(Context context) {
        SharedPreferences p = context.getSharedPreferences("location", Context.MODE_PRIVATE);
        return new Settings(p.getString("name", "Ghaziabad"), Double.parseDouble(p.getString("lat", "28.6692")),
                Double.parseDouble(p.getString("lon", "77.4538")), Double.parseDouble(p.getString("radius", "50")), p.getBoolean("preferPhotos", false));
    }
    public void save(Context context) {
        context.getSharedPreferences("location", Context.MODE_PRIVATE).edit().putString("name", name)
                .putString("lat", Double.toString(lat)).putString("lon", Double.toString(lon))
                .putString("radius", Double.toString(radiusKm)).putBoolean("preferPhotos", photos).apply();
    }
}
