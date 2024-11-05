package cn.leansd.cotrip.domain.cotrip;

import cn.leansd.base.model.GenericId;
import jakarta.persistence.Embeddable;

@Embeddable
public class CoTripId extends GenericId {
    public CoTripId(String id) {
        super(id);
    }
    public static CoTripId of(String id) {
        return new CoTripId(id);
    }
}
