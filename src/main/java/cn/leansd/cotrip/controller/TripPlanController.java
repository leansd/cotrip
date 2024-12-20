package cn.leansd.cotrip.controller;

import cn.leansd.base.exception.RequestedResourceNotFound;
import cn.leansd.base.model.UserId;
import cn.leansd.base.session.SessionDTO;
import cn.leansd.base.session.UserSession;
import cn.leansd.cotrip.types.TripPlanDTO;
import cn.leansd.cotrip.application.plan.TripPlanService;
import cn.leansd.cotrip.domain.plan.TripPlanId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cotrip/plan/v1/trip-plans/")
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

    @GetMapping("/")
    public ResponseEntity<List<TripPlanDTO>> retrieveAllTripPlans(@UserSession SessionDTO session) {
        List<TripPlanDTO> plans = tripPlanService.retrieveTripPlans(UserId.of(session.getUserId()));
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripPlanDTO> retrieveTripPlan(@PathVariable("id") String id, @UserSession SessionDTO session) throws RequestedResourceNotFound {
        TripPlanDTO tripPlan = tripPlanService.retrieveTripPlan(TripPlanId.of(id),UserId.of(session.getUserId()));
        return new ResponseEntity<>(tripPlan, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TripPlanDTO> cancelTripPlan(@PathVariable("id") String id, @UserSession SessionDTO session) throws RequestedResourceNotFound {
        TripPlanDTO tripPlan = tripPlanService.cancelTripPlan(TripPlanId.of(id),UserId.of(session.getUserId()));
        return new ResponseEntity<>(tripPlan, HttpStatus.OK);
    }




}
