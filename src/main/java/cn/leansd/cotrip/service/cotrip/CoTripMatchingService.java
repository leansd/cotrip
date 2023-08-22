package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.GenericId;
import cn.leansd.cotrip.model.cotrip.*;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.geo.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoTripMatchingService {
    private static final double START_LOCATION_LIMIT = 1.0;
    private final TripPlanRepository tripPlanRepository;
    private final CoTripRepository coTripRepository;
    private final EventPublisher eventPublisher;
    private final GeoService geoService;
    private final TripPlanService tripPlanService;
    @Autowired
    public CoTripMatchingService(TripPlanRepository tripPlanRepository,
                                 TripPlanService tripPlanService,
                                 CoTripRepository coTripRepository,
                                 GeoService geoService,
                                 EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.tripPlanService = tripPlanService;
        this.coTripRepository = coTripRepository;
        this.geoService = geoService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener(TripPlanCreatedEvent.class)
    public void receivedTripPlanCreatedEvent(TripPlanCreatedEvent event) throws InconsistentStatusException {
        CoTrip coTrip = matchExistingTripPlan(event.getData());
        if (coTrip!=null){
            matchSuccess(event.getData().getId(),coTrip);
        }
    }

    private void matchSuccess(String thePlanId, CoTrip coTrip) {
        coTrip.getTripPlanIdList().add(thePlanId);
        tripPlanService.joinedCoTrip(CoTripId.of(coTrip.getId()),coTrip.getTripPlanIdList().stream().map(
                id-> TripPlanId.of(id)).collect(Collectors.toList()));
        coTripRepository.save(coTrip);
        eventPublisher.publishEvent(new CoTripCreatedEvent(coTrip.getId()));
    }

    private CoTrip matchExistingTripPlan(TripPlanDTO tripPlan) {
        List<TripPlan> tripPlans = tripPlanRepository.findAllNotMatching();
        if (tripPlans.size() == 0) return null;
        for (TripPlan plan : tripPlans) {
            if (plan.getId().equals(tripPlan.getId())) continue;
            if (departureTimeNotMatch(plan, tripPlan)) continue;
            if (exceedMaxSeats(plan, tripPlan)) continue;
            if (startLocationNotMatch(plan, tripPlan)) continue;
            if (endLocationNotMatch(plan, tripPlan)) continue;
            return CoTrip.builder()
                    .status(CoTripStatus.CREATED)
                    .tripPlanIdList(tripPlans.stream().map(TripPlan::getId)
                            .collect(Collectors.toList()))
                    .build();
        }
        return null;
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
