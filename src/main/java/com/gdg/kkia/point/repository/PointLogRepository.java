package com.gdg.kkia.point.repository;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.point.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long> {

    List<PointLog> findByMemberOrderByReceivedDateDesc(Member member);

    boolean existsByReceivedDateAndMemberAndType(LocalDate receivedDate, Member member, PointLog.Type type);

}
