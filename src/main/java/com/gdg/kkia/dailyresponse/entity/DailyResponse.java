package com.gdg.kkia.dailyresponse.entity;

import com.gdg.kkia.common.exception.EmptyFieldException;
import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
public class DailyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @CreatedDate
    private LocalDateTime responseDateTime;
    @NotNull
    private LocalDate responseDate;
    @NotNull
    @NotEmpty
    private String response;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "daily_question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DailyQuestion dailyQuestion;

    public DailyResponse(String response, Member member, DailyQuestion dailyQuestion) {
        if (response.isBlank() || response.isEmpty()) {
            throw new EmptyFieldException("비어있을 수 없습니다.");
        }
        this.response = response;
        this.member = member;
        this.dailyQuestion = dailyQuestion;
    }

    @PrePersist
    public void convertToReceivedDate() {
        this.responseDate = this.responseDateTime.toLocalDate();
    }

    public boolean checkMember(Member member) {
        return this.member.equals(member);
    }
}
