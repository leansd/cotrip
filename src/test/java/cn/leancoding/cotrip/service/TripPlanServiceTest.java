package cn.leancoding.cotrip.service;

import cn.leancoding.cotrip.base.GenericId;
import cn.leancoding.cotrip.event.EventPublisher;
import cn.leancoding.cotrip.model.plan.TripPlan;
import cn.leancoding.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leancoding.cotrip.model.plan.TripPlanRepository;
import cn.leancoding.cotrip.model.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripPlanServiceTest {

    @Autowired
    private TripPlanService tripPlanService;

    @Autowired
    private TripPlanRepository tripPlanRepository;

    @MockBean
    private EventPublisher eventPublisher;

    @Test
    public void testCreateTripPlan() {
        // 创建测试数据
        LocationDTO departureLocation = new LocationDTO(31.240084, 121.501868);
        LocationDTO arrivalLocation = new LocationDTO(31.232862, 121.472768);
        TripPlanDTO tripPlanDTO = new TripPlanDTO(departureLocation, arrivalLocation, LocalDateTime.now().plusHours(2), 30);

        // 模拟事件发布
        doNothing().when(eventPublisher).publishEvent(any(TripPlanCreatedEvent.class));

        // 调用服务方法
        TripPlan tripPlan = tripPlanService.createTripPlan(tripPlanDTO, (UserId) GenericId.of(UserId.class,"user_1"));

        // 验证数据库保存
        Optional<TripPlan> retrievedTripPlan = tripPlanRepository.findById(tripPlan.getId());
        assertTrue(retrievedTripPlan.isPresent());
        assertEquals(tripPlan.getId(), retrievedTripPlan.get().getId());

        // 使用ArgumentCaptor来捕获参数
        ArgumentCaptor<TripPlanCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(TripPlanCreatedEvent.class);

        // 验证事件发布
        verify(eventPublisher).publishEvent(argumentCaptor.capture());
        TripPlanCreatedEvent capturedEvent = argumentCaptor.getValue();
        assertEquals(tripPlan.getId(), capturedEvent.getTripPlanId());
    }
}
