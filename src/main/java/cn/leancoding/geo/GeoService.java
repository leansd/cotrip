package cn.leancoding.geo;

import cn.leancoding.cotrip.model.location.Location;

public interface GeoService {
    /**
     * Get distance(Kilometer) between two locations
     */
    double getDistance(Location location_1, Location location_2);
}
