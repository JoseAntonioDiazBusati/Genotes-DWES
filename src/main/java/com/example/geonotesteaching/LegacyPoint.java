package com.example.geonotesteaching;

import java.util.Objects;
/**
 * Ventajas de usar records:
 * - Menos código repetitivo (equals, hashCode, toString y getters son automáticos).
 * - Representan claramente "datos inmutables".
 * - Código más legible y fácil de mantener.
 *
 * Cuándo no usar records:
 * - Si necesitas mutabilidad (los fields en records son finales).
 * - Si la clase requiere jerarquía de herencia (los records no pueden extender de otra clase, solo implementar interfaces).
 * - Si necesitas mucha lógica interna más allá de representar datos.
 */


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
