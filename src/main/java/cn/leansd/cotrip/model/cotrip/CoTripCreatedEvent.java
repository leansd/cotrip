package cn.leansd.cotrip.model.cotrip;


import cn.leansd.base.model.DomainEvent;
import lombok.Data;

@Data
public class CoTripCreatedEvent extends DomainEvent {
    private String coTripId;
    public CoTripCreatedEvent(String coTripId) {
        this.coTripId = coTripId;
    }
}
