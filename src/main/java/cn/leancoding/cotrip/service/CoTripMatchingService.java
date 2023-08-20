package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.model.cotrip.CoTrip;
import cn.leancoding.cotrip.model.cotrip.CoTripCreatedEvent;
import cn.leancoding.cotrip.model.cotrip.CoTripRepository;
import cn.leancoding.cotrip.model.cotrip.CoTripStatus;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanConverter;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoTripMatchingService {
    private final TripPlanRepository tripPlanRepository;
    private final CoTripRepository coTripRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public CoTripMatchingService(TripPlanRepository tripPlanRepository, CoTripRepository coTripRepository,  EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.coTripRepository = coTripRepository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener(TripPlanCreatedEvent.class)
    public void receivedTripPlanCreatedEvent(TripPlanCreatedEvent event) throws InconsistentStatusException {
        CoTrip coTrip = matchExistingTripPlan(event.getData());
        if (coTrip!=null){
            coTripRepository.save(coTrip);
            eventPublisher.publishEvent(new CoTripCreatedEvent(coTrip.getId()));
        }
    }

    private CoTrip matchExistingTripPlan(TripPlanDTO tripPlan) {
        List<TripPlan> tripPlans = tripPlanRepository.findAllNotMatching();
        if (tripPlans.size() == 0) return null;
        for (TripPlan plan : tripPlans) {
            if (plan.getId().equals(tripPlan.getId())) continue;
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

    private boolean endLocationNotMatch(TripPlan existPlan, TripPlanDTO newPlan) {
        return false;
    }

    private boolean startLocationNotMatch(TripPlan existPlan, TripPlanDTO newPlan) {
        return false;
    }

    private boolean exceedMaxSeats(TripPlan plan, TripPlanDTO tripPlan) {
        return plan.getPlanSpecification().getRequiredSeats() + tripPlan.getPlanSpecification().getRequiredSeats() > 4;
    }
}
