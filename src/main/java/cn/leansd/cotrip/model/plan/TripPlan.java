package cn.leansd.cotrip.model.plan;

import cn.leansd.base.model.DomainEntity;
import cn.leansd.base.model.UserId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TripPlan extends DomainEntity {
    public TripPlan(UserId creatorId, PlanSpecification planSpecification) {
        this();
        this.planSpecification = planSpecification;
        this.status = TripPlanStatus.WAITING_MATCH;
        addCreationInfo(creatorId);
    }

    @Embedded
    PlanSpecification planSpecification;

    @Enumerated(EnumType.STRING)
    private TripPlanStatus status;

    public void joinedCoTrip() {
        this.status = TripPlanStatus.JOINED;
    }
}