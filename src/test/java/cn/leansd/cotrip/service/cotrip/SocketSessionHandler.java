package cn.leansd.cotrip.service.cotrip;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class SocketSessionHandler implements StompSessionHandler {
    private final AtomicReference<Throwable> failure;
    private final CountDownLatch latch;
    private final String topic;
    private final Semaphore semaphore;
    Logger logger = Logger.getLogger(SocketSessionHandler.class.getName());
    public SocketSessionHandler(CountDownLatch latch, AtomicReference<Throwable> failure, String topic, Semaphore sem) {
        this.latch = latch;
        this.failure = failure;
        this.topic = topic;
        this.semaphore = sem;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(topic, this);
        logger.info("Subscribed to " + topic);
        semaphore.release();
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
