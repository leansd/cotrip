package cn.leansd.cotrip.service.plan;

import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanConverter;
import cn.leansd.cotrip.model.plan.TripPlanId;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
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
    public TripPlanDTO createTripPlan(TripPlanDTO tripPlanDTO) {
        TripPlan tripPlan = TripPlanFactory.build(
                tripPlanDTO);
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
