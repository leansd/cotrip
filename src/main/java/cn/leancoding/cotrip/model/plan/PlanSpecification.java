package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.base.model.ValueObject;
import cn.leancoding.cotrip.model.location.Location;
import cn.leancoding.cotrip.model.location.LocationConverter;
import cn.leancoding.cotrip.service.PlanSpecificationDTO;
import cn.leancoding.cotrip.service.TimeSpanDTO;
import cn.leancoding.cotrip.service.TripPlanDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Data
public class PlanSpecification extends ValueObject {

    public PlanSpecification(Location departureLocation, Location arrivalLocation, TimeSpanDTO plannedDepartureTime) {
        this(departureLocation, arrivalLocation, plannedDepartureTime,  1);
    }

    public PlanSpecification(Location departureLocation, Location arrivalLocation, TimeSpanDTO plannedDepartureTime, int requiredSeats) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.plannedDepartureTime = TimeSpanConverter.toEntity(plannedDepartureTime);
        this.requiredSeats = requiredSeats;
    }

    public PlanSpecification(PlanSpecificationDTO dto) {
        super();
        this.departureLocation = LocationConverter.toEntity(dto.getDepartureLocation());
        this.arrivalLocation = LocationConverter.toEntity(dto.getArrivalLocation());
        this.plannedDepartureTime = TimeSpanConverter.toEntity(dto.getPlannedDepartureTime());
        this.requiredSeats = (dto.getRequiredSeats()!=null)?dto.getRequiredSeats():1;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanSpecification that = (PlanSpecification) o;
        if (requiredSeats != that.requiredSeats) return false;
        if (!Objects.equals(departureLocation, that.departureLocation))
            return false;
        if (!Objects.equals(arrivalLocation, that.arrivalLocation))
            return false;
        return Objects.equals(plannedDepartureTime, that.plannedDepartureTime);
    }

    @Override
    public int hashCode() {
        int result = departureLocation != null ? departureLocation.hashCode() : 0;
        result = 31 * result + (arrivalLocation != null ? arrivalLocation.hashCode() : 0);
        result = 31 * result + (plannedDepartureTime != null ? plannedDepartureTime.hashCode() : 0);
        result = 31 * result + requiredSeats;
        return result;
    }
}
