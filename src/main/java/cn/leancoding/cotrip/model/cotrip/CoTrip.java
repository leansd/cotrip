package cn.leancoding.cotrip.model.cotrip;

import cn.leancoding.cotrip.base.model.DomainEntity;
import cn.leancoding.cotrip.model.plan.PlanSpecification;
import cn.leancoding.cotrip.model.plan.TripPlanId;
import cn.leancoding.cotrip.model.plan.TripPlanStatus;
import cn.leancoding.cotrip.model.user.UserId;
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