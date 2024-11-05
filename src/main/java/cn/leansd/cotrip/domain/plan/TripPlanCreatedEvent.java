package cn.leansd.cotrip.domain.plan;


import cn.leansd.base.model.DomainEvent;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import lombok.Data;

@Data
public class TripPlanCreatedEvent extends DomainEvent {
    private TripPlanDTO data;
    public TripPlanCreatedEvent(TripPlanDTO data) {
        this.data = data;
    }
}
