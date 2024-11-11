package com.gdg.kkia.diary.repository;

import com.gdg.kkia.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByMemberIdAndWrittenDate(Long memberId, LocalDate localDate);
}
