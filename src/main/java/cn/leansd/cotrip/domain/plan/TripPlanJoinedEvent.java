package cn.leansd.cotrip.domain.plan;


import cn.leansd.base.model.DomainEvent;
import cn.leansd.cotrip.types.TripPlanDTO;
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
