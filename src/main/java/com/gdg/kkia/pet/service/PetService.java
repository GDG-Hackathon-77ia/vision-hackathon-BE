package com.gdg.kkia.pet.service;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.NoPetException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.pet.dto.GetPetRequest;
import com.gdg.kkia.pet.dto.PetInfoResponse;
import com.gdg.kkia.pet.dto.PetNameRequest;
import com.gdg.kkia.pet.entity.Pet;
import com.gdg.kkia.pet.repository.PetRepository;
import com.gdg.kkia.point.service.PointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final PointLogService pointLogService;

    @Transactional
    public void getPet(Long memberId, GetPetRequest getPetRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        if (petRepository.existsByMember(member)) {
            throw new BadRequestException("한 멤버당 소유할 수 있는 팻의 수는 하나입니다.");
        }

        Pet newPet = new Pet(getPetRequest.name(), member);
        petRepository.save(newPet);
    }

    @Transactional
    public void updatePetName(Long memberId, PetNameRequest petNameRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        Pet pet = petRepository.findByMember(member)
                .orElseThrow(() -> new NoPetException("해당 멤버가 소유한 팻이 없습니다."));

        pet.changePetName(petNameRequest.name());
    }

    @Transactional
    public int earnExperience(Long memberId, Pet.GrowthButton growthButton) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        Pet pet = petRepository.findByMember(member)
                .orElseThrow(() -> new NoPetException("해당 멤버가 소유한 팻이 없습니다."));

        pointLogService.consumePointAndWriteLog(member, growthButton, pet.isMaxGrowth());
        return pet.earnExperience(growthButton);
    }

    @Transactional(readOnly = true)
    public PetInfoResponse getPetInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        Pet pet = petRepository.findByMember(member)
                .orElseThrow(() -> new NoPetException("해당 멤버가 소유한 팻이 없습니다."));

        return new PetInfoResponse(pet.getName(), pet.getLevel(), pet.getExperience());
    }
}
