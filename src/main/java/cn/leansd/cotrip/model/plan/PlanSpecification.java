package cn.leansd.cotrip.model.plan;

import cn.leansd.geo.location.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class PlanSpecification  {

    public PlanSpecification(Location departureLocation, Location arrivalLocation, TimeSpan plannedDepartureTime) {
        this(departureLocation, arrivalLocation, plannedDepartureTime,  1);
    }

    public PlanSpecification(Location departureLocation, Location arrivalLocation, TimeSpan plannedDepartureTime, int requiredSeats) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.plannedDepartureTime = plannedDepartureTime;
        this.requiredSeats = requiredSeats;
    }

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
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "departure_time_start")),
            @AttributeOverride(name = "end", column = @Column(name = "departure_time_end")),
    })
    private TimeSpan plannedDepartureTime;
    private int requiredSeats;
}
