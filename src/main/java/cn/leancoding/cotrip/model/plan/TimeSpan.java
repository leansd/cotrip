package cn.leancoding.cotrip.model.plan;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class TimeSpan {
    LocalDateTime start;
    LocalDateTime end;
}
