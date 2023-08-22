package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.model.plan.TripPlanStatus;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpEntity;

import java.time.LocalDateTime;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.service.TestMap.orientalPear;
import static cn.leansd.cotrip.service.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

}
