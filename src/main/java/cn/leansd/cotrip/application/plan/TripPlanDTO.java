package cn.leansd.cotrip.application.plan;

import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlan;
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
    private String userId;
    public TripPlanDTO(PlanSpecification planSpecification) {
        this.id = null;
        this.planSpecification = planSpecification;
    }

    public TripPlanDTO(TripPlan tripPlan) {
        this.id = tripPlan.getId();
        this.planSpecification = tripPlan.getPlanSpecification();
        this.status = tripPlan.getStatus().name();
        this.setUserId(tripPlan.getCreatorId());
    }
}
