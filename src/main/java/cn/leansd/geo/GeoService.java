package cn.leansd.geo;

public interface GeoService {
    /**
     * Get distance(Kilometer) between two locations
     */
    double getDistance(Location firstLocation, Location secondLocation);
}
