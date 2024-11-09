package com.gdg.kkia.pet.dto;

public record PetInfoResponse(
        String name,
        int level,
        int experience
) {
}
