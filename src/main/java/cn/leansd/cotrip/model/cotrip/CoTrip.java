package cn.leansd.cotrip.model.cotrip;

import cn.leansd.base.model.DomainEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CoTrip extends DomainEntity {
    @ElementCollection
    List<String> tripPlanIdList;

    @Enumerated(EnumType.STRING)
    private CoTripStatus status;
}