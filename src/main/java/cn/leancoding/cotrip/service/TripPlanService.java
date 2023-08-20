package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.model.location.Location;
import cn.leancoding.cotrip.model.plan.*;
import cn.leancoding.cotrip.model.user.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public TripPlanService(TripPlanRepository tripPlanRepository, EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    public TripPlan createTripPlan(TripPlanDTO tripPlanDTO, UserId creatorId) {

        PlanSpecification spec = tripPlanDTO.getPlanSpecification();
        TripPlan tripPlan = new TripPlan(creatorId,
                spec);
        eventPublisher.publishEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(tripPlan)));
        tripPlanRepository.save(tripPlan);
        return tripPlan;
    }
}
