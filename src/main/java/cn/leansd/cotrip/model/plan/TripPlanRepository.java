package cn.leansd.cotrip.model.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripPlanRepository extends JpaRepository<TripPlan, String> {
    @Query("select tp from TripPlan tp where tp.status = 'WAITING_MATCH' and tp.planType=:planType")
    List<TripPlan> findAllMatchCandidates(TripPlanType planType);
    List<TripPlan> findAllByCreatorId(String id);
}
