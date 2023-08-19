package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.model.location.LocationConverter;
import cn.leancoding.cotrip.service.PlanSpecificationDTO;

public class PlanSpecificationConverter {
    public static PlanSpecificationDTO toDTO(PlanSpecification entity) {
        PlanSpecificationDTO dto = new PlanSpecificationDTO();
        dto.setArrivalLocation(LocationConverter.toDTO(entity.getArrivalLocation()));
        dto.setDepartureLocation(LocationConverter.toDTO(entity.getDepartureLocation()));
        dto.setPlannedDepartureTime(TimeSpanConverter.toDTO(entity.getPlannedDepartureTime()));
        dto.setRequiredSeats(entity.getRequiredSeats());
        return dto;
    }
    public static PlanSpecification toEntity(PlanSpecificationDTO dto) {
        PlanSpecification planSpecification = new PlanSpecification();
        planSpecification.setArrivalLocation(LocationConverter.toEntity(dto.getArrivalLocation()));
        planSpecification.setDepartureLocation(LocationConverter.toEntity(dto.getDepartureLocation()));
        planSpecification.setPlannedDepartureTime(TimeSpanConverter.toEntity(dto.getPlannedDepartureTime()));
        planSpecification.setRequiredSeats(dto.getRequiredSeats());
        return planSpecification;
    }
}
