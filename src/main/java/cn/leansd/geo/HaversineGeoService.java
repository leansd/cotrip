package cn.leansd.geo;

import cn.leansd.geo.location.Location;
import org.springframework.stereotype.Service;

@Service
public class HaversineGeoService implements GeoService {
    @Override
    public double getDistance(Location location_1, Location location_2) {
        return HaversineDistance.between(location_1.getLatitude(), location_1.getLongitude(),
                location_2.getLatitude(), location_2.getLongitude());
    }
}
