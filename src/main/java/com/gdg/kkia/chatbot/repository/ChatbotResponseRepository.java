package com.gdg.kkia.chatbot.repository;

import com.gdg.kkia.chatbot.entity.ChatbotResponse;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {

    List<ChatbotResponse> findByMemberAndTypeAndResponseDateTimeBetween(
            Member member,
            GeminiRequestType type,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}