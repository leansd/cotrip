package cn.leancoding.cotrip.model.plan;


import cn.leancoding.cotrip.base.DomainEvent;
import lombok.Data;

@Data
public class TripPlanCreatedEvent extends DomainEvent {
    private String tripPlanId;
    public TripPlanCreatedEvent(String tripPlanId) {
        this.tripPlanId = tripPlanId;
    }
}
