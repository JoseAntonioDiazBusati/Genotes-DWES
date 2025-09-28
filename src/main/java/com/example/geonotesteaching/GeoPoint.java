package com.example.geonotesteaching;

public record GeoPoint(double lat, double lon) {
    public GeoPoint {
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("Latitud inválida: " + lat);
        if (lon < -180 || lon > 180) throw new IllegalArgumentException("Longitud inválida: " + lon);
    }
}