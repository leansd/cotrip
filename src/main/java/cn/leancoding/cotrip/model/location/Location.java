package cn.leancoding.cotrip.model.location;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Location {
    private double latitude;
    private double longitude;
}
