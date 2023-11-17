package cn.leansd.cotrip.service.plan;

import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanConverter;
import cn.leansd.cotrip.model.plan.TripPlanCreatedEvent;

public class TripPlanFactory {
    public static TripPlan build(TripPlanDTO planDTO) {
        TripPlan plan = new TripPlan(planDTO);
        plan.registerEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(plan)));
        return plan;
    }
}
