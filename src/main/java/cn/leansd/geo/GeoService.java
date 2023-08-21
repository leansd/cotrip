package cn.leansd.geo;

public interface GeoService {
    /**
     * Get distance(Kilometer) between two locations
     */
    double getDistance(Location location_1, Location location_2);
}
