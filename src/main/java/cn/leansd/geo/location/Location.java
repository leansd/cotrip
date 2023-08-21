package cn.leansd.geo.location;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Location {
    private double latitude;
    private double longitude;
}
