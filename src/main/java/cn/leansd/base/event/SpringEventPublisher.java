package cn.leansd.base.event;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishEvent(AggregateRoot object, DomainEvent event) {
        //object.registerDomainEvent(event);
    }
}
