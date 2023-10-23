package cn.leansd.cotrip.model.plan;

import cn.leansd.base.model.DomainEvent;
import cn.leansd.cotrip.service.plan.TripPlanDTO;

public class TripPlanCanceledEvent extends DomainEvent {
    private TripPlanDTO data;
    public TripPlanCanceledEvent(TripPlanDTO data) {
        this.data = data;
    }
}
