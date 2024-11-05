package cn.leansd.cotrip.application.cotrip.filter;

import cn.leansd.cotrip.application.plan.TripPlanDTO;
import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanType;

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
