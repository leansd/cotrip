package cn.leancoding.cotrip.model.plan;


import lombok.Data;

@Data
public class TripPlanCreatedEvent {
    private String tripPlanId;
    public TripPlanCreatedEvent(String tripPlanId) {
        this.tripPlanId = tripPlanId;
    }
}
