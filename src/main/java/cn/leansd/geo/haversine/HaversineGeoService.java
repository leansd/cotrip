package cn.leansd.geo.haversine;

import cn.leansd.geo.GeoService;
import cn.leansd.geo.Location;
import org.springframework.stereotype.Service;

@Service
public class HaversineGeoService implements GeoService {
    @Override
    public double getDistance(Location firstLocation, Location secondLocation) {
        return HaversineDistance.between(firstLocation.getLatitude(), firstLocation.getLongitude(),
                secondLocation.getLatitude(), secondLocation.getLongitude());
    }
}
