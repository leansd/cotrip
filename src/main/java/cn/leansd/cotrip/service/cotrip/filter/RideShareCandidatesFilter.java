package cn.leansd.cotrip.service.cotrip.filter;

import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.cotrip.model.plan.TripPlanRepository;
import cn.leansd.cotrip.model.plan.TripPlanType;
import cn.leansd.cotrip.service.plan.TripPlanDTO;

import java.util.List;

public class RideShareCandidatesFilter extends CandidatesFilter {

    public RideShareCandidatesFilter(TripPlanRepository tripPlanRepository) {
        super(tripPlanRepository);
    }

    @Override
    public List<TripPlan> filter(TripPlanDTO tripPlan) {
        return tripPlanRepository.findAllMatchCandidates(TripPlanType.RIDE_SHARING);
    }
}
