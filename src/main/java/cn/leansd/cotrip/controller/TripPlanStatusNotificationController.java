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
    public static final String TOPIC_TRIP_PLAN_JOINED = "/topic/updates";
    @TransactionalEventListener
    public void receivedTripPlanJoinedEvent(TripPlanJoinedEvent event) throws InconsistentStatusException {
        System.out.println(event);
        //template.convertAndSendToUser(event.getData().getUserId(), TOPIC_TRIP_PLAN_JOINED, event.getData());
    }
}





