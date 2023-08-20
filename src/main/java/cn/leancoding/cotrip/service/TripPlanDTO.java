package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.model.plan.PlanSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanDTO {
    private String id;
    private PlanSpecification planSpecification;
    private String status;
    public TripPlanDTO(PlanSpecification planSpecification) {
        this.id = null;
        this.planSpecification = planSpecification;
    }
}
