package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.base.ws.WebSocketConfig;
import cn.leansd.base.ws.WebSocketTestTemplate;
import cn.leansd.cotrip.controller.TripPlanStatusNotificationController;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.service.TestMap.orientalPear;
import static cn.leansd.cotrip.service.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 本测试的能力和CoTripMatchingResultMvcTest是一样的，
 * 但是本测试是通过实际的Http请求验证，MvcTest是仿制的Http服务器
 * 本测试仅是演示目的。
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class CoTripMatchingWebServiceIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Value(value="${local.server.port}")
    private int port;
    WebSocketTestTemplate testTemplate = null;

    @BeforeEach
    public void setUp() throws InterruptedException {
        testTemplate = new WebSocketTestTemplate("ws://localhost:" + this.port + WebSocketConfig.WS_ENDPOINT,
                "user-id-1",
                TripPlanStatusNotificationController.UPDATE_QUEUE,
                TripPlanStatusNotificationController.BROADCAST_UPDATE_TOPIC,
                TripPlanJoinedEvent.class,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object payload) {
                        assertNotNull(payload);
                    }
                });

        LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
        LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(), 1));
        TripPlan firstPlan = new TripPlan(UserId.of("user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan secondPlan = new TripPlan(UserId.of("user_id_2"),
                tripPlanDTO.getPlanSpecification());

        ResponseEntity<TripPlanDTO> response_1 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(firstPlan, buildHeaderWithUserId("user-id-1")), TripPlanDTO.class);
        assertThat(response_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<TripPlanDTO> response_2 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(secondPlan, buildHeaderWithUserId("user-id-2")), TripPlanDTO.class);
        assertThat(response_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态(从WebSocket接口验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByWebSocket() throws Exception {
        testTemplate.verify(()->{});
    }
}