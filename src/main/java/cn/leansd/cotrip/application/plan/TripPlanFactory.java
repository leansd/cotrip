package cn.leansd.cotrip.application.plan;

import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanConverter;
import cn.leansd.cotrip.domain.plan.TripPlanCreatedEvent;

public class TripPlanFactory {
    public static TripPlan build(TripPlanDTO planDTO) {
        TripPlan plan = new TripPlan(planDTO);
        plan.registerEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(plan)));
        return plan;
    }
}
