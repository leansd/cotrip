package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTrip;
import cn.leansd.cotrip.model.cotrip.CoTripId;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.cotrip.CoTripStatus;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.cotrip.service.site.PickupSiteService;
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

import static cn.leansd.cotrip.service.TestMap.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    public void shouldChangeTripPlanStatusWhenMatched() throws InconsistentStatusException {
        PlanSpecification planSpecification_1 = new PlanSpecification(nearHqStationSouth, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1);
        PlanSpecification planSpecification_2 = new PlanSpecification(nearHqStationSouth, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1);

        existingPlan = new TripPlan(UserId.of("user_id_1"),
                planSpecification_1);
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));

        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(new TripPlan(UserId.of("user_id_2"),
                planSpecification_2))));

        ArgumentCaptor<List<TripPlanId>> tripPlanListCaptor= ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<CoTripId> coTripIdArgumentCaptor= ArgumentCaptor.forClass(CoTripId.class);

        ArgumentCaptor<TripPlan> tripPlanArgumentCaptor= ArgumentCaptor.forClass(TripPlan.class);
        verify(tripPlanRepository).save(tripPlanArgumentCaptor.capture());
        TripPlan capturedTripPlan = tripPlanArgumentCaptor.getValue();
        assertThat(capturedTripPlan.getPickupLocation()).isNotNull();
    }

}
