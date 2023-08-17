package cn.leancoding.cotrip.service;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TimeSpanDTO {
    LocalDateTime start;
    LocalDateTime end;
}
