package com.gdg.kkia.member.controller;

import com.gdg.kkia.auth.dto.TokenResponse;
import com.gdg.kkia.member.dto.LoginRequest;
import com.gdg.kkia.member.dto.MemberInfoResponse;
import com.gdg.kkia.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "회원 정보", description = "회원 정보 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "임시 로그인", description = "카카오 연결 전까지 임시로 사용할 로그인입니다.", security = @SecurityRequirement(name = "JWT제외"))
    @PostMapping("/temp/login")
    public ResponseEntity<TokenResponse> tempLogin(@RequestBody LoginRequest request) {
        TokenResponse registerResponse = memberService.tempLogin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @Operation(summary = "멤버 정보조회", description = "로그인한 사용자의 이름, 이메일을 조회합니다.")
    @GetMapping("/members")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@RequestAttribute("memberId") Long memberId) {
        MemberInfoResponse memberInfoResponse = memberService.readMemberInfo(memberId);
        return ResponseEntity.ok().body(memberInfoResponse);
    }

    @Operation(summary = "멤버 회원탈퇴", description = "회원 정보를 삭제합니다.")
    @DeleteMapping("/members/withdrawal")
    public ResponseEntity<Void> deleteMember(@RequestAttribute("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }
}
