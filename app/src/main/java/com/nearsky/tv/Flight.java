package com.nearsky.tv;

public final class Flight {
    public final String hex, callsign, registration, type, description;
    public final double lat, lon, altitudeFt, speedKnots, heading, positionAgeSeconds;
    public final boolean grounded;
    public Flight(String hex, String callsign, String registration, String type, String description,
                  double lat, double lon, double altitudeFt, double speedKnots, double heading,
                  double positionAgeSeconds, boolean grounded) {
        this.hex = hex; this.callsign = callsign; this.registration = registration;
        this.type = type; this.description = description; this.lat = lat; this.lon = lon;
        this.altitudeFt = altitudeFt; this.speedKnots = speedKnots; this.heading = heading;
        this.positionAgeSeconds = positionAgeSeconds; this.grounded = grounded;
    }
    public String title() { return callsign.isEmpty() ? (registration.isEmpty() ? hex.toUpperCase(java.util.Locale.ROOT) : registration) : callsign; }
}
