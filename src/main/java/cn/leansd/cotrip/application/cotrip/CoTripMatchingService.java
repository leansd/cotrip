package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.application.cotrip.filter.CandidatesFilter;
import cn.leansd.cotrip.application.cotrip.filter.CandidatesFilterFactory;
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
import cn.leansd.site.service.PickupSiteDTO;
import cn.leansd.site.service.PickupSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoTripMatchingService {
    private static final double START_LOCATION_LIMIT = 1.0;
    private final TripPlanRepository tripPlanRepository;
    private final CoTripRepository coTripRepository;
    private final GeoService geoService;
    private final PickupSiteService pickupSiteService;
    private final double thresholdOfMergePickupSite = 0.5; //KM;

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
        List<TripPlan> tripPlans = filter(event.getData());
        CoTrip coTrip = match(tripPlans, event.getData());
        execute(coTrip);
    }

    private List<TripPlan> filter(TripPlanDTO tripPlan){
        CandidatesFilter filter = CandidatesFilterFactory.build(tripPlan.getPlanType(),tripPlanRepository);
        return filter.filter(tripPlan);
    }
    private void execute(CoTrip coTrip) {
        if (coTrip ==null) return;
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
        }).collect(Collectors.toList());

        assert tripPlans.size() == 2; //当前阶段我们仅支持2个出行计划合并为1个共乘。
        if (thresholdOfMergePickupSite>
                HaversineDistance.between(tripPlans.get(0).getPickupLocation().getLocation(),
                                        tripPlans.get(1).getPickupLocation().getLocation())){
            tripPlans.get(0).setPickupLocation(new PickupLocation(tripPlans.get(1).getPickupLocation()));
        }
        tripPlanRepository.saveAll(tripPlans);
    }

    private CoTrip match(List<TripPlan> candidates, TripPlanDTO newPlan) {
        if (candidates.size() == 0) return null;
        List<String> matchedTripPlanIds = new ArrayList<>();
        for (TripPlan candidate : candidates) {
            if (candidate.getId().equals(newPlan.getId())) continue;
            if (departureTimeNotMatch(candidate, newPlan)) continue;
            if (exceedMaxSeats(candidate, newPlan)) continue;
            if (startLocationNotMatch(candidate, newPlan)) continue;
            if (endLocationNotMatch(candidate, newPlan)) continue;
            matchedTripPlanIds.add(candidate.getId());
            break; //当前阶段仅支持匹配一个
        }
        if (matchedTripPlanIds.size() == 0) return null;
        matchedTripPlanIds.add(newPlan.getId());
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
        if (geoService.getDistance(existPlan.getPlanSpecification().getDepartureLocation(),
                newPlan.getPlanSpecification().getDepartureLocation()) > START_LOCATION_LIMIT)
            return true;
        return false;
    }

    private boolean exceedMaxSeats(TripPlan plan, TripPlanDTO tripPlan) {
        return plan.getPlanSpecification().getRequiredSeats() + tripPlan.getPlanSpecification().getRequiredSeats() > 4;
    }
}
