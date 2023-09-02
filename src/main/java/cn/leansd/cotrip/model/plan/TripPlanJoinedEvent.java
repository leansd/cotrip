package cn.leansd.cotrip.model.plan;


import cn.leansd.base.model.DomainEvent;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripPlanJoinedEvent extends DomainEvent {
    private TripPlanDTO data;
    public TripPlanJoinedEvent(TripPlanDTO data) {
        this.data = data;
    }
}
