package cn.leancoding.cotrip.model.location;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
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
