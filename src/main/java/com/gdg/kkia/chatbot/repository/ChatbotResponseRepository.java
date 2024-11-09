package com.gdg.kkia.chatbot.repository;

import com.gdg.kkia.chatbot.dto.ChatbotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {
}