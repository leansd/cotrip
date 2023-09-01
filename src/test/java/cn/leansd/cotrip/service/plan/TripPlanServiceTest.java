package cn.leansd.cotrip.service.plan;

import cn.leansd.base.TestEventListener;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.plan.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static cn.leansd.base.RestTemplateUtil.buildHeaderWithUserId;
import static cn.leansd.cotrip.service.TestMap.hqAirport;
import static cn.leansd.cotrip.service.TestMap.peopleSquare;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class TripPlanServiceTest {
    @Autowired
    private TripPlanService tripPlanService;
    @Autowired
    private TripPlanRepository tripPlanRepository;
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

    @Autowired
    MockMvc mockMvc;
    @DisplayName("查询不存在的TripPlan应该返回404")
    @Test
    public void shouldReturn404ErrorWhenTripPlanNotExisted() throws Exception {
        String tripPlanId = "not_existed_trip_plan_id";
        mockMvc.perform(get("/trip-plan/" + tripPlanId)
                .header("user-id", "user-id-1"))
                .andExpect(status().isNotFound());
    }
}
