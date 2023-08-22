package cn.leansd.cotrip.model.plan;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.DomainEntity;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.model.cotrip.CoTripId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TripPlan extends AggregateRoot<TripPlan> {
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

    public void joinedCoTrip(CoTripId coTripId) {
        this.coTripId = coTripId;
        this.status = TripPlanStatus.JOINED;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "cotrip_id")),
    })
    private CoTripId coTripId;
}