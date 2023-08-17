package cn.leancoding.cotrip.model.cotrip;


import cn.leancoding.cotrip.base.model.DomainEvent;
import lombok.Data;

@Data
public class CoTripCreatedEvent extends DomainEvent {
    private String coTripId;
    public CoTripCreatedEvent(String coTripId) {
        this.coTripId = coTripId;
    }
}
