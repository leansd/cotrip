package cn.leansd.cotrip.application.plan;

import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanConverter;
import cn.leansd.cotrip.domain.plan.TripPlanCreatedEvent;

public class TripPlanFactory {
    public static TripPlan build(UserId creatorId, PlanSpecification spec) {
        TripPlan plan = new TripPlan(creatorId, spec);
        plan.registerEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(plan)));
        return plan;
    }
}
