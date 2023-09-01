package cn.leansd.base.model;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
public abstract class AggregateRoot extends DomainEntity {
    @Transient
    private final transient List<Object> domainEvents = new ArrayList();

    public AggregateRoot() {
    }

    public void registerEvent(DomainEvent event) {
        Assert.notNull(event, "Domain event must not be null");
        this.domainEvents.add(event);
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    public Collection<Object> domainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }
}
