package com.gdg.kkia.member.entity;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.pet.dto.PointAndPointLogType;
import com.gdg.kkia.pet.entity.Pet;
import com.gdg.kkia.point.entity.PointLog;
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

    public static final int INITIAL_POINT = 100;
    public static final int NORMAL_BUTTON_PRICE = 10;
    public static final int PREMIUM_BUTTON_PRICE = 15;
    public static final int SUPER_BUTTON_PRICE = 20;


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

    public PointAndPointLogType consumePoint(Pet.GrowthButton growthButton) {
        int pointToConsume = pointToConsume(growthButton);
        if (this.point - pointToConsume < 0) {
            throw new BadRequestException("보유 포인트보다 많은 포인트를 소비할 수 없습니다.");
        }
        this.point -= pointToConsume;
        PointLog.Type pointLogType = convertGrowthButtonToPointLogType(growthButton);
        return new PointAndPointLogType(pointToConsume, pointLogType);
    }

    private int pointToConsume(Pet.GrowthButton growthButton) {
        return convertByGrowthButton(growthButton, NORMAL_BUTTON_PRICE, PREMIUM_BUTTON_PRICE, SUPER_BUTTON_PRICE);
    }

    private PointLog.Type convertGrowthButtonToPointLogType(Pet.GrowthButton growthButton) {
        switch (growthButton) {
            case WATER -> {
                return PointLog.Type.GROWTH_WATER;
            }
            case SUN -> {
                return PointLog.Type.GROWTH_SUN;
            }
            case NUTRIENT -> {
                return PointLog.Type.GROWTH_NUTRIENT;
            }
            default -> throw new BadRequestException("팻 성장 버튼 이름이 올바르지 않습니다.");
        }
    }
}
