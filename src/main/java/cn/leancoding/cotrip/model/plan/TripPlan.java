package cn.leancoding.cotrip.model.plan;

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
public class TripPlan {
    @Id
    private String id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "departure_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "departure_lng")),
    })
    private Location departureLocation;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "arrival_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "arrival_lng")),
    })
    private Location arrivalLocation;
    private LocalDateTime plannedDepartureTime;
    private int flexibleWaitTime; // 以分钟为单位的可前后浮动的等待时间
}