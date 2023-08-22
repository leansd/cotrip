package cn.leansd.cotrip.service.plan;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.cotrip.model.cotrip.CoTripId;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.base.model.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;

    @Autowired
    public TripPlanService(TripPlanRepository tripPlanRepository) {
        this.tripPlanRepository = tripPlanRepository;
    }

    @Transactional
    public TripPlanDTO createTripPlan(TripPlanDTO tripPlanDTO, UserId creatorId) {
        PlanSpecification spec = tripPlanDTO.getPlanSpecification();
        TripPlan tripPlan = TripPlanFactory.build(creatorId,
                spec);
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toDTO(tripPlan);
    }

    public void joinedCoTrip(CoTripId coTripId, List<TripPlanId> tripPlanIds) {
        tripPlanIds.forEach(tripPlanId -> {
            TripPlan tripPlan = tripPlanRepository.findById(tripPlanId.getId()).get();
            tripPlan.joinedCoTrip(coTripId);
            tripPlanRepository.save(tripPlan);
        });
    }

    public TripPlanDTO retrieveTripPlan(TripPlanId tripPlanId, UserId userId) throws RequestedResourceNotFound {
        Optional<TripPlan> tripPlan = tripPlanRepository.findById(tripPlanId.getId());
        if (!tripPlan.isPresent()) throw new RequestedResourceNotFound("trip plan:" + tripPlanId.getId());
        return TripPlanConverter.toDTO(tripPlan.get());
    }
}
