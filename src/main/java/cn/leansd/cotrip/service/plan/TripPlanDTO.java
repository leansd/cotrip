package cn.leansd.cotrip.service.plan;

import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanDTO {
    private String id;
    private PlanSpecification planSpecification;
    private String status;
    private String planType;
    private String userId;

    public TripPlanDTO(TripPlan tripPlan) {
        this.id = tripPlan.getId();
        this.planSpecification = tripPlan.getPlanSpecification();
        this.status = tripPlan.getStatus().name();
        this.planType = tripPlan.getPlanType().name();
        this.setUserId(tripPlan.getCreatorId());
    }
}
