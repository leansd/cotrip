package cn.leansd.cotrip.service.plan;

import cn.leansd.base.TestEventListener;
import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTrip;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.base.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Optional;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static cn.leansd.cotrip.service.TestMap.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class TripPlanServiceTest {
    @Autowired
    private TripPlanService tripPlanService;
    @Autowired
    private TripPlanRepository tripPlanRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestEventListener listener;

    @BeforeEach
    public void setUp(){
        listener.clear();
    }

    @Test
    public void testCreateTripPlan() {
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(hqAirport, peopleSquare, TimeSpan.builder()
                .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                .build(),1));
        TripPlanDTO tripPlan = tripPlanService.createTripPlan(tripPlanDTO, UserId.of("user_1"));
        verifyTripPlanCreated(tripPlan.getId());
        verifyTripPlanEventPublished(tripPlan.getId());
    }

    private void verifyTripPlanCreated(String tripPlanId) {
        Optional<TripPlan> retrievedTripPlan = tripPlanRepository.findById(tripPlanId);
        assertTrue(retrievedTripPlan.isPresent());
        assertEquals(TripPlanStatus.WAITING_MATCH, retrievedTripPlan.get().getStatus());
    }

    private void verifyTripPlanEventPublished(String tripPlanId) {
        assertTrue(listener.hasReceivedEvent(TripPlanCreatedEvent.class));
    }

    @DisplayName("查询不存在的TripPlan应该返回404")
    @Test
    public void shouldReturn404ErrorWhenTripPlanNotExisted() {
        String tripPlanId = "not_existed_trip_plan_id";
        ResponseEntity<TripPlanDTO> response = restTemplate.exchange(
                "/trip-plan/" + tripPlanId,
                HttpMethod.GET,
                new HttpEntity<>(buildHeaderWithUserId("user-id-1")),
                TripPlanDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
