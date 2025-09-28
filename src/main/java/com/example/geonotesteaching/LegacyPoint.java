package com.example.geonotesteaching;

import java.util.Objects;

public class LegacyPoint {
    private double lat;
    private double lon;

    public LegacyPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "LegacyPoint{ lat = "+ lat + ", lon = "+ lon +"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LegacyPoint)) {
            return false;
        }else{
            return this == obj;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
