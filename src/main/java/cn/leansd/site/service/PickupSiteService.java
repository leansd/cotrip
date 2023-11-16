package cn.leansd.site.service;

import cn.leansd.geo.Location;
import cn.leansd.geo.haversine.HaversineDistance;
import cn.leansd.site.model.site.PickupSite;
import cn.leansd.site.model.site.SiteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class PickupSiteService {
    @Autowired PickupSiteRepository pickupSiteRepository;
    private double maxDistance = 0.5; // 0.5km
    public PickupSiteDTO findNearestPickupSite(Location location) {
        PickupSite nearestSite =  pickupSiteRepository.findAll().stream()
                .min(Comparator.comparingDouble(pickupSite -> HaversineDistance.between(
                        location, pickupSite.getLocation())))
                .orElse(null);
        nearestSite = guaranteeValid(location, nearestSite);
        return new PickupSiteDTO(nearestSite);
    }

    private PickupSite guaranteeValid(Location location, PickupSite pickupSite) {
        if (pickupSite==null || HaversineDistance.between(location,pickupSite.getLocation())>maxDistance){
            return new PickupSite(location, SiteType.TEMPORARY);
        }else{
            return pickupSite;
        }
    }


    public void addPickupSite(Location hqStationSouth) {
        PickupSite pickupSite = new PickupSite(hqStationSouth, SiteType.MANAGED);
        pickupSiteRepository.save(pickupSite);
    }

    public void setMaxPickupSiteDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
}
