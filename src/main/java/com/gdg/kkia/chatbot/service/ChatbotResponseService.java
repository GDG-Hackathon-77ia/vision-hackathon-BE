package com.gdg.kkia.chatbot.service;

import com.gdg.kkia.chatbot.dto.ChatRequest;
import com.gdg.kkia.chatbot.repository.ChatbotResponseRepository;
import com.gdg.kkia.common.exception.BadRequestException;
import com.gdg.kkia.common.exception.NotFoundException;
import com.gdg.kkia.chatbot.entity.GeminiRequestType;
import com.gdg.kkia.member.entity.Member;
import com.gdg.kkia.member.repository.MemberRepository;
import com.gdg.kkia.chatbot.entity.ChatbotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotResponseService {

    private final ChatbotResponseRepository chatbotResponseRepository;
    private final MemberRepository memberRepository;

    public void saveChatbotResponses(Long memberId, GeminiRequestType type, List<ChatRequest> conversations) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("memberId에 해당하는 멤버가 없습니다."));

        List<ChatbotResponse> responses = new ArrayList<>();

        for (ChatRequest chat : conversations) {
            // check validation
            if (chat.getQuestion() == null) throw new BadRequestException("Question 항목이 null입니다.");
            if (chat.getResponse() == null) throw new BadRequestException("Response 항목이 null입니다.");
            if (chat.getResponseDateTime() == null) throw new BadRequestException("ResponseDateTime 항목이 null입니다.");

            ChatbotResponse chatbotResponse = new ChatbotResponse();
            chatbotResponse.setQuestion(chat.getQuestion());
            chatbotResponse.setResponse(chat.getResponse());
            chatbotResponse.setResponseDateTime(chat.getResponseDateTime());
            chatbotResponse.setType(type);
            chatbotResponse.setMember(member);

            responses.add(chatbotResponse);


        }

        chatbotResponseRepository.saveAll(responses);
    }

}
