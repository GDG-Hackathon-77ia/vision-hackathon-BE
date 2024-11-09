package com.gdg.kkia.pet.repository;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByMember(Member member);

    boolean existsByMember(Member member);

}
