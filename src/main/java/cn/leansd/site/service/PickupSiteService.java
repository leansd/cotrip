package cn.leansd.site.service;

import cn.leansd.geo.Location;
import cn.leansd.geo.haversine.HaversineDistance;
import cn.leansd.site.model.site.PickupSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PickupSiteService {
    @Autowired PickupSiteRepository pickupSiteRepository;

    public PickupSiteDTO findNearestPickupSite(Location location) {
        return pickupSiteRepository.findAll().stream()
                .min(Comparator.comparingDouble(pickupSite -> HaversineDistance.between(
                        location.getLatitude(), location.getLongitude(),
                        pickupSite.getLocation().getLatitude(), pickupSite.getLocation().getLongitude())))
                .map(PickupSiteDTO::new)
                .orElse(null);
    }


    public void addPickupSite(Location hqStationSouth) {
        PickupSite pickupSite = new PickupSite(hqStationSouth);
        pickupSiteRepository.save(pickupSite);
    }
}
