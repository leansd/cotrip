package cn.leansd.site;

import cn.leansd.base.model.UserId;
import cn.leansd.base.types.TimeSpan;
import cn.leansd.cotrip.model.plan.*;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.site.service.PickupSiteDTO;
import cn.leansd.site.service.PickupSiteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static cn.leansd.cotrip.service.TestMap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RecordApplicationEvents
public class PickupSiteServiceTest {
    @Autowired
    private PickupSiteService pickupSiteService;

    @DisplayName("存在多个上车点时返回最近的上车点")
    @Test
    public void testGetNearestPickupSite() {
        pickupSiteService.addPickupSite(hqStationSouth);
        pickupSiteService.addPickupSite(peopleSquare);
        PickupSiteDTO pickupSiteDTO = pickupSiteService.findNearestPickupSite(nearHqStationSouth);
        assertThat(pickupSiteDTO.getLocation()).isEqualTo(hqStationSouth);
    }

    @Autowired
    private ApplicationEvents applicationEvents; // 注入ApplicationEvents来审查事件
}
