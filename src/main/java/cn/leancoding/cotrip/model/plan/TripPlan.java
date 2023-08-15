package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.base.DomainEntity;
import cn.leancoding.cotrip.model.user.UserId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TripPlan extends DomainEntity {

    public TripPlan(UserId creatorId, PlanSpecification planSpecification) {
        this();
        this.planSpecification = planSpecification;
        this.status = TripPlanStatus.DRAFTED;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "creator_id")),
    })
    UserId creatorId;

    @Embedded
    PlanSpecification planSpecification;

    @Enumerated(EnumType.STRING)
    private TripPlanStatus status;

    public void publish() {
        this.status = TripPlanStatus.PUBLISHED;
    }
}