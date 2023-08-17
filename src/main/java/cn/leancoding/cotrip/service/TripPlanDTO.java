package cn.leancoding.cotrip.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanDTO {
    private LocationDTO departureLocation;
    private LocationDTO arrivalLocation;
    private TimeSpanDTO plannedDepartureTime;
}
