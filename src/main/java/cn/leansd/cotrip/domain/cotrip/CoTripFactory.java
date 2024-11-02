package cn.leansd.cotrip.domain.cotrip;

import java.util.List;

public class CoTripFactory {
    private CoTripFactory(){}
    public static CoTrip build(List<String> tripPlanIds) {
        CoTrip coTrip = new CoTrip(tripPlanIds);
        coTrip.registerEvent(new CoTripCreatedEvent(coTrip.getId()));
        return coTrip;
    }
}
