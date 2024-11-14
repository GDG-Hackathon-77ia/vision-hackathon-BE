package com.gdg.kkia.memo.repository;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.memo.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByMemberAndWrittenDatetimeBetween(
            Member member,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
