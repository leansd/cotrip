package cn.leansd.cotrip.model.cotrip;

import cn.leansd.cotrip.model.plan.TripPlan;

import java.util.ArrayList;
import java.util.List;

public class CoTripFactory {
    public static CoTrip build(List<TripPlan> tripPlans) {
        CoTrip coTrip = new CoTrip(new ArrayList<>(
                tripPlans.stream().map(TripPlan::getId).toList()));
        coTrip.registerEvent(new CoTripCreatedEvent(coTrip.getId()));
        return coTrip;
    }
}
