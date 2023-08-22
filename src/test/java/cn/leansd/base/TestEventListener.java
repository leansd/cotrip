package cn.leansd.base;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestEventListener {

    private final List<Object> capturedEvents = new ArrayList<>();

    @EventListener
    public void handleEvent(Object event) {
        capturedEvents.add(event);
    }

    public boolean hasReceivedEvent(Class<?> eventType) {
        return capturedEvents.stream()
                .anyMatch(event -> event.getClass().equals(eventType));
    }

    public void clear() {
        capturedEvents.clear();
    }
}
