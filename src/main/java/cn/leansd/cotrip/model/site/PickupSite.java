package cn.leansd.cotrip.model.site;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.model.cotrip.CoTripStatus;
import cn.leansd.geo.Location;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PickupSite extends AggregateRoot {
    private Location location;
    public PickupSite(){
        super();
    }
    public PickupSite(Location location){
        this.location = location;
    }
}