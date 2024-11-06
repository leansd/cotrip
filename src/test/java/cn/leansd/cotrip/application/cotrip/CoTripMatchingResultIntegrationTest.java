package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.types.TimeSpan;
import cn.leansd.base.ws.WebSocketTestTemplate;
import cn.leansd.cotrip.types.PlanSpecification;
import cn.leansd.cotrip.types.TripPlanDTO;
import cn.leansd.cotrip.domain.cotrip.CoTripRepository;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanStatus;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.application.TestMap.orientalPear;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 本测试的能力和CoTripMatchingResultMvcTest是一样的，
 * 但是本测试是通过实际的Http请求验证，MvcTest是仿制的Http服务器
 * 本测试仅是演示目的。
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class CoTripMatchingResultIntegrationTest {
    private static final String urlTripPlan = "/cotrip/plan/v1/trip-plans/";

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
    WebSocketTestTemplate testTemplate = null;


    @BeforeEach
    void setUp() throws InterruptedException {

        LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
        LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
        PlanSpecification planSpec = new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(), 1);
        TripPlan firstPlan = new TripPlan(TripPlanDTO.builder().planSpecification(planSpec)
                .planType(TripPlanType.RIDE_SHARING.name()).userId("user_id_1").build());
        TripPlan secondPlan = new TripPlan(TripPlanDTO.builder().planSpecification(planSpec)
                .planType(TripPlanType.RIDE_SHARING.name()).userId("user_id_2").build());

        ResponseEntity<TripPlanDTO> response_1 = restTemplate.postForEntity(urlTripPlan, new HttpEntity<>(firstPlan, buildHeaderWithUserId("user-id-1")), TripPlanDTO.class);
        assertThat(response_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        firstTripPlan = response_1.getBody();

        ResponseEntity<TripPlanDTO> response_2 = restTemplate.postForEntity(urlTripPlan, new HttpEntity<>(secondPlan, buildHeaderWithUserId("user-id-2")), TripPlanDTO.class);
        assertThat(response_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        secondTripPlan = response_2.getBody();


    }



    @DisplayName("匹配成功后应该更新TripPlan的状态(数据库验证)")
    @Test
    void shouldChangeTripPlanStatusWhenMatchedVerifiedByDb() {
        TripPlan savedExistingPlan = tripPlanRepository.findById(firstTripPlan.getId()).orElse(null);
        TripPlan savedNewPlan = tripPlanRepository.findById(secondTripPlan.getId()).orElse(null);
        assertThat(savedExistingPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
        assertThat(savedNewPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态(接口验证)")
    @Test
    void shouldChangeTripPlanStatusWhenMatchedVerifiedByAPI() {
        ResponseEntity<TripPlanDTO> response =  restTemplate.exchange(
                urlTripPlan + firstTripPlan.getId(),
                HttpMethod.GET,
                new HttpEntity<>(buildHeaderWithUserId("user-id-1")),
                TripPlanDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TripPlanDTO tripPlanDTO = response.getBody();
        assertThat(tripPlanDTO.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());

        response = restTemplate.exchange(
                urlTripPlan + secondTripPlan.getId(),
                HttpMethod.GET,
                new HttpEntity<>(buildHeaderWithUserId("user-id-1")),
                TripPlanDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        tripPlanDTO = response.getBody();
        assertThat(tripPlanDTO.getStatus()).isEqualTo(TripPlanStatus.JOINED.toString());
    }

}
