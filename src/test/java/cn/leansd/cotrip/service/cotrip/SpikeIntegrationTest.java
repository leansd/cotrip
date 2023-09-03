package cn.leansd.cotrip.service.cotrip;

import cn.leansd.cotrip.controller.TripPlanStatusNotificationController;
import cn.leansd.cotrip.controller.WebSocketConfig;
import cn.leansd.cotrip.model.plan.TripPlanJoinedEvent;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 本测试的能力和CoTripMatchingResultMvcTest是一样的，
 * 但是本测试是通过实际的Http请求验证，MvcTest是仿制的Http服务器
 * 本测试仅是演示目的。
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class SpikeIntegrationTest {
    @Value(value="${local.server.port}")
    private int port;

    @BeforeEach
    public void setUp(){
    }

    @Autowired  SimpMessagingTemplate template;
    Logger logger = Logger.getLogger(SpikeIntegrationTest.class.getName());
    @DisplayName("验证WebSocket发送给单一用户通知的能力")
    @Test
    public void testWebSocketSendToSingleUser() throws Exception {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        stompClient.setMessageConverter(converter);

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("user-id", "user-id-1");

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        Semaphore sem = new Semaphore(0);

        StompSessionHandler handler = new SocketSessionHandler(latch, failure,
                Arrays.asList(TripPlanStatusNotificationController.BROADCAST_UPDATE_TOPIC,
                "/user" +TripPlanStatusNotificationController.UPDATE_QUEUE),sem) {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return TripPlanJoinedEvent.class;
            }

            @Override
            protected void handlePayload(Object payload) {
               TripPlanJoinedEvent event = (TripPlanJoinedEvent) payload;
               assertNotNull(event);
           }
        };
        CompletableFuture<StompSession> connect = stompClient.connectAsync("ws://localhost:{port}"+WebSocketConfig.WS_ENDPOINT, headers, handler, this.port);
        connect.join();
        sem.acquire(); //看起来这个是必须的，否则下面的send有时候会失败。需要进一步求证一下原因。
        logger.info("send message");
        template.convertAndSendToUser("user-id-1", TripPlanStatusNotificationController.UPDATE_QUEUE,
                new TripPlanJoinedEvent(new TripPlanDTO() ));
        if (latch.await(6, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                throw new AssertionError("", failure.get());
            }
        }
        else {
            fail("No message received");
        }
    }

}
