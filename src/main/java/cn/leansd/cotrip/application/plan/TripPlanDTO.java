package cn.leansd.cotrip.application.plan;

import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlan;
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
