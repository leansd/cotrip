package cn.leansd.cotrip.domain.plan;


import cn.leansd.cotrip.types.TripPlanDTO;

import java.util.List;

public class TripPlanConverter {
    private TripPlanConverter(){}
    public static TripPlanDTO toDTO(TripPlan entity) {
        TripPlanDTO dto = new TripPlanDTO();
        dto.setId(entity.getId());
        dto.setPlanSpecification(entity.getPlanSpecification());
        dto.setStatus(entity.getStatus().name());
        dto.setUserId(entity.getCreatorId());
        return dto;
    }
    public static TripPlan toEntity(TripPlanDTO dto) {
        TripPlan tripPlan = new TripPlan();
        tripPlan.setPlanSpecification(dto.getPlanSpecification());
        return tripPlan;
    }

    public static List<TripPlanDTO> toDTOs(List<TripPlan> tripPlans) {
        return tripPlans.stream().map(TripPlanConverter::toDTO).toList();
    }
}
