package com.gdg.kkia.storage.dailyresponse.repository;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.storage.dailyresponse.entity.DailyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyResponseRepository extends JpaRepository<DailyResponse, Long> {

    List<DailyResponse> findAllByMember(Member member);

}
