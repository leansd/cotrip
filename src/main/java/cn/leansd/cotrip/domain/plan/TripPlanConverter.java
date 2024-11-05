package cn.leansd.cotrip.domain.plan;


import cn.leansd.cotrip.application.plan.TripPlanDTO;

import java.util.List;

public class TripPlanConverter {
    public static TripPlanDTO toDTO(TripPlan entity) {
        TripPlanDTO dto = new TripPlanDTO();
        dto.setId(entity.getId());
        dto.setPlanSpecification(entity.getPlanSpecification());
        dto.setStatus(entity.getStatus().name());
        dto.setPlanType(entity.getPlanType().name());
        dto.setUserId(entity.getCreatorId());
        return dto;
    }
    public static List<TripPlanDTO> toDTOs(List<TripPlan> tripPlans) {
        return tripPlans.stream().map(TripPlanConverter::toDTO).collect(java.util.stream.Collectors.toList());
    }
}
