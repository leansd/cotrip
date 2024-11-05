package cn.leansd.cotrip.application.cotrip.filter;

import cn.leansd.cotrip.domain.plan.TripPlanRepository;
import cn.leansd.cotrip.domain.plan.TripPlanType;

public class CandidatesFilterFactory {
    public static CandidatesFilter build(String planType, TripPlanRepository tripPlanRepository) {
        if (TripPlanType.RIDE_SHARING.name().equals(planType)){
            return new RideShareCandidatesFilter(tripPlanRepository);
        }
        if (TripPlanType.HITCHHIKING_PROVIDER.name().equals(planType)){
            return new HitchhikingProviderCandidatesFilter(tripPlanRepository);
        }
        if (TripPlanType.HITCHHIKING_CONSUMER.name().equals(planType)){
            return new HitchhikingConsumerCandidatesFilter(tripPlanRepository);
        }
        return null;
    }
}
