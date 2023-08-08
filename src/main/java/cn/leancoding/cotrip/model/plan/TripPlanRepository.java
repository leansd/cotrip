package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.model.plan.TripPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlanRepository extends JpaRepository<TripPlan, String> {
}
