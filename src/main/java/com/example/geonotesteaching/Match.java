package com.example.geonotesteaching;

final class Match {

    public static boolean isInArea(GeoPoint p, GeoArea a){
        return p.lat()>=a.topLeft().lat() && p.lat()<=a.bottomRight().lat() && p.lon()>=a.topLeft().lon() && p.lon()<=a.bottomRight().lon();
    }

    /**
     * Versión ilustrativa con “record patterns”  para determinar la ubicación de un punto en áreas lógicas.
     * Incluye:
     * - Switch usado como expresión (produce un resultado directamente).
     * - Record pattern: permite extraer latitud y longitud del record GeoPoint.
     * - Condiciones de guarda (when): añaden verificaciones adicionales a cada caso.
     */
    public static String where(GeoPoint p) {
        return switch (p) {
            // Caso ORIGIN: patrón record + guarda que comprueba si lat y lon son ambos 0
            case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";

            // Caso Equator: latitud 0, cualquier longitud
            case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";

            // Caso Greenwich: longitud 0, cualquier latitud
            case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";

            // Caso general: cualquier otro punto, se devuelve como texto "(lat,lon)"
            case GeoPoint(double lat, double lon) -> "(" + lat + "," + lon + ")";
        };
    }
}