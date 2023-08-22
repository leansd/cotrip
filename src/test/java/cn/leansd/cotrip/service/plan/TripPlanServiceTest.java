package cn.leansd.cotrip.service.plan;

import cn.leansd.base.model.GenericId;
import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.base.model.UserId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static cn.leansd.cotrip.service.TestMap.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("dev")
public class TripPlanServiceTest {

    @Autowired
    private TripPlanService tripPlanService;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @MockBean
    private EventPublisher eventPublisher;

    @Autowired
    private TestRestTemplate restTemplate;

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
        ArgumentCaptor<TripPlanCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(TripPlanCreatedEvent.class);
        verify(eventPublisher).publishEvent(argumentCaptor.capture());
        TripPlanCreatedEvent capturedEvent = argumentCaptor.getValue();
        assertEquals(tripPlanId, capturedEvent.getData().getId());
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

        assertEquals(404, response.getStatusCodeValue());
    }
}
