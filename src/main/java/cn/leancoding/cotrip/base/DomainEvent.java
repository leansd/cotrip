package cn.leancoding.cotrip.base;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public abstract class DomainEvent {
    private String id;
    private final Instant occurredOn;
    public DomainEvent() {
        this.id = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
    }
    public Instant getOccurredOn() {
        return occurredOn;
    }
}

