package com.gdg.kkia.memo.service;

import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.common.exception.UnauthorizedException;
import com.gdg.kkia.memo.dto.MemoRequest;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.memo.dto.MemoResponse;
import com.gdg.kkia.memo.entity.Memo;
import com.gdg.kkia.memo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemoService {

    private final MemberRepository memberRepository;
    private final MemoRepository memoRepository;

    @Transactional
    public void writeMemo(Long memberId, MemoRequest memoRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Memo memo = new Memo(memoRequest.content(), member);

        memoRepository.save(memo);
    }
    
    @Transactional(readOnly = true)
    public List<MemoResponse> getAllMemoByLocalDateTime(Long memberId, LocalDateTime localDateTime) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        LocalDateTime start = localDateTime.toLocalDate().atStartOfDay();
        LocalDateTime end = localDateTime.toLocalDate().atTime(23, 59, 59, 999999999);
        return memoRepository.findByMemberAndWrittenDatetimeBetween(member, start, end)
                .stream()
                .map(memo -> new MemoResponse(
                        memo.getId(),
                        memo.getWrittenDatetime(),
                        memo.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMemo(Long memberId, Long memoId, MemoRequest memoRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 메모가 없습니다."));

        if (!Objects.equals(member.getId(), memo.getMember().getId())) {
            throw new UnauthorizedException("로그인한 사용자가 작성한 메모가 아닙니다.");
        }

        memo.setContent(memoRequest.content());
        memo.setWrittenDatetime(LocalDateTime.now());

        memoRepository.save(memo);
    }

    @Transactional
    public void deleteMemo(Long memberId, Long memoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 멤버가 없습니다."));

        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new NotFoundException("id에 해당하는 메모가 없습니다."));

        if (!Objects.equals(member.getId(), memo.getMember().getId())) {
            throw new UnauthorizedException("로그인한 사용자가 작성한 메모가 아닙니다.");
        }

        memoRepository.deleteById(memoId);
    }
}
