package cn.leansd.cotrip.service.plan;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.base.model.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final EventPublisher eventPublisher;

    @Autowired
    public TripPlanService(TripPlanRepository tripPlanRepository, EventPublisher eventPublisher) {
        this.tripPlanRepository = tripPlanRepository;
        this.eventPublisher = eventPublisher;
    }

    public TripPlanDTO createTripPlan(TripPlanDTO tripPlanDTO, UserId creatorId) {
        PlanSpecification spec = tripPlanDTO.getPlanSpecification();
        TripPlan tripPlan = new TripPlan(creatorId,
                spec);
        eventPublisher.publishEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(tripPlan)));
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toDTO(tripPlan);
    }

    public void joinedCoTrip(List<TripPlanId> tripPlanIds) {
    }
}
