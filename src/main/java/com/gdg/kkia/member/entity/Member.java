package com.gdg.kkia.member.entity;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.pet.entity.Pet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.gdg.kkia.pet.entity.Pet.convertByGrowthButton;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    public static final int INITIAL_POINT = 30000;
    public static final int NORMAL_BUTTON_PRICE = 100;
    public static final int PREMIUM_BUTTON_PRICE = 1000;
    public static final int SUPER_BUTTON_PRICE = 2000;


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

    public int consumePoint(Pet.GrowthButton growthButton) {
        int pointToConsume = pointToConsume(growthButton);
        if (this.point - pointToConsume < 0) {
            throw new BadRequestException("보유 포인트보다 많은 포인트를 소비할 수 없습니다.");
        }
        this.point -= pointToConsume;
        return pointToConsume;
    }

    private int pointToConsume(Pet.GrowthButton growthButton) {
        return convertByGrowthButton(growthButton, NORMAL_BUTTON_PRICE, PREMIUM_BUTTON_PRICE, SUPER_BUTTON_PRICE);
    }
}
