package cn.leansd.site.application;

import cn.leansd.geo.Location;
import cn.leansd.geo.haversine.HaversineDistance;
import cn.leansd.site.domain.site.PickupSite;
import cn.leansd.site.domain.site.SiteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class PickupSiteService {
    private PickupSiteRepository pickupSiteRepository;
    private double maxDistance = 0.5; // 0.5km
    public PickupSiteService(@Autowired PickupSiteRepository pickupSiteRepository) {
        this.pickupSiteRepository = pickupSiteRepository;
    }
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
            PickupSite temporarySite = new PickupSite(location, SiteType.TEMPORARY);
            temporarySite.registerEvent(new PickupSiteNotAvailableEvent(location));
            pickupSiteRepository.save(temporarySite);
            return  temporarySite;
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
