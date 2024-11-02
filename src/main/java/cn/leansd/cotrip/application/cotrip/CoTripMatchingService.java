package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import cn.leansd.cotrip.domain.cotrip.CoTrip;
import cn.leansd.cotrip.domain.cotrip.CoTripFactory;
import cn.leansd.cotrip.domain.cotrip.CoTripId;
import cn.leansd.cotrip.domain.cotrip.CoTripRepository;
import cn.leansd.cotrip.domain.plan.PickupLocation;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanCreatedEvent;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.geo.GeoService;
import cn.leansd.geo.haversine.HaversineDistance;
import cn.leansd.site.application.PickupSiteDTO;
import cn.leansd.site.application.PickupSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoTripMatchingService {
    private static final double START_LOCATION_LIMIT = 1.0;
    private final TripPlanRepository tripPlanRepository;
    private final CoTripRepository coTripRepository;
    private final GeoService geoService;
    private final PickupSiteService pickupSiteService;
    private final double thresholdOfMergePickupSite = 0.5; /*Unit(KM). Could be configured in future*/

    @Autowired
    public CoTripMatchingService(TripPlanRepository tripPlanRepository,
                                 CoTripRepository coTripRepository,
                                 GeoService geoService,
                                 PickupSiteService pickupSiteService) {
        this.tripPlanRepository = tripPlanRepository;
        this.coTripRepository = coTripRepository;
        this.geoService = geoService;
        this.pickupSiteService = pickupSiteService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void receivedTripPlanCreatedEvent(TripPlanCreatedEvent event) throws InconsistentStatusException {
        CoTrip coTrip = matchExistingTripPlan(event.getData());
        if (coTrip!=null){
            matchSuccess(coTrip);
        }
    }

    private void matchSuccess(CoTrip coTrip) {
        updateTripPlans(CoTripId.of(coTrip.getId()),coTrip.getTripPlanIdList());
        coTripRepository.save(coTrip);
    }

    private void updateTripPlans(CoTripId coTripId, List<String> tripPlanIds) {
        List<TripPlan> tripPlans = tripPlanIds.stream().map(tripPlanId->
        {
            TripPlan tripPlan = tripPlanRepository.findById(tripPlanId).get();
            tripPlan.joinCoTrip(coTripId);
            PickupSiteDTO siteLocation = pickupSiteService.findNearestPickupSite(tripPlan.getPlanSpecification().getDepartureLocation());
            tripPlan.setPickupLocation(new PickupLocation(siteLocation.getLocation()));
            return tripPlan;
        }).toList();

        assert tripPlans.size() == 2; //当前阶段我们仅支持2个出行计划合并为1个共乘。
        if (thresholdOfMergePickupSite>
                HaversineDistance.between(tripPlans.get(0).getPickupLocation().getLocation(),
                                        tripPlans.get(1).getPickupLocation().getLocation())){
            tripPlans.get(0).setPickupLocation(new PickupLocation(tripPlans.get(1).getPickupLocation()));
        }
        tripPlanRepository.saveAll(tripPlans);
    }

    private CoTrip matchExistingTripPlan(TripPlanDTO tripPlan) {
        List<TripPlan> tripPlans = tripPlanRepository.findAllNotMatching();
        if (tripPlans.isEmpty()) return null;
        List<String> matchedTripPlanIds = new ArrayList<>();
        for (TripPlan plan : tripPlans) {
            if (plan.getId().equals(tripPlan.getId())) continue;
            if (departureTimeNotMatch(plan, tripPlan)) continue;
            if (exceedMaxSeats(plan, tripPlan)) continue;
            if (startLocationNotMatch(plan, tripPlan)) continue;
            if (endLocationNotMatch(plan, tripPlan)) continue;
            matchedTripPlanIds.add(plan.getId());
            break; //当前阶段仅支持匹配一个
        }
        if (matchedTripPlanIds.isEmpty()) return null;
        matchedTripPlanIds.add(tripPlan.getId());
        return CoTripFactory.build(matchedTripPlanIds);
    }

    private boolean departureTimeNotMatch(TripPlan existPlan, TripPlanDTO newPlan) {
        return !new TimeSpanMatcher().match(existPlan.getPlanSpecification().getPlannedDepartureTime(),
                newPlan.getPlanSpecification().getPlannedDepartureTime());
    }

    private boolean endLocationNotMatch(TripPlan existPlan, TripPlanDTO newPlan) {
        return false;
    }

    private boolean startLocationNotMatch(TripPlan existPlan, TripPlanDTO newPlan) {
        return (geoService.getDistance(existPlan.getPlanSpecification().getDepartureLocation(),
                newPlan.getPlanSpecification().getDepartureLocation()) > START_LOCATION_LIMIT);
    }

    private boolean exceedMaxSeats(TripPlan plan, TripPlanDTO tripPlan) {
        return plan.getPlanSpecification().getRequiredSeats() + tripPlan.getPlanSpecification().getRequiredSeats() > 4;
    }
}
