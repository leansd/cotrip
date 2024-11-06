package cn.leansd.cotrip.types;

import cn.leansd.cotrip.domain.plan.TripPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanDTO {
    private String id;
    private PlanSpecification planSpecification;
    private String status;
    private String userId;
    public static TripPlanDTO buildFrom(PlanSpecification planSpecification){
        TripPlanDTO tripPlanDTO = new TripPlanDTO();
        tripPlanDTO.id = null;
        tripPlanDTO.planSpecification = planSpecification;
        return tripPlanDTO;
    }
}
