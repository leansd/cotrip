package cn.leansd.cotrip.service.cotrip.filter;

import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.service.plan.TripPlanDTO;

import java.util.List;

public abstract class CandidatesFilter {
    TripPlanRepository tripPlanRepository;
    public CandidatesFilter(TripPlanRepository tripPlanRepository) {
        this.tripPlanRepository = tripPlanRepository;
    }
    public abstract List<TripPlan> filter(TripPlanDTO tripPlan);
}
