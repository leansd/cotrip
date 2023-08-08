package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.event.EventPublisher;
import cn.leancoding.cotrip.model.location.Location;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public TripPlanService(TripPlanRepository tripPlanRepository, EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    public TripPlan createTripPlan(TripPlanDTO tripPlanDTO) {
        Location departureLocation = new Location(tripPlanDTO.getDepartureLocation().getLatitude(), tripPlanDTO.getDepartureLocation().getLongitude());
        Location arrivalLocation = new Location(tripPlanDTO.getArrivalLocation().getLatitude(), tripPlanDTO.getArrivalLocation().getLongitude());

        TripPlan tripPlan = new TripPlan(UUID.randomUUID().toString(), departureLocation, arrivalLocation, tripPlanDTO.getPlannedDepartureTime(), tripPlanDTO.getFlexibleWaitTime());
        tripPlanRepository.save(tripPlan);

        eventPublisher.publishEvent(new TripPlanCreatedEvent(tripPlan.getId()));

        return tripPlan;
    }

    // 其他服务方法
}
