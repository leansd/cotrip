package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.model.cotrip.CoTrip;
import cn.leansd.cotrip.model.cotrip.CoTripFactory;
import cn.leansd.cotrip.model.cotrip.CoTripId;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leansd.cotrip.model.plan.TripPlanId;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.geo.GeoService;
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
    private final TripPlanService tripPlanService;
    @Autowired
    public CoTripMatchingService(TripPlanRepository tripPlanRepository,
                                 TripPlanService tripPlanService,
                                 CoTripRepository coTripRepository,
                                 GeoService geoService) {
        this.tripPlanRepository = tripPlanRepository;
        this.tripPlanService = tripPlanService;
        this.coTripRepository = coTripRepository;
        this.geoService = geoService;
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
        tripPlanService.joinedCoTrip(CoTripId.of(coTrip.getId()),coTrip.getTripPlanIdList().stream().map(
                id-> TripPlanId.of(id)).collect(Collectors.toList()));
        coTripRepository.save(coTrip);
    }

    private CoTrip matchExistingTripPlan(TripPlanDTO tripPlan) {
        List<TripPlan> tripPlans = tripPlanRepository.findAllNotMatching();
        if (tripPlans.size() == 0) return null;
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
        if (matchedTripPlanIds.size() == 0) return null;
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
        if (geoService.getDistance(existPlan.getPlanSpecification().getDepartureLocation(),
                newPlan.getPlanSpecification().getDepartureLocation()) > START_LOCATION_LIMIT)
            return true;
        return false;
    }

    private boolean exceedMaxSeats(TripPlan plan, TripPlanDTO tripPlan) {
        return plan.getPlanSpecification().getRequiredSeats() + tripPlan.getPlanSpecification().getRequiredSeats() > 4;
    }
}
