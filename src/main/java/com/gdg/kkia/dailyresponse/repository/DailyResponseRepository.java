package com.gdg.kkia.dailyresponse.repository;

import com.gdg.kkia.dailyresponse.entity.DailyResponse;
import com.gdg.kkia.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyResponseRepository extends JpaRepository<DailyResponse, Long> {

    List<DailyResponse> findAllByMemberAndResponseDate(Member member, LocalDate localDate);

    Optional<DailyResponse> findByMemberId(Long memberId);

    boolean existsByMemberIdAndResponseDate(Long memberId, LocalDate localDate);

}
