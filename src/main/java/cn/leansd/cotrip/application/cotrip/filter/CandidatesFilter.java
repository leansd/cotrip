package cn.leansd.cotrip.application.cotrip.filter;

import cn.leansd.cotrip.application.plan.TripPlanDTO;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;

import java.util.List;

public abstract class CandidatesFilter {
    TripPlanRepository tripPlanRepository;
    public CandidatesFilter(TripPlanRepository tripPlanRepository) {
        this.tripPlanRepository = tripPlanRepository;
    }
    public abstract List<TripPlan> filter(TripPlanDTO tripPlan);
}
