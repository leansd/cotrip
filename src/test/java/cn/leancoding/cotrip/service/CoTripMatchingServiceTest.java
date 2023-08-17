package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.base.model.GenericId;
import cn.leancoding.cotrip.model.cotrip.CoTripCreatedEvent;
import cn.leancoding.cotrip.model.cotrip.CoTripRepository;
import cn.leancoding.cotrip.model.plan.PlanSpecification;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import cn.leancoding.cotrip.model.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static cn.leancoding.cotrip.service.TestMap.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CoTripMatchingServiceTest {
    @Autowired TripPlanRepository tripPlanRepository;
    @Autowired CoTripMatchingService coTripMatchingService;
    @Autowired CoTripRepository coTripRepository;
    @MockBean EventPublisher eventPublisher;

    @DisplayName("时间地点完全相同可以匹配")
    @Test
    public void testMatchingWithExactSamePlan() throws InconsistentStatusException {
        TripPlanDTO tripPlanDTO = new TripPlanDTO(orientalPear, peopleSquare, TimeSpanDTO.builder()
                .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                .build());

        PlanSpecification spec = new PlanSpecification(tripPlanDTO);
        TripPlan existingPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                spec);
        tripPlanRepository.save(existingPlan);

        TripPlan newPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                spec);
        tripPlanRepository.save(newPlan);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(newPlan.getId()));

        assertEquals(1,coTripRepository.count());

    }

    private void verifyCoTripCreatedEventPublished() {
        ArgumentCaptor<CoTripCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(CoTripCreatedEvent.class);
        verify(eventPublisher).publishEvent(argumentCaptor.capture());
        CoTripCreatedEvent capturedEvent = argumentCaptor.getValue();
        assertNotNull(capturedEvent);
    }

    private void verifyNoCoTripCreatedEventPublished() {
        ArgumentCaptor<CoTripCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(CoTripCreatedEvent.class);
        verify(eventPublisher).publishEvent(argumentCaptor.capture());
        CoTripCreatedEvent capturedEvent = argumentCaptor.getValue();
        assertNotNull(capturedEvent);
    }


    @DisplayName("出行时间不一致则无法匹配")
    @Test
    public void testNoMatchingWithDifferentTimePlan() throws InconsistentStatusException {
        // Given 出行计划A
        TripPlanDTO tripPlanA = new TripPlanDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(LocalDateTime.of(2023, 5, 1, 8, 0)) // 08:00
                        .end(LocalDateTime.of(2023, 5, 1, 9, 0)) // 09:00
                        .build());
        PlanSpecification specA = new PlanSpecification(tripPlanA);
        TripPlan existingPlanA = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                specA);
        tripPlanRepository.save(existingPlanA);

        // Given 出行计划B
        TripPlanDTO tripPlanB = new TripPlanDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(LocalDateTime.of(2023, 5, 1, 9, 0)) // 09:00
                        .end(LocalDateTime.of(2023, 5, 1, 10, 0)) // 10:00
                        .build());
        PlanSpecification specB = new PlanSpecification(tripPlanB);
        TripPlan newPlanB = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                specB);

        // When 创建出行计划B
        tripPlanRepository.save(newPlanB);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(newPlanB.getId()));

        // Then 出行计划A和B无法匹配成共乘
        verify(eventPublisher, times(0)).publishEvent(any(CoTripCreatedEvent.class));
    }

}
