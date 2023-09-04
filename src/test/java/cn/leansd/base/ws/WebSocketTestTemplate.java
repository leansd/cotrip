package cn.leansd.base.ws;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;
public class WebSocketTestTemplate {

    private final String headerUserId;
    private final String endPoint;
    private final String queueName;
    private final String topicName;
    private final Type payloadType;

    private CountDownLatch latch = new CountDownLatch(1);
    private Semaphore clientConnectedSemaphore = new Semaphore(0);
    private AtomicReference<Throwable> failure = new AtomicReference<>();

    private WebSocketStompClient stompClient;
    private WebSocketHttpHeaders headers;
    private SocketSessionClientHandler clientSessionHandler;
    private Consumer<Object> returnedPayloadHandler;

    public WebSocketTestTemplate(String endPoint, String headerUserId,
                                 String queueName, String topicName,
                                 Type payloadType, Consumer<Object> returnedPayloadHandler) throws InterruptedException {
        this.endPoint = endPoint;
        this.headerUserId = headerUserId;
        this.queueName = queueName;
        this.topicName = topicName;
        this.payloadType = payloadType;
        this.returnedPayloadHandler = returnedPayloadHandler;
        buildClientSessionHandler();
    }

    public void execute(Runnable task) throws Exception {
        assert (task != null);
        clientConnectedSemaphore.acquire();
        task.run();
    }

    public void verify(Runnable task) throws InterruptedException {
        if (task != null)
            task.run();
        if (latch.await(30, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
        } else {
            fail("No message received");
        }
    }

    private void buildClientSessionHandler() throws InterruptedException {
        buildStompClient();
        buildHeadersWithUserId();
        this.clientSessionHandler = new SocketSessionClientHandler(latch, failure,
                Arrays.asList(topicName,
                        "/user" + queueName), clientConnectedSemaphore) {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return
                        payloadType;
            }

            @Override
            protected void handlePayload(Object payload) {
                returnedPayloadHandler.accept(payload);
            }
        };

        CompletableFuture<StompSession> connect = stompClient.connectAsync(endPoint, headers, clientSessionHandler);
        connect.join();
    }


    private void buildStompClient() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        stompClient.setMessageConverter(converter);
    }

    private void buildHeadersWithUserId() {
        headers = new WebSocketHttpHeaders();
        headers.add("user-id", this.headerUserId);
    }
}
