package com.gdg.kkia.dailyresponse.service;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.common.exception.UnauthorizedException;
import com.gdg.kkia.dailyresponse.dto.DailyResponseRequest;
import com.gdg.kkia.dailyresponse.dto.DailyResponseResponse;
import com.gdg.kkia.dailyresponse.entity.DailyQuestion;
import com.gdg.kkia.dailyresponse.entity.DailyResponse;
import com.gdg.kkia.dailyresponse.repository.DailyQuestionRepository;
import com.gdg.kkia.dailyresponse.repository.DailyResponseRepository;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.point.service.PointLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyResponseService {

    private final DailyResponseRepository dailyResponseRepository;
    private final DailyQuestionRepository dailyQuestionRepository;
    private final MemberRepository memberRepository;
    private final PointLogService pointLogService;

    public void saveResponseOfDailyQuestion(Long memberId, DailyResponseRequest dailyResponseRequest) {
        if (dailyResponseRepository.existsByMemberIdAndResponseDate(memberId, LocalDate.now())) {
            throw new BadRequestException("이미 오늘 응답을 했습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyResponseRequest.questionId())
                .orElseThrow(() -> new NotFoundException("questionId에 해당하는 질문이 없습니다."));
        DailyResponse dailyResponse = new DailyResponse(dailyResponseRequest.response(), member, dailyQuestion);
        dailyResponseRepository.save(dailyResponse);

        pointLogService.earnResponseDailyQuestionPointPerDay(member);
    }

    public DailyResponseResponse getOneResponseOfDailyQuestion(Long memberId, Long dailyResponseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        DailyResponse dailyResponse = dailyResponseRepository.findById(dailyResponseId)
                .orElseThrow(() -> new NotFoundException("responseId에 해당하는 답변이 없습니다."));

        if (!dailyResponse.checkMember(member)) {
            throw new UnauthorizedException("답변을 조회할 권한이 없습니다. 본인이 쓴 답변만 조회 가능합니다.");
        }

        return new DailyResponseResponse(dailyResponse.getId(), dailyResponse.getDailyQuestion().getQuestion(), dailyResponse.getResponse());
    }

    public List<DailyResponseResponse> getAllResponseOfDailyQuestionInLocalDate(Long memberId, LocalDate localDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        return dailyResponseRepository.findAllByMemberAndResponseDate(member, localDate)
                .stream()
                .map(DailyResponse -> new DailyResponseResponse(
                        DailyResponse.getId(),
                        DailyResponse.getDailyQuestion().getQuestion(),
                        DailyResponse.getResponse()))
                .collect(Collectors.toList());
    }

    public void deleteResponseOfDailyQuestion(Long memberId, Long dailyResponseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        DailyResponse dailyResponse = dailyResponseRepository.findById(dailyResponseId)
                .orElseThrow(() -> new NotFoundException("responseId에 해당하는 답변이 없습니다."));

        if (!dailyResponse.checkMember(member)) {
            throw new UnauthorizedException("답변을 삭제할 권한이 없습니다. 본인이 쓴 답변만 삭제 가능합니다.");
        }

        dailyResponseRepository.deleteById(dailyResponseId);
    }
}
