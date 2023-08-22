package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.event.EventPublisher;
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
import cn.leansd.geo.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static cn.leansd.cotrip.service.TestMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoTripMatchingResultTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    TripPlanService tripPlanService = Mockito.mock(TripPlanService.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);
    EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
    GeoService geoService = Mockito.mock(GeoService.class);

    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);

    CoTripMatchingService coTripMatchingService = null;
    TripPlan existingPlan  = null;
    TripPlan newPlan = null;
    @BeforeEach
    public void setUp(){
        coTripMatchingService = new CoTripMatchingService(tripPlanRepository, tripPlanService, coTripRepository, geoService, eventPublisher);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        existingPlan = new TripPlan(UserId.of("user_id_1"),
                tripPlanDTO.getPlanSpecification());
        newPlan = new TripPlan(UserId.of("user_id_2"),
                tripPlanDTO.getPlanSpecification());
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态")
    @Test
    public void shouldChangeTripPlanStatusWhenMatched() throws InconsistentStatusException {
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<List<TripPlanId>> tripPlanListCaptor= ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<CoTripId> coTripIdArgumentCaptor= ArgumentCaptor.forClass(CoTripId.class);
        verify(tripPlanService).joinedCoTrip(coTripIdArgumentCaptor.capture(), tripPlanListCaptor.capture());
        List<TripPlanId> capturedTripPlan = tripPlanListCaptor.getValue();
        assertThat(capturedTripPlan).containsExactlyInAnyOrderElementsOf(
                Arrays.asList(
                        TripPlanId.of(existingPlan.getId()),
                        TripPlanId.of(newPlan.getId())
        ));
        assertThat(coTripIdArgumentCaptor.getValue()).isNotNull();
    }

    @DisplayName("匹配成功后应该创建CoTrip，状态应该是CREATED")
    @Test
    public void shouldCreateCoTripWhenMatched() throws InconsistentStatusException {
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<CoTrip> argumentCaptor= ArgumentCaptor.forClass(CoTrip.class);
        verify(coTripRepository).save(argumentCaptor.capture());
        CoTrip capturedCoTrip = argumentCaptor.getValue();
        assertThat(capturedCoTrip).isNotNull();
        assertThat(capturedCoTrip.getStatus()).isEqualTo(CoTripStatus.CREATED);
    }

}
