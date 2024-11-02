package cn.leansd.cotrip.domain.plan;

import cn.leansd.base.types.TimeSpan;
import cn.leansd.geo.Location;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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
    @AttributeOverride(name = "name", column = @Column(name = "start_location_name"))
    @AttributeOverride(name = "latitude", column = @Column(name = "start_longitude"))
    @AttributeOverride(name = "longitude", column = @Column(name = "start_latitude"))
    private Location departureLocation;
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "destination_location_name"))
    @AttributeOverride(name = "latitude", column = @Column(name = "destination_longitude"))
    @AttributeOverride(name = "longitude", column = @Column(name = "destination_latitude"))
    private Location arrivalLocation;
    @Embedded
    @AttributeOverride(name = "start", column = @Column(name = "departure_time_start"))
    @AttributeOverride(name = "end", column = @Column(name = "departure_time_end"))
    private TimeSpan plannedDepartureTime;
    private int requiredSeats;
}
