package cn.leansd.cotrip.application.plan;

import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.cotrip.types.PlanSpecification;
import cn.leansd.cotrip.types.TripPlanDTO;
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


    public TripPlanDTO retrieveTripPlan(TripPlanId tripPlanId, UserId userId) throws RequestedResourceNotFound {
        Optional<TripPlan> tripPlan = tripPlanRepository.findById(tripPlanId.getId());
        if (!tripPlan.isPresent()) throw new RequestedResourceNotFound("trip plan:" + tripPlanId.getId());
        return TripPlanConverter.toDTO(tripPlan.get());
    }

    public List<TripPlanDTO> retrieveTripPlans(UserId userId) {
        List<TripPlan> tripPlans = tripPlanRepository.findAllByCreatorId(userId.getId());
        return TripPlanConverter.toDTOs(tripPlans);
    }

    public TripPlanDTO cancelTripPlan(TripPlanId tripPlanId, UserId userId) throws RequestedResourceNotFound {
        Optional<TripPlan> tripPlan = tripPlanRepository.findById(tripPlanId.getId());
        if (!tripPlan.isPresent()) throw new RequestedResourceNotFound("trip plan:" + tripPlanId.getId());
        tripPlan.get().cancel();
        tripPlanRepository.save(tripPlan.get());
        return TripPlanConverter.toDTO(tripPlan.get());
    }
}
