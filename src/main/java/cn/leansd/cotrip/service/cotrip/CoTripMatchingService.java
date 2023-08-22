package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.model.cotrip.CoTrip;
import cn.leansd.cotrip.model.cotrip.CoTripCreatedEvent;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.cotrip.CoTripStatus;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.model.plan.TripPlanStatus;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
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
    @Autowired
    public CoTripMatchingService(TripPlanRepository tripPlanRepository,
                                 CoTripRepository coTripRepository,
                                 GeoService geoService,
                                 EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.coTripRepository = coTripRepository;
        this.geoService = geoService;
        this.eventPublisher = eventPublisher;
    }

    @EventListener(TripPlanCreatedEvent.class)
    public void receivedTripPlanCreatedEvent(TripPlanCreatedEvent event) throws InconsistentStatusException {
        CoTrip coTrip = matchExistingTripPlan(event.getData());
        if (coTrip!=null){
            matchSuccess(coTrip);
        }
    }

    private void matchSuccess(CoTrip coTrip) {
        coTripRepository.save(coTrip);
        coTrip.getTripPlanIdList().forEach(tripPlanId -> {
            System.out.println(tripPlanId);
            TripPlan tripPlan = tripPlanRepository.findById(tripPlanId).get();
            tripPlan.setStatus(TripPlanStatus.JOINED);
            tripPlanRepository.save(tripPlan);
        });
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
