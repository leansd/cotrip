package cn.leansd.cotrip.domain.plan;

import cn.leansd.base.model.ValueObject;
import cn.leansd.geo.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PickupLocation extends ValueObject {
    @Embedded
    private Location location;
    public PickupLocation(Location location) {
        super();
        this.location = location;
    }

    public PickupLocation(PickupLocation prototype) {
        this.location = prototype.location;
    }
}