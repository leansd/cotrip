package cn.leansd.site.model.site;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.geo.Location;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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