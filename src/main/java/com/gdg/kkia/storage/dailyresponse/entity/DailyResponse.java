package com.gdg.kkia.storage.dailyresponse.entity;

import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @CreatedDate
    private LocalDateTime responseDateTime;
    @NotNull
    private String response;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @OneToOne
    @NotNull
    @JoinColumn(name = "daily_question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DailyQuestion dailyQuestion;
}
