package cn.leancoding.cotrip.model.plan;

import cn.leancoding.cotrip.base.ValueObject;
import cn.leancoding.cotrip.model.location.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RouteSpecification  extends ValueObject {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteSpecification that = (RouteSpecification) o;

        if (flexibleWaitTime != that.flexibleWaitTime) return false;
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
        result = 31 * result + flexibleWaitTime;
        return result;
    }
}
