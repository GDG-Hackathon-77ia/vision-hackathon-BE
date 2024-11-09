package com.gdg.kkia.pet.controller;

import com.gdg.kkia.common.dto.StringTypeMessageResponse;
import com.gdg.kkia.pet.dto.EarnExperienceResponse;
import com.gdg.kkia.pet.dto.GetPetRequest;
import com.gdg.kkia.pet.dto.PetInfoResponse;
import com.gdg.kkia.pet.dto.PetNameRequest;
import com.gdg.kkia.pet.entity.Pet;
import com.gdg.kkia.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
@Tag(name = "팻(캐릭터)", description = "팻(캐릭터) 관련 API")
public class PetController {

    private final PetService petService;

    @Operation(summary = "팻 획득", description = "사용자가 팻을 선정하여 획득합니다.")
    @PostMapping
    public ResponseEntity<StringTypeMessageResponse> getPet(@RequestAttribute("memberId") Long memberId, @RequestBody GetPetRequest getPetRequest) {
        petService.getPet(memberId, getPetRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringTypeMessageResponse("팻 획득이 완료되었습니다."));
    }

    @Operation(summary = "팻 이름 변경", description = "사용자가 팻의 이름을 변경합니다.")
    @PutMapping
    public ResponseEntity<StringTypeMessageResponse> updatePetName(@RequestAttribute("memberId") Long memberId, @RequestBody PetNameRequest petNameRequest) {
        petService.updatePetName(memberId, petNameRequest);
        return ResponseEntity.ok().body(new StringTypeMessageResponse("팻 이름 변경이 완료되었습니다."));
    }

    @Operation(summary = "팻 성장 버튼 구매", description = "사용자가 포인트를 사용하여 팻 성장 버튼을 구매합니다(즉시사용됨)")
    @PostMapping("/{growthButton}")
    public ResponseEntity<EarnExperienceResponse> buyAndUseGrowthButton(@RequestAttribute("memberId") Long memberId, @PathVariable("growthButton") Pet.GrowthButton growthButton) {
        EarnExperienceResponse earnExperienceResponse = new EarnExperienceResponse(petService.earnExperience(memberId, growthButton));
        return ResponseEntity.ok().body(earnExperienceResponse);
    }

    @Operation(summary = "팻 정보 조회", description = "팻의 이름, 레벨, 경험치를 조회합니다.")
    @GetMapping
    public ResponseEntity<PetInfoResponse> getPetInfo(@RequestAttribute("memberId") Long memberId) {
        PetInfoResponse petInfoResponse = petService.getPetInfo(memberId);
        return ResponseEntity.ok().body(petInfoResponse);
    }
}
