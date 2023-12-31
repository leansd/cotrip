package cn.leansd.site;

import cn.leansd.cotrip.model.plan.TripPlanCreatedEvent;
import cn.leansd.site.model.site.SiteType;
import cn.leansd.site.service.PickupSiteDTO;
import cn.leansd.site.service.PickupSiteNotAvailableEvent;
import cn.leansd.site.service.PickupSiteRepository;
import cn.leansd.site.service.PickupSiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static cn.leansd.cotrip.service.TestMap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RecordApplicationEvents
public class PickupSiteServiceTest {
    @Autowired
    private PickupSiteService pickupSiteService;
    @Autowired
    private PickupSiteRepository pickupSiteRepository;
    @BeforeEach
    public void setUp() {
        pickupSiteRepository.deleteAll();
    }

    @DisplayName("存在多个上车点时返回最近的上车点")
    @Test
    public void testGetNearestPickupSite() {
        pickupSiteService.addPickupSite(hqStationSouth);
        pickupSiteService.addPickupSite(peopleSquare);
        PickupSiteDTO pickupSiteDTO = pickupSiteService.findNearestPickupSite(nearHqStationSouth);
        assertThat(pickupSiteDTO.getLocation()).isEqualTo(hqStationSouth);
    }

    @DisplayName("最近的上车点超过500米时，直接把用户出发位置作为临时上车点")
    @Test
    public void testDepartureLocationAsPickupSiteWhenNearbyPickupSiteIsTooFar() {
        pickupSiteService.addPickupSite(peopleSquare);
        pickupSiteService.setMaxPickupSiteDistance(0.5);
        PickupSiteDTO pickupSiteDTO = pickupSiteService.findNearestPickupSite(nearHqStationSouth);
        assertThat(pickupSiteDTO.getLocation()).isEqualTo(nearHqStationSouth);
        assertThat(pickupSiteDTO.getSiteType()).isEqualTo(SiteType.TEMPORARY.name());
    }


    @DisplayName("不存在预设的上车点时，直接把用户出发位置作为临时上车点")
    @Test
    public void testDepartureLocationAsPickupSiteWhenNoPickupSiteDefined() {
        /* no pickup site */
        PickupSiteDTO pickupSiteDTO = pickupSiteService.findNearestPickupSite(nearHqStationSouth);
        assertThat(pickupSiteDTO.getLocation()).isEqualTo(nearHqStationSouth);
        assertThat(pickupSiteDTO.getSiteType()).isEqualTo(SiteType.TEMPORARY.name());
    }
    //
    @DisplayName("临时上车点应该存入数据库并且触发PickupSiteNotAvailable事件")
    @Test
    public void testSaveTemporaryPickupSiteAndTriggerPickupSiteNotAvailable() {
        pickupSiteService.setMaxPickupSiteDistance(0.5);
        pickupSiteService.findNearestPickupSite(nearHqStationSouth);
        assertThat(pickupSiteRepository.count()).isEqualTo(1);
        assertThat(pickupSiteRepository.findAll().get(0).getSiteType()).isEqualTo(SiteType.TEMPORARY);
        verifyPickupSiteNotAvailableEventPublished();
    }

    private void verifyPickupSiteNotAvailableEventPublished() {
        assertThat(applicationEvents.stream(PickupSiteNotAvailableEvent.class))
                .isNotEmpty()
                .hasSize(1);
    }

    @Autowired
    private ApplicationEvents applicationEvents; // 注入ApplicationEvents来审查事件
}
