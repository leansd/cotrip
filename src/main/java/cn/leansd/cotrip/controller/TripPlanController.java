package cn.leansd.cotrip.controller;

import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import cn.leansd.cotrip.service.plan.TripPlanService;
import cn.leansd.base.session.SessionDTO;
import cn.leansd.base.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip-plan")
public class TripPlanController {

    private final TripPlanService tripPlanService;
    @Autowired
    public TripPlanController(TripPlanService tripPlanService) {
        this.tripPlanService = tripPlanService;
    }

    @PostMapping
    public ResponseEntity<TripPlanDTO> createTripPlan(@RequestBody TripPlanDTO tripPlanDTO, @UserSession SessionDTO session) {
        TripPlanDTO createdTripPlan = tripPlanService.createTripPlan(tripPlanDTO,UserId.of(session.getUserId()));
        return new ResponseEntity<>(createdTripPlan, HttpStatus.CREATED);
    }
}
