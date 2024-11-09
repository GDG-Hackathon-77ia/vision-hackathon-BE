package com.gdg.kkia.storage.chatbot.repository;

import com.gdg.kkia.storage.chatbot.entity.ChatbotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {
}