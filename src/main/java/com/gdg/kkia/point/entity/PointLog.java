package com.gdg.kkia.point.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PointLog.Type type;
    @CreatedDate
    @NotNull
    private LocalDateTime recievedDatetime;

    public enum Type {
        ATTENDANCE,
        DAILYRESPONSE,
        DIARY

    }
}
