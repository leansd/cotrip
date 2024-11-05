package cn.leansd.cotrip.application.plan;

import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.domain.plan.PlanSpecification;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanType;
import cn.leansd.vehicle_owner.VehicleOwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static cn.leansd.cotrip.application.TestMap.hqAirport;
import static cn.leansd.cotrip.application.TestMap.peopleSquare;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class HitchhikingTripPlanServiceTest {
    @Mock
    private TripPlanRepository tripPlanRepository;
    @Mock
    private VehicleOwnerService vehicleOwnerService;
    @InjectMocks
    private TripPlanService tripPlanService;
    private PlanSpecification planSpec = new PlanSpecification(hqAirport, peopleSquare,
            TimeSpan.builder()
                    .start(LocalDateTime.of(2023, 5, 1, 8, 0))
                    .end(LocalDateTime.of(2023, 5, 1, 8, 30))
                    .build(),1);
    TripPlanDTO noHitchhikingProviderTripPlanDTO = TripPlanDTO.builder().planSpecification(
                    planSpec)
            .planType(TripPlanType.RIDE_SHARING.name())
            .userId("user-id-1")
            .build();
    TripPlanDTO hitchhikingProviderTripPlanDTO = TripPlanDTO.builder().planSpecification(
                    planSpec)
            .planType(TripPlanType.HITCHHIKING_PROVIDER.name())
            .userId("user-id-1")
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("非司机单不检查车主认证")
    @Test
    void testCreateRideSharingOrHitchhikingConsumerTripPlanShouldNotCheckVehicleOwner()  {
        when(vehicleOwnerService.isVehicleOwner(anyString())).thenReturn(false);
        assertDoesNotThrow(() -> tripPlanService.createTripPlan(noHitchhikingProviderTripPlanDTO));
    }

    @DisplayName("未完成车主认证的用户无法创建司机单")
    @Test
    void testCreateTripPlanShouldFailIfNotVehicleOwner()  {
        when(vehicleOwnerService.isVehicleOwner(anyString())).thenReturn(false);
        assertThrows(NoVehicleOwnerException.class,
                () -> tripPlanService.createTripPlan(hitchhikingProviderTripPlanDTO));
    }

    @DisplayName("已完成车主认证的用户可以创建司机单")
    @Test
    void testCreateTripPlanShouldSuccessIfVehicleOwner()  {
        when(vehicleOwnerService.isVehicleOwner(anyString())).thenReturn(true);
        assertDoesNotThrow(() -> tripPlanService.createTripPlan(hitchhikingProviderTripPlanDTO));
    }
}
