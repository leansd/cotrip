package cn.leansd.cotrip.model.cotrip;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.DomainEntity;
import cn.leansd.base.model.UserId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CoTrip extends AggregateRoot {
    public CoTrip(List<String> tripPlanIdList) {
        super();
        this.tripPlanIdList = tripPlanIdList;
        this.status = CoTripStatus.CREATED;
        addCreationInfo(UserId.of("SYSTEM"));
    }
    public CoTrip(){
        super();
    }
    @ElementCollection
    List<String> tripPlanIdList;
    @Enumerated(EnumType.STRING)
    private CoTripStatus status;
}