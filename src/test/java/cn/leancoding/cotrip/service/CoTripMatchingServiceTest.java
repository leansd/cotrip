package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.event.EventPublisher;
import cn.leancoding.cotrip.base.model.GenericId;
import cn.leancoding.cotrip.model.cotrip.CoTripCreatedEvent;
import cn.leancoding.cotrip.model.cotrip.CoTripRepository;
import cn.leancoding.cotrip.model.plan.*;
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
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecificationDTO(orientalPear, peopleSquare, TimeSpanDTO.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        TripPlan existingPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                PlanSpecificationConverter.toEntity(tripPlanDTO.getPlanSpecification()));
        TripPlan newPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                PlanSpecificationConverter.toEntity(tripPlanDTO.getPlanSpecification()));
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
        when(tripPlanRepository.findById(newPlan.getId())).thenReturn(Optional.of(newPlan));

        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));
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
        PlanSpecificationDTO specA = new PlanSpecificationDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),1);

        TripPlan existingPlanA = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                PlanSpecificationConverter.toEntity(specA));
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecificationDTO specB = new PlanSpecificationDTO(
                orientalPear, peopleSquare,
                TimeSpanDTO.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build(),1);
        TripPlan newPlanB = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                PlanSpecificationConverter.toEntity(specB));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));

        // Then 出行计划A和B无法匹配成共乘
        verify(eventPublisher, times(0)).publishEvent(any(CoTripCreatedEvent.class));
    }

}
