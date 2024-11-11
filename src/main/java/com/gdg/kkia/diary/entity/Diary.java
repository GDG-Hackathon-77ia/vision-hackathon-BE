package com.gdg.kkia.diary.entity;

import com.gdg.kkia.common.exception.BadRequestException;
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
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String content;
    @NotNull
    @CreatedDate
    private LocalDateTime writtenDatetime;
    @NotNull
    private LocalDate writtenDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Diary.Type type;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public Diary(Diary.Type type, String content, Member member) {
        this.type = type;
        this.content = content;
        this.member = member;
    }

    @PrePersist
    public void convertToReceivedDate() {
        this.writtenDate = this.writtenDatetime.toLocalDate();
    }

    public boolean checkMemberIsNotCorrect(Member member) {
        return !this.member.equals(member);
    }

    public void updateDiary(String content) {
        if (content.isEmpty() || content.isBlank()) {
            throw new BadRequestException("변경할 일기의 내용이 비어있을 수 없습니다.");
        }
        this.content = content;
    }

    public enum Type {
        DAY,
        EMOTION,
        MEMO
    }
}
