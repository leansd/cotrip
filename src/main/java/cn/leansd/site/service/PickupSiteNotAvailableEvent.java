package cn.leansd.site.service;

import cn.leansd.base.model.DomainEvent;
import cn.leansd.geo.Location;

public class PickupSiteNotAvailableEvent extends DomainEvent {
    private Location location;
    public PickupSiteNotAvailableEvent(Location Location) {
        this.location = Location;
    }
}
