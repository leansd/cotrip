package cn.leansd.base.ws;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class SocketSessionClientHandler implements StompSessionHandler {
    private final AtomicReference<Throwable> failure;
    private final CountDownLatch latch;
    private final List<String> topics;
    private final Semaphore connectedSemaphore;
    protected Object payload;
    Logger logger = Logger.getLogger(SocketSessionClientHandler.class.getName());
    public SocketSessionClientHandler(CountDownLatch latch, AtomicReference<Throwable> failure, List<String> topics, Semaphore sem) {
        this.latch = latch;
        this.failure = failure;
        this.topics = topics;
        this.connectedSemaphore = sem;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        topics.forEach(topic->{
            session.subscribe(topic, this);
            logger.info("Subscribed to " + topic);
        });
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            connectedSemaphore.release();
            System.out.println("Semaphore released after 2 seconds!");
        }, 2, TimeUnit.SECONDS);
    }

    @Override
    public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
        this.failure.set(ex);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable ex) {
        this.failure.set(ex);
    }

    protected abstract void handlePayload(Object payload);

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        try {
            handlePayload(payload);
        } catch (Throwable t) {
            failure.set(t);
        } finally {
            latch.countDown();
        }
    }
}
