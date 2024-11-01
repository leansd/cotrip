package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import cn.leansd.cotrip.application.plan.TripPlanService;
import cn.leansd.cotrip.domain.cotrip.CoTrip;
import cn.leansd.cotrip.domain.cotrip.CoTripRepository;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.geo.GeoService;
import cn.leansd.site.application.PickupSiteDTO;
import cn.leansd.site.application.PickupSiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static cn.leansd.cotrip.application.TestMap.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoTripMatchingStrategyTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    TripPlanService tripPlanService = Mockito.mock(TripPlanService.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);
    EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
    GeoService geoService = Mockito.mock(GeoService.class);
    PickupSiteService pickupSiteService = Mockito.mock(PickupSiteService.class);


    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);
    LocalDateTime Y2305010831 = LocalDateTime.of(2023, 5, 1, 8, 31);
    LocalDateTime Y2305010900 = LocalDateTime.of(2023, 5, 1, 9, 00);

    CoTripMatchingService coTripMatchingService = null;

    @BeforeEach
    public void setUp(){
        coTripMatchingService = new CoTripMatchingService(tripPlanRepository, coTripRepository, geoService,pickupSiteService);
        when(tripPlanRepository.findById(anyString())).thenReturn(Optional.of(new TripPlan(
                UserId.of("userid"),
                new PlanSpecification(hqAirport,orientalPear, TimeSpan.builder().build())
        )));
        when(pickupSiteService.findNearestPickupSite(any())).thenReturn(new PickupSiteDTO(hqAirport));
    }

    @DisplayName("时间地点完全相同可以匹配")
    @Test
    public void testMatchingWithExactSamePlan() throws InconsistentStatusException {
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        TripPlan existingPlan = new TripPlan(UserId.of("user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan newPlan = new TripPlan(UserId.of("user_id_2"),
                tripPlanDTO.getPlanSpecification());
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));
        verifyCoTripCreatedEventPublished();
    }

    private void verifyCoTripCreatedEventPublished() {
        ArgumentCaptor<CoTrip> coTripArgumentCaptor= ArgumentCaptor.forClass(CoTrip.class);
        verify(coTripRepository).save(coTripArgumentCaptor.capture());
        CoTrip capturedCoTrip = coTripArgumentCaptor.getValue();
        assertNotNull(capturedCoTrip);
        assertEquals(1,capturedCoTrip.domainEvents().size());
    }

    private void verifyNoCoTripCreatedEventPublished() {
        Mockito.verifyNoInteractions(coTripRepository);
    }


    @DisplayName("出行时间不一致则无法匹配")
    @Test
    public void testNoMatchingWithDifferentTimePlan() throws InconsistentStatusException {
        // Given 出行计划A
        PlanSpecification specA = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),1);

        TripPlan existingPlanA = new TripPlan(UserId.of("user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecification specB = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010831)
                        .end(Y2305010900)
                        .build(),1);
        TripPlan newPlanB = new TripPlan(UserId.of("user_id_2"),
                specB);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));
        verifyNoCoTripCreatedEventPublished();
    }

    @DisplayName("座位数不超出限制可以匹配")
    @Test
    public void testMatchingWhenNoExceedMaxSeats() throws InconsistentStatusException {
        // Given 出行计划A
        PlanSpecification specA = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),3);

        TripPlan existingPlanA = new TripPlan(UserId.of("user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecification specB = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build(),1);
        TripPlan newPlanB = new TripPlan(UserId.of("user_id_2"),
                specB);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));
        verifyCoTripCreatedEventPublished();
    }

    @DisplayName("超出座位数限制不可匹配")
    @Test
    public void testNoMatchingWhenExceedMaxSeats() throws InconsistentStatusException {
        // Given 出行计划A
        PlanSpecification specA = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),3);

        TripPlan existingPlanA = new TripPlan(UserId.of("user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecification specB = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build(),2);
        TripPlan newPlanB = new TripPlan(UserId.of("user_id_2"),
                specB);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));

        verifyNoCoTripCreatedEventPublished();
    }

    @DisplayName("起点超出限定距离不可匹配")
    @Test
    public void shouldNotMatchingWhenStartPointDistanceExceedLimitation() throws InconsistentStatusException {
        // Given 出行计划A
        PlanSpecification specA = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),1);

        TripPlan existingPlanA = new TripPlan(UserId.of("user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecification specB = new PlanSpecification(
                hqAirport, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build(),1);
        TripPlan newPlanB = new TripPlan(UserId.of("user_id_2"),
                specB);
        Mockito.when(geoService.getDistance(orientalPear, hqAirport)).thenReturn(21.0);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));
        verifyNoCoTripCreatedEventPublished();
    }

    @DisplayName("起点未超出限定距离可以匹配")
    @Test
    public void shouldMatchingWhenStartPointDistanceExceedLimitation() throws InconsistentStatusException {
        // Given 出行计划A
        PlanSpecification specA = new PlanSpecification(
                orientalPear, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010800)
                        .end(Y2305010830)
                        .build(),1);

        TripPlan existingPlanA = new TripPlan(UserId.of("user_id_1"),
                specA);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlanA));

        // Given 出行计划B
        PlanSpecification specB = new PlanSpecification(
                oceanAquarium, peopleSquare,
                TimeSpan.builder()
                        .start(Y2305010830)
                        .end(Y2305010900)
                        .build(),1);
        TripPlan newPlanB = new TripPlan(UserId.of("user_id_2"),
                specB);
        Mockito.when(geoService.getDistance(orientalPear, oceanAquarium)).thenReturn(0.5);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlanB)));
        verifyCoTripCreatedEventPublished();
    }
}
