package com.gdg.kkia.storage.diary.repository;

import com.gdg.kkia.storage.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
