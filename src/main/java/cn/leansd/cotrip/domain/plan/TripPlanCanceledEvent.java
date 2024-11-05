package cn.leansd.cotrip.domain.plan;

import cn.leansd.base.model.DomainEvent;
import cn.leansd.cotrip.application.plan.TripPlanDTO;

public class TripPlanCanceledEvent extends DomainEvent {
    private TripPlanDTO data;
    public TripPlanCanceledEvent(TripPlanDTO data) {
        this.data = data;
    }
}