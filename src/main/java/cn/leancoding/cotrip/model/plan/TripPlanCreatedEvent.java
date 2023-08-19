package cn.leancoding.cotrip.model.plan;


import cn.leancoding.cotrip.base.model.DomainEvent;
import cn.leancoding.cotrip.service.TripPlanDTO;
import lombok.Data;

@Data
public class TripPlanCreatedEvent extends DomainEvent {
    private TripPlanDTO data;
    public TripPlanCreatedEvent(TripPlanDTO data) {
        this.data = data;
    }
}
