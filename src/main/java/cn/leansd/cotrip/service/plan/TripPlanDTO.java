package cn.leansd.cotrip.service.plan;

import cn.leansd.cotrip.model.plan.PlanSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
