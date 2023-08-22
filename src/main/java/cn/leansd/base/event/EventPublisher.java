package cn.leansd.base.event;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.DomainEvent;

public interface EventPublisher {
    void publishEvent(AggregateRoot object, DomainEvent event);
}
