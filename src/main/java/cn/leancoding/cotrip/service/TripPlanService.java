package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.model.location.Location;
import cn.leancoding.cotrip.model.plan.PlanSpecification;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
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
        Location departureLocation = new Location(tripPlanDTO.getDepartureLocation().getLatitude(), tripPlanDTO.getDepartureLocation().getLongitude());
        Location arrivalLocation = new Location(tripPlanDTO.getArrivalLocation().getLatitude(), tripPlanDTO.getArrivalLocation().getLongitude());

        TripPlan tripPlan = new TripPlan(creatorId,
                new PlanSpecification(departureLocation, arrivalLocation, tripPlanDTO.getPlannedDepartureTime()));
        eventPublisher.publishEvent(new TripPlanCreatedEvent(tripPlan.getId()));
        tripPlanRepository.save(tripPlan);
        return tripPlan;
    }

    // 其他服务方法
}
