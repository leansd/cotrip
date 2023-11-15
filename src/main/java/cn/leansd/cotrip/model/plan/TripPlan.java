package cn.leansd.cotrip.model.plan;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.model.cotrip.CoTripId;
import cn.leansd.cotrip.service.plan.TripPlanDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TripPlan extends AggregateRoot {
    public TripPlan(UserId creatorId, PlanSpecification planSpecification) {
        this();
        this.planSpecification = planSpecification;
        this.status = TripPlanStatus.WAITING_MATCH;
        addCreationInfo(creatorId);
    }

    @Embedded
    PlanSpecification planSpecification;

    @OneToOne
    @Nullable
    PickupLocation pickupLocation;

    @Enumerated(EnumType.STRING)
    private TripPlanStatus status;

    public void joinCoTrip(CoTripId coTripId) {
        this.coTripId = coTripId;
        this.status = TripPlanStatus.JOINED;
        registerEvent(new TripPlanJoinedEvent(new TripPlanDTO(this)));
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "cotrip_id")),
    })
    private CoTripId coTripId;

    public void cancel() {
        this.status = TripPlanStatus.CANCELED;
        registerEvent(new TripPlanCanceledEvent(new TripPlanDTO(this)));
    }
}