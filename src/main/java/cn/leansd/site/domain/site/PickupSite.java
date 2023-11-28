package cn.leansd.site.domain.site;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.geo.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PickupSite extends AggregateRoot {
    @Embedded
    private Location location;
    @Enumerated(EnumType.STRING)
    private SiteType siteType;
    public PickupSite(){
        super();
    }
    public PickupSite(Location location, SiteType siteType){
        this.location = location;
        this.siteType = siteType;
    }
}