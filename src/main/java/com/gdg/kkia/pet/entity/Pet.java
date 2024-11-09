package com.gdg.kkia.pet.entity;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
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
    private final int MAX_EXPERIENCE = 100;
    private final int NORMAL_PLUS = 1;
    private final int PREMIUM_PLUS = 12;
    private final int SUPER_PLUS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
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
        this.name = name;
        this.member = member;
        this.level = INITIAL_LEVEL;
        this.experience = INITIAL_EXPERIENCE;
        isMaxGrowth = false;
    }

    public static int convertByGrowthButton(GrowthButton growthButton, int normalPlus, int premiumPlus, int superPlus) {
        switch (growthButton) {
            case NORMAL -> {
                return normalPlus;
            }
            case PREMIUM -> {
                return premiumPlus;
            }
            case SUPER -> {
                return superPlus;
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

    public boolean checkMemberIsCorrect(Member member) {
        return this.member.equals(member);
    }

    public int earnExperience(GrowthButton growthButton) {
        int plusExperience = experienceToEarn(growthButton);

        if (this.experience + plusExperience >= MAX_EXPERIENCE) {
            if (this.level < MAX_LEVEL) {
                levelUP();
                this.experience = (this.experience + plusExperience) - MAX_EXPERIENCE;
            } else {
                this.experience = MAX_EXPERIENCE;
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
        return convertByGrowthButton(growthButton, NORMAL_PLUS, PREMIUM_PLUS, SUPER_PLUS);
    }

    public enum GrowthButton {
        NORMAL,
        PREMIUM,
        SUPER
    }

}
