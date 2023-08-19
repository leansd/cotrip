package cn.leancoding.cotrip.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanDTO {
    private String id;
    private PlanSpecificationDTO planSpecification;
    private String status;
    public TripPlanDTO(PlanSpecificationDTO planSpecification) {
        this.id = null;
        this.planSpecification = planSpecification;
    }
}
