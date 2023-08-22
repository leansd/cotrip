package cn.leansd.cotrip.service.plan;

import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanConverter;
import cn.leansd.cotrip.model.plan.TripPlanCreatedEvent;

public class TripPlanFactory {
    public static TripPlan build(UserId creatorId, PlanSpecification spec) {
        TripPlan plan = new TripPlan(creatorId, spec);
        plan.registerEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(plan)));
        return plan;
    }
}
