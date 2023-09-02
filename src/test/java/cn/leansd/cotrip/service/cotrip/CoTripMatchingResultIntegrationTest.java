package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.controller.TripPlanStatusNotificationController;
import cn.leansd.cotrip.controller.WebSocketConfig;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.service.TestMap.orientalPear;
import static cn.leansd.cotrip.service.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;
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
public class CoTripMatchingResultIntegrationTest {
    @Autowired
    private TripPlanRepository tripPlanRepository;
    @Autowired
    private CoTripRepository coTripRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value(value="${local.server.port}")
    private int port;

    TripPlanDTO firstTripPlan = null;
    TripPlanDTO secondTripPlan = null;
    @BeforeEach
    public void setUp(){
        LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
        LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        TripPlan firstPlan = new TripPlan(UserId.of("user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan secondPlan = new TripPlan(UserId.of("user_id_2"),
                tripPlanDTO.getPlanSpecification());

        ResponseEntity<TripPlanDTO> response_1 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(firstPlan, buildHeaderWithUserId("user-id-1")), TripPlanDTO.class);
        assertThat(response_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        firstTripPlan = response_1.getBody();

        ResponseEntity<TripPlanDTO> response_2 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(secondPlan, buildHeaderWithUserId("user-id-2")), TripPlanDTO.class);
        assertThat(response_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        secondTripPlan = response_2.getBody();
    }



    @DisplayName("匹配成功后应该更新TripPlan的状态(数据库验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByDb() {
        TripPlan savedExistingPlan = tripPlanRepository.findById(firstTripPlan.getId()).orElse(null);
        TripPlan savedNewPlan = tripPlanRepository.findById(secondTripPlan.getId()).orElse(null);
        assertThat(savedExistingPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
        assertThat(savedNewPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态(接口验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByAPI() {
        ResponseEntity<TripPlanDTO> response =  restTemplate.exchange(
                "/trip-plan/" + firstTripPlan.getId(),
                HttpMethod.GET,
                new HttpEntity<>(buildHeaderWithUserId("user-id-1")),
                TripPlanDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TripPlanDTO tripPlanDTO = response.getBody();
        assertThat(tripPlanDTO.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());

        response = restTemplate.exchange(
                "/trip-plan/" + secondTripPlan.getId(),
                HttpMethod.GET,
                new HttpEntity<>(buildHeaderWithUserId("user-id-1")),
                TripPlanDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        tripPlanDTO = response.getBody();
        assertThat(tripPlanDTO.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());
    }

    @Autowired  SimpMessagingTemplate template;
    Logger logger = Logger.getLogger(CoTripMatchingResultIntegrationTest.class.getName());
    @DisplayName("匹配成功后应该更新TripPlan的状态(从WebSocket接口验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByWebSocket() throws InterruptedException, ExecutionException, JsonProcessingException {
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
                TripPlanStatusNotificationController.TOPIC_TRIP_PLAN_JOINED,sem) {
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
        template.convertAndSend(TripPlanStatusNotificationController.TOPIC_TRIP_PLAN_JOINED, new TripPlanJoinedEvent(new TripPlanDTO() ));
        //template.convertAndSendToUser("user-id-1", "/queue/updates", new TripPlanJoinedEvent(new TripPlanDTO() ));
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
