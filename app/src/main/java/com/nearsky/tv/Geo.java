package com.nearsky.tv;

import java.util.*;

public final class Geo {
    private Geo() { }
    public static boolean valid(double lat, double lon) {
        return Double.isFinite(lat) && Double.isFinite(lon) && Math.abs(lat) <= 90 && Math.abs(lon) <= 180;
    }
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double a = Math.toRadians(lat2 - lat1), b = Math.toRadians(lon2 - lon1);
        double h = Math.pow(Math.sin(a / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(b / 2), 2);
        return 6371.0088 * 2 * Math.asin(Math.sqrt(Math.min(1, h)));
    }
    public static double bearing(double lat1, double lon1, double lat2, double lon2) {
        double d = Math.toRadians(lon2 - lon1), a = Math.toRadians(lat1), b = Math.toRadians(lat2);
        return (Math.toDegrees(Math.atan2(Math.sin(d) * Math.cos(b), Math.cos(a) * Math.sin(b) - Math.sin(a) * Math.cos(b) * Math.cos(d))) + 360) % 360;
    }
    public static String compass(double bearing) {
        return new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"}[(int)Math.round(bearing / 45) % 8];
    }
    public static List<Flight> nearest(List<Flight> source, double lat, double lon, double radiusKm, double elapsedSeconds) {
        if (!valid(lat, lon) || !Double.isFinite(radiusKm) || radiusKm <= 0) throw new IllegalArgumentException("Invalid search area");
        List<Flight> valid = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Flight f : source) {
            if (!f.hex.matches("[0-9a-fA-F]{6}") || f.grounded || !valid(f.lat, f.lon)
                    || !Double.isFinite(f.positionAgeSeconds) || f.positionAgeSeconds < 0
                    || f.positionAgeSeconds + Math.max(0, elapsedSeconds) > 60
                    || distanceKm(lat, lon, f.lat, f.lon) > radiusKm || !seen.add(f.hex.toLowerCase(Locale.ROOT))) continue;
            valid.add(f);
        }
        valid.sort(Comparator.comparingDouble((Flight f) -> distanceKm(lat, lon, f.lat, f.lon)).thenComparing(f -> f.hex));
        return new ArrayList<>(valid.subList(0, Math.min(3, valid.size())));
    }
}
