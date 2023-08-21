package cn.leansd.geo;

import cn.leansd.geo.location.Location;

public interface GeoService {
    /**
     * Get distance(Kilometer) between two locations
     */
    double getDistance(Location location_1, Location location_2);
}
