package cn.leansd.site.application;

import cn.leansd.geo.Location;
import cn.leansd.site.domain.site.PickupSite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupSiteDTO {
    private String id;
    private Location location;
    private String siteType;

    public PickupSiteDTO(PickupSite nearestPickupSite) {
        if (nearestPickupSite!= null) {
            this.id = nearestPickupSite.getId();
            this.location = nearestPickupSite.getLocation();
            this.siteType = nearestPickupSite.getSiteType().toString();
        }
    }

    public PickupSiteDTO(Location location) {
        this.location = location;
    }
}
