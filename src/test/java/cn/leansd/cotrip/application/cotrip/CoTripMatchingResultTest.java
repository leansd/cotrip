package cn.leansd.cotrip.application.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.domain.cotrip.CoTrip;
import cn.leansd.cotrip.domain.cotrip.CoTripRepository;
import cn.leansd.cotrip.domain.cotrip.CoTripStatus;
import cn.leansd.cotrip.domain.plan.*;
import cn.leansd.cotrip.application.plan.TripPlanService;
import cn.leansd.geo.GeoService;
import cn.leansd.site.service.PickupSiteDTO;
import cn.leansd.site.service.PickupSiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.leansd.cotrip.application.TestMap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoTripMatchingResultTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    TripPlanService tripPlanService = Mockito.mock(TripPlanService.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);
    GeoService geoService = Mockito.mock(GeoService.class);
    PickupSiteService pickupSiteService = Mockito.mock(PickupSiteService.class);

    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);

    CoTripMatchingService coTripMatchingService = null;
    TripPlan existingPlan  = null;
    TripPlan newPlan = null;
    @BeforeEach
    public void setUp(){
        coTripMatchingService = new CoTripMatchingService(tripPlanRepository, coTripRepository, geoService,pickupSiteService);
        PlanSpecification planSpec = new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(), 1);
        existingPlan = new TripPlan(UserId.of("user_id_1"),
                planSpec,TripPlanType.RIDE_SHARING);
        newPlan = new TripPlan(UserId.of("user_id_2"),
                planSpec,TripPlanType.RIDE_SHARING);
        when(tripPlanRepository.findAllMatchCandidates(any())).thenReturn(Arrays.asList(existingPlan));
        when(pickupSiteService.findNearestPickupSite(any())).thenReturn(new PickupSiteDTO(hqAirport));
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态")
    @Test
    public void shouldChangeTripPlanStatusWhenMatched() throws InconsistentStatusException {
        when(tripPlanRepository.findById(eq(existingPlan.getId()))).thenReturn(Optional.of(existingPlan));
        when(tripPlanRepository.findById(eq(newPlan.getId()))).thenReturn(Optional.of(newPlan));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<List<TripPlan>> tripPlanListCaptor= ArgumentCaptor.forClass(List.class);
        verify(tripPlanRepository).saveAll(tripPlanListCaptor.capture());
        List<TripPlan> capturedTripPlan = tripPlanListCaptor.getValue();
        assertThat(capturedTripPlan.stream().map(tripPlan->tripPlan.getId()).collect(Collectors.toList())).
                containsExactlyInAnyOrderElementsOf(
                Arrays.asList(
                        existingPlan.getId(),
                        newPlan.getId()
        ));
        capturedTripPlan.forEach(tripPlan -> {
            assertThat(tripPlan.getStatus()).isEqualTo(TripPlanStatus.JOINED);
        });
        capturedTripPlan.forEach(tripPlan -> {
            assertThat(tripPlan.getCoTripId()).isNotNull();
        });
    }

    @DisplayName("匹配成功后应该创建CoTrip，状态应该是CREATED")
    @Test
    public void shouldCreateCoTripWhenMatched() throws InconsistentStatusException {
        when(tripPlanRepository.findById(anyString())).thenReturn(Optional.of(new TripPlan(
                UserId.of("userid"),
                new PlanSpecification(hqAirport,orientalPear, TimeSpan.builder().build()),
                TripPlanType.RIDE_SHARING
        )));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<CoTrip> argumentCaptor= ArgumentCaptor.forClass(CoTrip.class);
        verify(coTripRepository).save(argumentCaptor.capture());
        CoTrip capturedCoTrip = argumentCaptor.getValue();
        assertThat(capturedCoTrip).isNotNull();
        assertThat(capturedCoTrip.getStatus()).isEqualTo(CoTripStatus.CREATED);
    }

}
