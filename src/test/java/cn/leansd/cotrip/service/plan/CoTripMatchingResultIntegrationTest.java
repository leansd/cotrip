package cn.leansd.cotrip.service.plan;

import cn.leansd.base.model.GenericId;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTrip;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.cotrip.CoTripStatus;
import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.model.plan.TripPlanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static cn.leansd.base.session.HttpTest.USER_ID_HEADER;
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
        TripPlan firstPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan secondPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                tripPlanDTO.getPlanSpecification());

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, "user-id-1");
        ResponseEntity<TripPlanDTO> response_1 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(firstPlan, headers), TripPlanDTO.class);
        assertThat(response_1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        firstTripPlan = response_1.getBody();

        headers.set(USER_ID_HEADER, "user-id-2");
        ResponseEntity<TripPlanDTO> response_2 = restTemplate.postForEntity("/trip-plan", new HttpEntity<>(secondPlan, headers), TripPlanDTO.class);
        assertThat(response_2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        secondTripPlan = response_2.getBody();
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态")
    @Test
    public void shouldChangeTripPlanStatusWhenMatched() {
        TripPlan savedExistingPlan = tripPlanRepository.findById(firstTripPlan.getId()).orElse(null);
        TripPlan savedNewPlan = tripPlanRepository.findById(secondTripPlan.getId()).orElse(null);
        assertThat(savedExistingPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
        assertThat(savedNewPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
    }

    @DisplayName("匹配成功后应该创建CoTrip，状态应该是CREATED")
    @Test
    public void shouldCreateCoTripWhenMatched() {
        CoTrip savedCoTrip = coTripRepository.findFirstByOrderByCreatedAtDesc();
        assertThat(savedCoTrip).isNotNull();
        assertThat(savedCoTrip.getStatus()).isEqualTo(CoTripStatus.CREATED);
    }
}
