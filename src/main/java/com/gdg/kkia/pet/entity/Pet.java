package com.gdg.kkia.pet.entity;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.EmptyFieldException;
import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    private final int INITIAL_LEVEL = 1;
    private final int INITIAL_EXPERIENCE = 0;
    private final int MAX_LEVEL = 3;
    private final int MAX_EXPERIENCE = 150;
    private final int WATER_PLUS = 10;
    private final int SUN_PLUS = 20;
    private final int NUTRIENT_PLUS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private int level;
    @NotNull
    private int experience;
    @NotNull
    private boolean isMaxGrowth;
    @OneToOne
    @NotNull
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public Pet(String name, Member member) {
        if (name.isBlank() || name.isEmpty()) {
            throw new EmptyFieldException("비어있을 수 없습니다.");
        }
        this.name = name;
        this.member = member;
        this.level = INITIAL_LEVEL;
        this.experience = INITIAL_EXPERIENCE;
        isMaxGrowth = false;
    }

    public static int convertByGrowthButton(GrowthButton growthButton, int waterPlus, int sunPlus, int nutrientPlus) {
        switch (growthButton) {
            case WATER -> {
                return waterPlus;
            }
            case SUN -> {
                return sunPlus;
            }
            case NUTRIENT -> {
                return nutrientPlus;
            }
            default -> {
                throw new BadRequestException("성장버튼 타입이 올바르지 않습니다.");
            }
        }
    }

    public void changePetName(String name) {
        if (name.isEmpty() || name.isBlank()) {
            throw new BadRequestException("팻 이름은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public int earnExperience(GrowthButton growthButton) {
        int plusExperience = experienceToEarn(growthButton);
        int nextLevelExperience = MAX_EXPERIENCE * this.level;

        if (this.experience + plusExperience >= nextLevelExperience) {
            if (this.level < MAX_LEVEL) {
                levelUP();
                this.experience = (this.experience + plusExperience) - nextLevelExperience;
            } else {
                this.experience = nextLevelExperience;
                this.level = MAX_LEVEL;
                this.isMaxGrowth = true;
            }
        } else {
            this.experience += plusExperience;
        }

        return plusExperience;
    }

    private void levelUP() {
        this.level += 1;
    }

    private int experienceToEarn(GrowthButton growthButton) {
        return convertByGrowthButton(growthButton, WATER_PLUS, SUN_PLUS, NUTRIENT_PLUS);
    }

    public enum GrowthButton {
        WATER,
        SUN,
        NUTRIENT
    }

}
