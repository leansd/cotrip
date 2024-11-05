package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.types.TimeSpan;
import cn.leansd.base.ws.WebSocketConfig;
import cn.leansd.base.ws.WebSocketTestTemplate;
import cn.leansd.cotrip.controller.TripPlanStatusNotificationController;
import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanJoinedEvent;
import cn.leansd.cotrip.domain.plan.TripPlanType;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.application.TestMap.orientalPear;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CoTripMatchingWebSocketIntegrationTest {
    private static final String urlTripPlan = "/cotrip/plan/v1/trip-plans/";
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
        PlanSpecification planSpecification  = new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(), 1);

        TripPlan firstPlan = new TripPlan(TripPlanDTO.builder()
                .planSpecification(planSpecification).userId("user_id_1").planType(TripPlanType.RIDE_SHARING.name()).build());
        TripPlan secondPlan = new TripPlan(TripPlanDTO.builder()
                .planSpecification(planSpecification).userId("user_id_2").planType(TripPlanType.RIDE_SHARING.name()).build());

        ResponseEntity<TripPlanDTO> response_1 = restTemplate.postForEntity(urlTripPlan, new HttpEntity<>(firstPlan, buildHeaderWithUserId("user-id-1")), TripPlanDTO.class);
        assertThat(response_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<TripPlanDTO> response_2 = restTemplate.postForEntity(urlTripPlan, new HttpEntity<>(secondPlan, buildHeaderWithUserId("user-id-2")), TripPlanDTO.class);
        assertThat(response_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态(从WebSocket接口验证)")
    @Test
    public void shouldChangeTripPlanStatusWhenMatchedVerifiedByWebSocket() throws Exception {
        testTemplate.verify(()->{});
    }
}