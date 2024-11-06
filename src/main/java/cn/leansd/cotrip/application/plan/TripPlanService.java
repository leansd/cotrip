package cn.leansd.cotrip.application.plan;

import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.cotrip.types.TripPlanDTO;
import cn.leansd.vehicle_owner.VehicleOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TripPlanService {
    private final TripPlanRepository tripPlanRepository;
    private final VehicleOwnerService vehicleOwnerService;

    @Autowired
    public TripPlanService(TripPlanRepository tripPlanRepository,
                            VehicleOwnerService vehicleOwnerService) {
        this.tripPlanRepository = tripPlanRepository;
        this.vehicleOwnerService = vehicleOwnerService;
    }

    @Transactional
    public TripPlanDTO createTripPlan(TripPlanDTO tripPlanDTO) throws NoVehicleOwnerException {
        TripPlan tripPlan = TripPlanFactory.build(
                tripPlanDTO);
        onlyVehicleOwnerCanCreateTripPlan(tripPlan.getCreatorId(),tripPlanDTO.getPlanType());
        tripPlanRepository.save(tripPlan);
        return TripPlanConverter.toDTO(tripPlan);
    }

    private void onlyVehicleOwnerCanCreateTripPlan(String creatorId, String planType) throws NoVehicleOwnerException {
        if (!TripPlanType.HITCHHIKING_PROVIDER.name().equals(planType)) return;
        if (!vehicleOwnerService.isVehicleOwner(creatorId)){
            throw new NoVehicleOwnerException(creatorId);
        }
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
