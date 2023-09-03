package cn.leansd.cotrip.controller;

import cn.leansd.base.exception.InconsistentStatusException;
import cn.leansd.cotrip.model.plan.TripPlanJoinedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.event.TransactionalEventListener;

@Controller
public class TripPlanStatusNotificationController {

    @Autowired
    private SimpMessagingTemplate template;
    public static final String BROADCAST_UPDATE_TOPIC = "/topic/updates";
    public static final String UPDATE_QUEUE = "/queue/status";
    @TransactionalEventListener
    public void receivedTripPlanJoinedEvent(TripPlanJoinedEvent event) throws InconsistentStatusException {
        System.out.println(event);
        //template.convertAndSend(BROADCAST_UPDATE_TOPIC, event);
        template.convertAndSendToUser("user-id-1", UPDATE_QUEUE, event);

    }
}





