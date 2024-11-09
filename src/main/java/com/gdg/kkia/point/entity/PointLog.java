package com.gdg.kkia.point.entity;

import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PointLog.Type type;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PointLog.Status status;
    @CreatedDate
    @NotNull
    private LocalDateTime receivedDatetime;
    @NotNull
    private LocalDate receivedDate;
    @NotNull
    private int receivedPoint;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public PointLog(Type type, Status status, Member member, int receivedPoint) {
        this.type = type;
        this.status = status;
        this.member = member;
        this.receivedPoint = receivedPoint;
    }

    @PrePersist
    public void convertToReceivedDate() {
        this.receivedDate = this.receivedDatetime.toLocalDate();
    }

    public enum Type {
        ATTENDANCE,
        DAILYRESPONSE,
        DIARY,
        PET_GROWTH

    }

    public enum Status {
        EARNED,
        CONSUMED

    }
}
