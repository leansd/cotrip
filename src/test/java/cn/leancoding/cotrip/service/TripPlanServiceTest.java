package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.model.GenericId;
import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import cn.leancoding.cotrip.model.plan.TripPlanStatus;
import cn.leancoding.cotrip.model.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static cn.leancoding.cotrip.service.TestMap.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripPlanServiceTest {

    @Autowired
    private TripPlanService tripPlanService;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @MockBean
    private EventPublisher eventPublisher;

    @Test
    public void testCreateTripPlan() {
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecificationDTO(hqAirport, peopleSquare, TimeSpanDTO.builder()
                .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                .build(),1));

        TripPlan tripPlan = tripPlanService.createTripPlan(tripPlanDTO, (UserId) GenericId.of(UserId.class,"user_1"));
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
}
