package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.site.service.PickupSiteDTO;
import cn.leansd.site.service.PickupSiteService;
import cn.leansd.geo.GeoService;
import cn.leansd.geo.haversine.HaversineGeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cn.leansd.cotrip.service.TestMap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatchPickupLocationTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    TripPlanService tripPlanService = Mockito.mock(TripPlanService.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);

    GeoService geoService = new HaversineGeoService();
    PickupSiteService pickupSiteService = Mockito.mock(PickupSiteService.class);

    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);

    CoTripMatchingService coTripMatchingService = null;
    TripPlan existingPlan  = null;
    TripPlan newPlan = null;
    @BeforeEach
    public void setUp(){
        coTripMatchingService = new CoTripMatchingService(tripPlanRepository,  coTripRepository, geoService,pickupSiteService);
    }

    @DisplayName("匹配成功后，应该更新TripPlan的pickup信息")
    @Test
    public void shouldChangeTripPlanStatusWhenMatched() throws InconsistentStatusException {
        PlanSpecification planSpecification = new PlanSpecification(nearHqStationSouth, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1);

        existingPlan = new TripPlan(UserId.of("user_id_1"),
                planSpecification);
        newPlan = new TripPlan(UserId.of("user_id_2"),
                planSpecification);

        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
        when(tripPlanRepository.findById(eq(existingPlan.getId()))).thenReturn(Optional.of(existingPlan));
        when(tripPlanRepository.findById(eq(newPlan.getId()))).thenReturn(Optional.of(newPlan));

        when(pickupSiteService.findNearestPickupSite(notNull())).thenReturn(PickupSiteDTO.builder().location(hqStationNorth).build());
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<List<TripPlan>> tripPlanListCaptor= ArgumentCaptor.forClass(List.class);
        verify(tripPlanRepository).saveAll(tripPlanListCaptor.capture());
        List<TripPlan> capturedTripPlan = tripPlanListCaptor.getValue();
        capturedTripPlan.forEach(tripPlan -> {
            assertThat(tripPlan.getPickupLocation().getLocation()).isEqualTo(hqStationNorth);
        });
    }
}
