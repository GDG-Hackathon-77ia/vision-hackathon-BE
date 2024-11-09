package com.gdg.kkia.diary.repository;

import com.gdg.kkia.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
