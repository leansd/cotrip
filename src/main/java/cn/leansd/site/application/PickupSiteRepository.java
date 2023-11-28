package cn.leansd.site.application;

import cn.leansd.site.domain.site.PickupSite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupSiteRepository extends JpaRepository<PickupSite, String> {
}
