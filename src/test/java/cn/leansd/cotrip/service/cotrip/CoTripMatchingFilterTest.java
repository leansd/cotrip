package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlanId;
import cn.leansd.cotrip.model.plan.TripPlanStatus;
import cn.leansd.cotrip.model.plan.TripPlanType;
import cn.leansd.cotrip.service.plan.NoVehicleOwnerException;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static cn.leansd.cotrip.service.TestMap.hqAirport;
import static cn.leansd.cotrip.service.TestMap.orientalPear;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext
public class CoTripMatchingFilterTest {
    @Autowired CoTripMatchingService coTripMatchingService;
    @Autowired TripPlanService tripPlanService;
    PlanSpecification spec = new PlanSpecification(
            hqAirport, orientalPear,
            new TimeSpan(LocalDateTime.of(2023, 5, 1, 8, 00),
                    LocalDateTime.of(2023, 5, 1, 8, 30)),1);
    @DisplayName("司机单不会匹配无车共乘单")
    @Test
    public void shouldNotMatchRideSharingToHitchhiking() throws InconsistentStatusException, NoVehicleOwnerException, RequestedResourceNotFound {
        TripPlanDTO existedPlan = tripPlanService.createTripPlan(TripPlanDTO.builder()
                .userId("user-id-1")
                .planType(TripPlanType.RIDE_SHARING.name())
                .planSpecification(spec)
                .build());
        TripPlanDTO newPlan = tripPlanService.createTripPlan(TripPlanDTO.builder()
                .userId("user-id-2")
                .planType(TripPlanType.HITCHHIKING_PROVIDER.name())
                .planSpecification(spec)
                .build());
        existedPlan = tripPlanService.retrieveTripPlan(TripPlanId.of(existedPlan.getId()), UserId.of("user-id-1"));
        assertThat(existedPlan.getStatus()).isEqualTo(TripPlanStatus.WAITING_MATCH.name());
    }
}
