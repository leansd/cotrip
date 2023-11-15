package cn.leansd.site.service;

import cn.leansd.cotrip.model.plan.PlanSpecification;
import cn.leansd.cotrip.model.plan.TripPlan;
import cn.leansd.geo.Location;
import cn.leansd.site.model.site.PickupSite;
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

    public PickupSiteDTO(PickupSite nearestPickupSite) {
        if (nearestPickupSite!= null) {
            this.id = nearestPickupSite.getId();
            this.location = nearestPickupSite.getLocation();
        }
    }
}
