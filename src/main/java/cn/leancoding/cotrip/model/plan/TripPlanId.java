package cn.leancoding.cotrip.model.plan;
import cn.leancoding.cotrip.base.model.GenericId;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class TripPlanId extends GenericId {
    public TripPlanId(String value) {
        super(value);
    }
}