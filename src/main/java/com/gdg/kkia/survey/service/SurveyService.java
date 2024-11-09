package com.gdg.kkia.survey.service;

import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.survey.dto.SurveyRequest;
import com.gdg.kkia.survey.dto.SurveyResponse;
import com.gdg.kkia.survey.entity.Survey;
import com.gdg.kkia.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveSurveyAnswerWrittenByUser(Long memberId, SurveyRequest surveyRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        Survey survey = new Survey(surveyRequest.Answer(), Survey.Role.USER, member);
        surveyRepository.save(survey);
    }

    @Transactional(readOnly = true)
    public List<SurveyResponse> getAllSurveyAnswerWrittenByUser(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        return surveyRepository.findAllByMemberAndRole(member, Survey.Role.USER)
                .stream()
                .map(Survey -> new SurveyResponse(
                        Survey.getId(),
                        Survey.getSurveyedDatetime(),
                        Survey.getAnswer()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveSurveyAnswerWrittenByModel(List<Integer> answer, Member member) {
        Survey survey = new Survey(answer, Survey.Role.MODEL, member);
        surveyRepository.save(survey);
    }

    @Transactional(readOnly = true)
    public SurveyResponse getMostRecentlyWrittenSurveyWrittenByModel(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        Survey survey = surveyRepository.findTopByMemberAndRoleOrderBySurveyedDatetimeDesc(member, Survey.Role.MODEL)
                .orElseThrow(() -> new NotFoundException("작성된 survey가 없습니다."));

        return new SurveyResponse(survey.getId(), survey.getSurveyedDatetime(), survey.getAnswer());
    }

    @Transactional(readOnly = true)
    public LocalDateTime getMostRecentlyWrittenSurveyDateTimeWrittenByModel(Member member) {
        Survey survey = surveyRepository.findTopByMemberAndRoleOrderBySurveyedDatetimeDesc(member, Survey.Role.MODEL)
                .orElse(null);
        if (survey == null) {
            return null;
        }
        return survey.getSurveyedDatetime();
    }
}
