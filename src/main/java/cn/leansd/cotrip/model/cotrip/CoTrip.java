package cn.leansd.cotrip.model.cotrip;

import cn.leansd.base.model.AggregateRoot;
import cn.leansd.base.model.UserId;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

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