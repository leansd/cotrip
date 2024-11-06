package cn.leansd.cotrip.application.plan;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.cotrip.types.PlanSpecification;
import cn.leansd.cotrip.types.TripPlanDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static cn.leansd.cotrip.application.TestMap.hqAirport;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@RecordApplicationEvents
class TripPlanServiceTest {
    private static final String urlTripPlan = "/cotrip/plan/v1/trip-plans/";
    TripPlanDTO tripPlanDTO = TripPlanDTO.builder().planSpecification(
                    new PlanSpecification(hqAirport, peopleSquare,
                            TimeSpan.builder()
                                    .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                                    .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                                    .build(),1))
            .planType(TripPlanType.RIDE_SHARING.name())
            .userId("user-id-1")
            .build();

    @Autowired
    private TripPlanService tripPlanService;
    @Autowired
    private TripPlanRepository tripPlanRepository;

    @AfterEach
    void setUp() {
        tripPlanRepository.deleteAll();
    }


    @DisplayName("创建TripPlan应该触发TripPlanCreatedEvent")
    @Test
    void testCreateTripPlan() throws NoVehicleOwnerException {

        TripPlanDTO tripPlan = tripPlanService.createTripPlan(tripPlanDTO);
        verifyTripPlanCreated(tripPlan.getId());
        verifyTripPlanEventPublished(tripPlan.getId());
    }

    private void verifyTripPlanEventPublished(String id) {
        assertThat(applicationEvents.stream(TripPlanCreatedEvent.class))
                .isNotEmpty()
                .hasSize(1)
                .allMatch(event -> event instanceof TripPlanCreatedEvent);
    }



    @Autowired
    private ApplicationEvents applicationEvents; // 注入ApplicationEvents来审查事件

    private void verifyTripPlanCreated(String tripPlanId) {
        Optional<TripPlan> retrievedTripPlan = tripPlanRepository.findById(tripPlanId);
        assertTrue(retrievedTripPlan.isPresent());
        assertEquals(TripPlanStatus.WAITING_MATCH, retrievedTripPlan.get().getStatus());
    }

    @Autowired
    MockMvc mockMvc;
    @DisplayName("查询不存在的TripPlan应该返回404")
    @Test
    void shouldReturn404ErrorWhenTripPlanNotExisted() throws Exception {
        String tripPlanId = "not_existed_trip_plan_id";
        mockMvc.perform(get(urlTripPlan + tripPlanId)
                .header("user-id", "user-id-1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("查询用户的所有TripPlan")
    @Test
    void testRetrieveAllTripPlans() throws NoVehicleOwnerException {
        TripPlanDTO tripPlan = tripPlanService.createTripPlan(tripPlanDTO);
        assertEquals(1, tripPlanService.retrieveTripPlans(UserId.of(tripPlanDTO.getUserId())).size());
    }

    @DisplayName("取消已创建的TripPlan应该返回200")
    @Test
    void testCancelTripPlan() throws Exception {
        TripPlanDTO tripPlan = tripPlanService.createTripPlan(tripPlanDTO);
        mockMvc.perform(delete(urlTripPlan + tripPlan.getId())
                .header("user-id", "user-id-1"))
                .andExpect(status().isOk());
        tripPlanRepository.findById(tripPlan.getId()).ifPresent(tripPlan1 -> assertEquals(TripPlanStatus.CANCELED, tripPlan1.getStatus()));
    }
}
