package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.event.EventPublisher;
import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.base.model.GenericId;
import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.cotrip.CoTripCreatedEvent;
import cn.leansd.cotrip.model.cotrip.CoTripRepository;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.geo.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static cn.leansd.cotrip.service.TestMap.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoTripMatchingResultTest {
    TripPlanRepository tripPlanRepository = Mockito.mock(TripPlanRepository.class);
    CoTripRepository coTripRepository = Mockito.mock(CoTripRepository.class);
    EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
    GeoService geoService = Mockito.mock(GeoService.class);

    LocalDateTime Y2305010800 = LocalDateTime.of(2023, 5, 1, 8, 00);
    LocalDateTime Y2305010830 = LocalDateTime.of(2023, 5, 1, 8, 30);

    CoTripMatchingService coTripMatchingService = null;

    @BeforeEach
    public void setUp(){
        coTripMatchingService = new CoTripMatchingService(tripPlanRepository, coTripRepository, geoService, eventPublisher);
    }

    @DisplayName("匹配成功后应该更新TripPlan的状态")
    @Test
    public void shouldChangeTripPlanStatusWhenMatched() throws InconsistentStatusException {
        TripPlanDTO tripPlanDTO = new TripPlanDTO(new PlanSpecification(orientalPear, peopleSquare, TimeSpan.builder()
                .start(Y2305010800)
                .end(Y2305010830)
                .build(),1));
        TripPlan existingPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_1"),
                tripPlanDTO.getPlanSpecification());
        TripPlan newPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_id_2"),
                tripPlanDTO.getPlanSpecification());
        when(tripPlanRepository.findAllNotMatching()).thenReturn(Arrays.asList(existingPlan));
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(TripPlanConverter.toDTO(newPlan)));

        ArgumentCaptor<TripPlan> argumentCaptor= ArgumentCaptor.forClass(TripPlan.class);
        verify(tripPlanRepository).save(argumentCaptor.capture());
        TripPlan capturedTripPlan = argumentCaptor.getValue();
        assertEquals(TripPlanStatus.JOINED,capturedTripPlan.getStatus());
    }

}
