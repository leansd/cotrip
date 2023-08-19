package cn.leancoding.cotrip.model.plan;


import cn.leancoding.cotrip.model.location.LocationConverter;
import cn.leancoding.cotrip.service.TimeSpanDTO;
import cn.leancoding.cotrip.service.TripPlanDTO;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.parameters.P;

public class TripPlanConverter {
    public static TripPlanDTO toDTO(TripPlan entity) {
        TripPlanDTO dto = new TripPlanDTO();
        dto.setId(entity.getId());
        dto.setPlanSpecification(PlanSpecificationConverter.toDTO(entity.getPlanSpecification()));
        dto.setStatus(entity.getStatus().name());
        return dto;
    }
    public static TripPlan toEntity(TripPlanDTO dto) {
        TripPlan tripPlan = new TripPlan();
        tripPlan.setPlanSpecification(PlanSpecificationConverter.toEntity(dto.getPlanSpecification()));
        return tripPlan;
    }
}
