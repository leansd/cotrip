package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.model.GenericId;
import cn.leancoding.cotrip.model.cotrip.CoTripRepository;
import cn.leancoding.cotrip.model.plan.PlanSpecification;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import cn.leancoding.cotrip.model.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CoTripMatchingServiceTest {
    @Autowired TripPlanRepository tripPlanRepository;
    @Autowired CoTripMatchingService coTripMatchingService;
    @Autowired CoTripRepository coTripRepository;

    @Test
    public void testMatchingTripPlan() throws InconsistentStatusException {
        LocationDTO departureLocation = new LocationDTO(31.240084, 121.501868);
        LocationDTO arrivalLocation = new LocationDTO(31.232862, 121.472768);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(departureLocation, arrivalLocation, LocalDateTime.now().plusHours(2));

        PlanSpecification spec = new PlanSpecification(tripPlanDTO);
        TripPlan existingPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_1"),
                spec);
        tripPlanRepository.save(existingPlan);

        TripPlan newPlan = new TripPlan((UserId) GenericId.of(UserId.class,"user_1"),
                spec);
        tripPlanRepository.save(newPlan);
        coTripMatchingService.receivedTripPlanCreatedEvent(new TripPlanCreatedEvent(newPlan.getId()));

        assertEquals(1,coTripRepository.count());
    }
}
