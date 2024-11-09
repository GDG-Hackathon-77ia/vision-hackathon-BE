package com.gdg.kkia.storage.chatbot.entity;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.point.entity.PointLog;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatbotResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String question;
    @NotNull
    private String response;
    @NotNull
    @CreatedDate
    private LocalDateTime responseDateTime;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PointLog.Type type;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public enum Type {
        DIARY,
        CHAT
    }

}
