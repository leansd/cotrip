package cn.leancoding.cotrip.base.model;

import lombok.Data;

import java.time.Instant;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEvent that = (DomainEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

