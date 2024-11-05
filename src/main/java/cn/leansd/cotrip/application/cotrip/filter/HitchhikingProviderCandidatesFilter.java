package cn.leansd.cotrip.application.cotrip.filter;

import cn.leansd.cotrip.domain.plan.TripPlan;
import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanType;
import cn.leansd.cotrip.application.plan.TripPlanDTO;

import java.util.List;

public class HitchhikingProviderCandidatesFilter extends CandidatesFilter {
    public HitchhikingProviderCandidatesFilter(TripPlanRepository tripPlanRepository) {
        super(tripPlanRepository);
    }

    @Override
    public List<TripPlan> filter(TripPlanDTO tripPlan) {
        return tripPlanRepository.findAllMatchCandidates(TripPlanType.HITCHHIKING_CONSUMER);
    }
}