package cn.leancoding.cotrip.service;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class PlanSpecificationDTO {
    private LocationDTO departureLocation;
    private LocationDTO arrivalLocation;
    private TimeSpanDTO plannedDepartureTime;
    private Integer requiredSeats;
}
