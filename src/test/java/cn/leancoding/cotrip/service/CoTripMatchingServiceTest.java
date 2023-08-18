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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static cn.leancoding.cotrip.service.TestMap.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CoTripMatchingServiceTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);
    EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);

    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
    LocalDateTime Y2305010900 = LocalDateTime.of(2023, 5, 1, 9, 00);

    @DisplayName("时间地点完全相同可以匹配")
    @Test
    public void testMatchingWithExactSamePlan() throws InconsistentStatusException {
        CoTripMatchingService coTripMatchingService = new CoTripMatchingService(tripPlanRepository, coTripRepository, eventPublisher);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(orientalPear, peopleSquare, TimeSpanDTO.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build());
        PlanSpecification spec = new PlanSpecification(tripPlanDTO);
        TripPlan existingPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                spec);
        TripPlan newPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                spec);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
        when(tripPlanRepository.findById(newPlan.getId())).thenReturn(Optional.of(newPlan));

        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(newPlan.getId()));
        verifyCoTripCreatedEventPublished();
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
        CoTripMatchingService coTripMatchingService = new CoTripMatchingService(tripPlanRepository, coTripRepository, eventPublisher);
        // Given 出行计划A
        TripPlanDTO tripPlanA = new TripPlanDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build());
        PlanSpecification specA = new PlanSpecification(tripPlanA);
        TripPlan existingPlanA = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        TripPlanDTO tripPlanB = new TripPlanDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build());
        PlanSpecification specB = new PlanSpecification(tripPlanB);
        TripPlan newPlanB = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                specB);
        when(tripPlanRepository.findById(newPlanB.getId())).thenReturn(Optional.of(newPlanB));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(newPlanB.getId()));

        // Then 出行计划A和B无法匹配成共乘
        verify(eventPublisher, times(0)).publishEvent(any(CoTripCreatedEvent.class));
    }

}
