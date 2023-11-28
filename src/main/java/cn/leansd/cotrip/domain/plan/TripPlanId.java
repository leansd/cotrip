package cn.leansd.cotrip.domain.plan;
import cn.leansd.base.model.GenericId;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class TripPlanId extends GenericId {
    public TripPlanId(String value) {
        super(value);
    }
    public static TripPlanId of(String id) {
        return new TripPlanId(id);
    }
}