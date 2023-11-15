package cn.leansd.cotrip.model.plan;

import cn.leansd.base.model.ValueObject;
import cn.leansd.geo.Location;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PickupLocation extends ValueObject {
    @Embedded
    private final Location location;
    public PickupLocation(Location location) {
        super();
        this.location = location;
    }
}