package cn.leansd.cotrip.domain.plan;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.UserId;
import cn.leansd.cotrip.application.plan.TripPlanDTO;
import cn.leansd.cotrip.domain.cotrip.CoTripId;
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
    public TripPlan(TripPlanDTO tripPlanDTO) {
        this();
        this.planSpecification = tripPlanDTO.getPlanSpecification();
        this.planType = TripPlanType.valueOf(tripPlanDTO.getPlanType());
        this.status = TripPlanStatus.WAITING_MATCH;
        addCreationInfo(UserId.of(tripPlanDTO.getUserId()));
    }

    public TripPlan(UserId userId, PlanSpecification planSpecification, TripPlanType tripPlanType) {
        this();
        this.planSpecification = planSpecification;
        this.planType = tripPlanType;
        this.status = TripPlanStatus.WAITING_MATCH;
        addCreationInfo(userId);

    }

    @Embedded
    PlanSpecification planSpecification;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Nullable
    PickupLocation pickupLocation;

    @Enumerated(EnumType.STRING)
    private TripPlanStatus status;

    @Enumerated(EnumType.STRING)
    private TripPlanType planType;



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