package com.gdg.kkia.member.entity;

import com.gdg.kkia.common.exception.BadRequestException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    public static final int INITIAL_POINT = 3000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private int point;

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
        this.point = INITIAL_POINT;
    }

    public void earnPoint(int point) {
        this.point += point;
    }

    public void consumePoint(int point) {
        if (this.point - point < 0) {
            throw new BadRequestException("보유 포인트보다 많은 포인트를 소비할 수 없습니다.");
        }
        this.point -= point;
    }
}
