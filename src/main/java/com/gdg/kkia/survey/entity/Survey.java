package com.gdg.kkia.survey.entity;

import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ElementCollection
    @CollectionTable(name = "survey_answers", joinColumns = @JoinColumn(name = "survey_id"))
    @Column(name = "answer")
    private List<Integer> answer = new ArrayList<>();
    @CreatedDate
    private LocalDateTime surveyedDatetime;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Survey.Role role;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public Survey(List<Integer> answer, Role role, Member member) {
        this.answer = answer;
        this.role = role;
        this.member = member;
    }

    public enum Role {
        USER,
        MODEL
    }
}
