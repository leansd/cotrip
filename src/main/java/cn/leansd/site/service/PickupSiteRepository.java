package cn.leansd.site.service;

import cn.leansd.site.model.site.PickupSite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupSiteRepository extends JpaRepository<PickupSite, String> {
}
