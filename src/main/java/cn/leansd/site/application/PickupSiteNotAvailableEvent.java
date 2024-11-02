package cn.leansd.site.application;

import cn.leansd.base.model.DomainEvent;
import cn.leansd.geo.Location;
import lombok.Data;

@Data
public class PickupSiteNotAvailableEvent extends DomainEvent {
    private Location location;
    public PickupSiteNotAvailableEvent(Location Location) {
        this.location = Location;
    }
}
