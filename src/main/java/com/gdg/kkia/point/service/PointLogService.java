package com.gdg.kkia.point.service;

import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.pet.dto.PointAndPointLogType;
import com.gdg.kkia.pet.entity.Pet;
import com.gdg.kkia.point.dto.PointLogResponse;
import com.gdg.kkia.point.dto.PointResponse;
import com.gdg.kkia.point.entity.PointLog;
import com.gdg.kkia.point.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointLogService {

    private final static int ATTENDANCE_BASE_POINT = 100;
    private final static int ATTENDANCE_BONUS_2_DAYS = 50;
    private final static int ATTENDANCE_BONUS_3_DAYS = 100;
    private final static int ATTENDANCE_BONUS_4_DAYS = 150;
    private final static int ATTENDANCE_BONUS_5_DAYS = 200;
    private final static int DIARY_WRITE_POINT = 300;
    private final static int USING_CHAT_BOT_POINT = 200;
    private final static int DAILY_QUESTION_ANSWER_POINT = 200;
    private final static List<PointLog.Type> ATTENDANCE_LISTS = Arrays.asList(
            PointLog.Type.ATTENDANCE_DAY_1,
            PointLog.Type.ATTENDANCE_DAY_2,
            PointLog.Type.ATTENDANCE_DAY_3,
            PointLog.Type.ATTENDANCE_DAY_4,
            PointLog.Type.ATTENDANCE_DAY_5_OR_MORE
    );


    private final PointLogRepository pointLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<PointLogResponse> getPointLogList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        return pointLogRepository.findByMemberOrderByReceivedDateDesc(member)
                .stream()
                .map(pointLog -> new PointLogResponse(
                        pointLog.getReceivedDatetime(),
                        pointLog.getType(),
                        pointLog.getStatus(),
                        pointLog.getReceivedPoint()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PointResponse getMemberPoint(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버를 찾을 수 없습니다."));

        return new PointResponse(member.getPoint());
    }

    @Transactional
    public void earnAttendancePointPerDay(Member member) {
        LocalDate today = LocalDate.now();

        if (!pointLogRepository.existsByReceivedDateAndMemberAndTypeIn(today, member, ATTENDANCE_LISTS)) {
            int consecutiveDays = calculateConsecutiveAttendanceDays(member);

            int totalPoints = ATTENDANCE_BASE_POINT + calculateBonusPoints(consecutiveDays);

            PointLog.Type attendanceType = getAttendanceType(consecutiveDays);

            PointLog newPointLog = new PointLog(attendanceType, PointLog.Status.EARNED, member, totalPoints);
            pointLogRepository.save(newPointLog);

            member.earnPoint(totalPoints);
        }
    }

    private int calculateConsecutiveAttendanceDays(Member member) {
        LocalDate today = LocalDate.now();

        int consecutiveDays = 0;
        for (int i = 1; i <= 5; i++) {
            LocalDate dateToCheck = today.minusDays(i);
            if (pointLogRepository.existsByReceivedDateAndMemberAndTypeIn(dateToCheck, member, ATTENDANCE_LISTS)) {
                consecutiveDays++;
            } else {
                break;
            }
        }
        return consecutiveDays;
    }

    private int calculateBonusPoints(int consecutiveDays) {
        return switch (consecutiveDays) {
            case 2 -> ATTENDANCE_BONUS_2_DAYS;
            case 3 -> ATTENDANCE_BONUS_3_DAYS;
            case 4 -> ATTENDANCE_BONUS_4_DAYS;
            case 5 -> ATTENDANCE_BONUS_5_DAYS;
            default -> 0;
        };
    }

    private PointLog.Type getAttendanceType(int consecutiveDays) {
        return switch (consecutiveDays) {
            case 2 -> PointLog.Type.ATTENDANCE_DAY_2;
            case 3 -> PointLog.Type.ATTENDANCE_DAY_3;
            case 4 -> PointLog.Type.ATTENDANCE_DAY_4;
            case 5 -> PointLog.Type.ATTENDANCE_DAY_5_OR_MORE;
            default -> PointLog.Type.ATTENDANCE_DAY_1;
        };
    }

    @Transactional
    public void earnDiaryWritePointPerDay(Member member) {
        LocalDate today = LocalDate.now();

        if (!pointLogRepository.existsByReceivedDateAndMemberAndTypeIn(today, member, List.of(PointLog.Type.DIARY))) {
            PointLog newPointLog = new PointLog(PointLog.Type.DIARY, PointLog.Status.EARNED, member, DIARY_WRITE_POINT);
            pointLogRepository.save(newPointLog);

            member.earnPoint(DIARY_WRITE_POINT);
        }
    }

    @Transactional
    public void earnUsingChatBotPointPerDay(Member member) {
        LocalDate today = LocalDate.now();

        if (!pointLogRepository.existsByReceivedDateAndMemberAndTypeIn(today, member, List.of(PointLog.Type.CHAT_BOT))) {
            PointLog newPointLog = new PointLog(PointLog.Type.CHAT_BOT, PointLog.Status.EARNED, member, USING_CHAT_BOT_POINT);
            pointLogRepository.save(newPointLog);

            member.earnPoint(USING_CHAT_BOT_POINT);
        }
    }

    @Transactional
    public void earnResponseDailyQuestionPointPerDay(Member member) {
        LocalDate today = LocalDate.now();

        if (!pointLogRepository.existsByReceivedDateAndMemberAndTypeIn(today, member, List.of(PointLog.Type.DAILY_RESPONSE))) {
            PointLog newPointLog = new PointLog(PointLog.Type.DAILY_RESPONSE, PointLog.Status.EARNED, member, DAILY_QUESTION_ANSWER_POINT);
            pointLogRepository.save(newPointLog);

            member.earnPoint(DAILY_QUESTION_ANSWER_POINT);
        }
    }

    @Transactional
    public void consumePointAndWriteLog(Member member, Pet.GrowthButton growthButton, boolean isMaxGrowth) {
        if (isMaxGrowth) {
            throw new BadRequestException("최고레벨입니다.");
        }
        PointAndPointLogType pointAndButtonType = member.consumePoint(growthButton);
        PointLog newPointLog = new PointLog(pointAndButtonType.pointLogType(), PointLog.Status.CONSUMED, member, pointAndButtonType.point());
        pointLogRepository.save(newPointLog);
    }
}
