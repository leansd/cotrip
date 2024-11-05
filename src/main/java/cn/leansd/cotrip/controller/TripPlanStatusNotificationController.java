package cn.leansd.cotrip.controller;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.domain.plan.TripPlanJoinedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.event.TransactionalEventListener;

@Controller
public class TripPlanStatusNotificationController {
    Logger logger = LoggerFactory.getLogger(TripPlanStatusNotificationController.class);
    @Autowired
    private SimpMessagingTemplate template;
    public static final String BROADCAST_UPDATE_TOPIC = "/topic/updates";
    public static final String UPDATE_QUEUE = "/queue/status";
    @TransactionalEventListener
    public void receivedTripPlanJoinedEvent(TripPlanJoinedEvent event) throws InconsistentStatusException {
        template.convertAndSendToUser(event.getData().getUserId(), UPDATE_QUEUE, event);
        logger.info("sent TripPlanJoinedEvent to user: " + event.getData().getUserId() + event.getData());
    }
}





