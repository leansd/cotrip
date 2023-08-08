package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.base.DomainEntity;
import cn.leancoding.cotrip.model.user.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.leancoding.cotrip.model.location.Location;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TripPlan extends DomainEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "creator_id")),
    })
    UserId creatorId;

    @Embedded
    RouteSpecification routeSpecification;
}