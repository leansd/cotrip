package cn.leansd.cotrip.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanDTO {
    private String id;
    private PlanSpecification planSpecification;
    private String planType;
    private String status;
    private String userId;
}
