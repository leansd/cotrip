package cn.leansd.geo.haversine;

import cn.leansd.geo.Location;

public class HaversineDistance {
    public static double between(double lat1, double lon1, double lat2, double lon2) {
        int earthRadiusKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadiusKm * c;
    }

    public static double between(Location location1, Location location2) {
        return between(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());
    }
}
