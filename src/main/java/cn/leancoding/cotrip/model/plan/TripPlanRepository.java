package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.model.plan.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripPlanRepository extends JpaRepository<TripPlan, String> {
    @Query("select tp from TripPlan tp where tp.status = 'WAITING_MATCH'")
    List<TripPlan> findAllNotMatching();
}
