package com.gdg.kkia.storage.chatbot.entity;

import com.gdg.kkia.point.entity.PointLog;
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


    public enum Type {
        DIARY,
        CHAT
    }

}
