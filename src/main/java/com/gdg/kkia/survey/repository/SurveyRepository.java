package com.gdg.kkia.survey.repository;

import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    List<Survey> findAllByMemberAndRole(Member member, Survey.Role role);

    Optional<Survey> findTopByMemberAndRoleOrderBySurveyedDatetimeDesc(Member member, Survey.Role role);

}
