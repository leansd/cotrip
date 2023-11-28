package cn.leansd.cotrip.domain.cotrip;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoTripRepository extends JpaRepository<CoTrip, String> {
    CoTrip findFirstByOrderByCreatedTimeDesc();
}
